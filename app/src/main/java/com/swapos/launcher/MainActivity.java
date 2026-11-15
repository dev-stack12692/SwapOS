package com.swapos.launcher;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
public class MainActivity extends AppCompatActivity {
    private final Handler clock = new Handler();
    private final Runnable tick = new Runnable() {
        public void run() { updateClock(); clock.postDelayed(this, 10000); }
    };
    private float downY = -1;
    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_home);
        updateClock(); clock.postDelayed(tick, 10000);
        fillFavorites();
        View root = findViewById(android.R.id.content);
        applyWallpaper();
        if (Prefs.blur(this)) {
            Frost.apply(findViewById(R.id.clockCard), root, R.drawable.wall_phone, 22);
            Frost.apply(findViewById(R.id.searchBar), root, R.drawable.wall_phone, 22);
        }
        findViewById(R.id.btnSettings).setOnClickListener(v ->
                startActivity(new Intent(this, SettingsActivity.class)));
        findViewById(R.id.searchBar).setOnClickListener(v -> openSearch());
        findViewById(R.id.clockCard).setOnClickListener(v -> openDrawer());
        root.setOnTouchListener((v, e) -> {
            if (e.getAction() == MotionEvent.ACTION_DOWN) downY = e.getY();
            else if (e.getAction() == MotionEvent.ACTION_UP && downY - e.getY() > 180) { openDrawer(); downY = -1; return true; }
            return false;
        });
    }
    private void applyWallpaper() {
        boolean land = getResources().getConfiguration().orientation
                == android.content.res.Configuration.ORIENTATION_LANDSCAPE;
        ((ImageView) findViewById(R.id.wallpaper)).setImageResource(
                Wallpapers.forScreen(Prefs.wallpaper(this), land));
    }
    private void updateClock() {
        Date now = new Date();
        ((TextView) findViewById(R.id.clockTime)).setText(new SimpleDateFormat("h:mm", Locale.getDefault()).format(now));
        ((TextView) findViewById(R.id.clockDate)).setText(new SimpleDateFormat("EEE, d MMM", Locale.getDefault()).format(now));
    }
    private void fillFavorites() {
        GridLayout g = findViewById(R.id.favGrid);
        List<AppInfo> all = Apps.launchable(this);
        String[] preferred = {"dialer", "phone", "messaging", "mms", "chrome", "camera", "youtube", "spotify", "whatsapp", "musicnest", "gmail", "photos", "gallery"};
        List<AppInfo> favs = new ArrayList<>();
        for (String key : preferred)
            for (AppInfo a : all)
                if (favs.size() < 8 && !favs.contains(a) && (a.packageName.toLowerCase().contains(key) || a.label.toLowerCase().contains(key)))
                    favs.add(a);
        for (AppInfo a : all) if (favs.size() < 8 && !favs.contains(a)) favs.add(a);
        float d = getResources().getDisplayMetrics().density;
        boolean land = getResources().getConfiguration().orientation
                == android.content.res.Configuration.ORIENTATION_LANDSCAPE;
        int cols = land ? favs.size() : 4;
        g.setColumnCount(cols);
        g.setRowCount((int) Math.ceil(favs.size() / (double) cols));
        int cell = getResources().getDisplayMetrics().widthPixels / cols;
        for (AppInfo a : favs) {
            GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
            lp.width = cell; lp.setGravity(Gravity.CENTER_HORIZONTAL);
            g.addView(appCell(this, a, cell, d), lp);
        }
    }
    private View appCell(Context c, AppInfo a, int cell, float d) {
        android.widget.LinearLayout col = new android.widget.LinearLayout(c);
        col.setOrientation(android.widget.LinearLayout.VERTICAL);
        col.setGravity(Gravity.CENTER_HORIZONTAL);
        int pad = (int)(12*d); col.setPadding(pad, pad, pad, pad);
        ImageView iv = new ImageView(c);
        int is = (int)(54*d);
        iv.setLayoutParams(new android.widget.LinearLayout.LayoutParams(is, is));
        iv.setImageDrawable(a.icon);
        TextView tv = new TextView(c);
        tv.setText(a.label); tv.setTextColor(0xFFFFFFFF); tv.setTextSize(12);
        tv.setSingleLine(true); tv.setGravity(Gravity.CENTER);
        tv.setShadowLayer(3, 0, 1, 0x90000000);
        tv.setMaxWidth((int)(76*d));
        tv.setEllipsize(android.text.TextUtils.TruncateAt.END);
        col.addView(iv); col.addView(tv);
        col.setOnClickListener(v -> {
            Intent i = new Intent(); i.setClassName(a.packageName, a.className);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); c.startActivity(i);
        });
        col.setOnLongClickListener(v -> {
            Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    android.net.Uri.parse("package:" + a.packageName));
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); c.startActivity(i); return true;
        });
        return col;
    }
    private void openSearch() {
        android.content.pm.PackageManager pm = getPackageManager();
        Intent i = pm.getLaunchIntentForPackage("com.google.android.googlequicksearchbox");
        if (i != null) startActivity(i); else openDrawer();
    }
    private void openDrawer() {
        startActivity(new Intent(this, AppDrawerActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
    @Override protected void onResume() { super.onResume(); updateClock(); applyWallpaper(); }
    @Override protected void onDestroy() { clock.removeCallbacks(tick); super.onDestroy(); }
    @Override public void onBackPressed() { /* launcher: stay on home */ }
}
