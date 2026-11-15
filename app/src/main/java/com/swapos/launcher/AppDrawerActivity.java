package com.swapos.launcher;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
public class AppDrawerActivity extends AppCompatActivity {
    private AppAdapter adapter;
    private final int[] chipIds = {R.id.chipAll, R.id.chipComm, R.id.chipProd, R.id.chipEnt, R.id.chipTools};
    private final String[] chipNames = {"All", "Communication", "Productivity", "Entertainment", "Tools"};
    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_drawer);
        View root = findViewById(android.R.id.content);
        Frost.apply(findViewById(R.id.veil), root, R.drawable.wall_tab, 0);
        RecyclerView grid = findViewById(R.id.appGrid);
        int span = Math.max(4, getResources().getDisplayMetrics().widthPixels /
                (int)(getResources().getDisplayMetrics().density * 96));
        grid.setLayoutManager(new GridLayoutManager(this, span));
        adapter = new AppAdapter(Apps.launchable(this));
        grid.setAdapter(adapter);
        for (int i = 0; i < chipIds.length; i++) {
            final int idx = i;
            findViewById(chipIds[i]).setOnClickListener(v -> selectChip(idx));
        }
        findViewById(R.id.tabFiles).setOnClickListener(v -> openFiles());
        findViewById(R.id.tabSettings).setOnClickListener(v ->
                startActivity(new Intent(this, SettingsActivity.class)));
    }
    private void selectChip(int idx) {
        for (int i = 0; i < chipIds.length; i++) {
            TextView t = findViewById(chipIds[i]);
            boolean on = i == idx;
            t.setBackgroundResource(on ? R.drawable.chip_on_bg : R.drawable.chip_bg);
            t.setTextColor(on ? 0xFF1C1C1E : 0xFFFFFFFF);
        }
        adapter.setChip(chipNames[idx]);
    }
    private void openFiles() {
        PackageManager pm = getPackageManager();
        Intent i = pm.getLaunchIntentForPackage("com.google.android.documentsui");
        if (i == null) i = pm.getLaunchIntentForPackage("com.android.documentsui");
        if (i == null) i = new Intent(Intent.ACTION_VIEW).setType("*/*");
        if (i.resolveActivity(pm) != null) startActivity(i);
        else Toast.makeText(this, "No file manager found", Toast.LENGTH_SHORT).show();
    }
}
