package com.swapos.launcher;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
public class SettingsActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_settings);
        buildWallpaperRow();
        refreshBlurLabel();
        findViewById(R.id.rowBlur).setOnClickListener(v -> {
            Prefs.setBlur(this, !Prefs.blur(this));
            refreshBlurLabel();
        });
        findViewById(R.id.rowAndroidSettings).setOnClickListener(v ->
                startActivity(new Intent(Settings.ACTION_SETTINGS)));
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }
    private void refreshBlurLabel() {
        ((TextView) findViewById(R.id.blurState)).setText(Prefs.blur(this) ? "On" : "Off");
    }
    private void buildWallpaperRow() {
        LinearLayout row = findViewById(R.id.wallRow);
        row.removeAllViews();
        float d = getResources().getDisplayMetrics().density;
        int w = (int)(88*d), h = (int)(150*d), m = (int)(10*d);
        int sel = Prefs.wallpaper(this);
        for (int i = 0; i < Wallpapers.COUNT; i++) {
            final int idx = i;
            LinearLayout col = new LinearLayout(this);
            col.setOrientation(LinearLayout.VERTICAL);
            col.setGravity(Gravity.CENTER_HORIZONTAL);
            ImageView iv = new ImageView(this);
            LinearLayout.LayoutParams ip = new LinearLayout.LayoutParams(w, h);
            ip.setMargins(m, 0, m, 0);
            iv.setLayoutParams(ip);
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            iv.setImageResource(Wallpapers.phone(i));
            iv.setClipToOutline(true);
            iv.setBackgroundResource(R.drawable.glass_bg);
            if (i == sel) iv.setForeground(getResources().getDrawable(R.drawable.wall_sel_fg, getTheme()));
            TextView tv = new TextView(this);
            tv.setText(Wallpapers.NAMES[i]);
            tv.setTextColor(i == sel ? 0xFFFFFFFF : 0xCCFFFFFF);
            tv.setTextSize(12);
            tv.setPadding(0, (int)(6*d), 0, 0);
            col.addView(iv); col.addView(tv);
            col.setOnClickListener(v -> {
                Prefs.setWallpaper(this, idx);
                buildWallpaperRow();
            });
            row.addView(col);
        }
    }
}
