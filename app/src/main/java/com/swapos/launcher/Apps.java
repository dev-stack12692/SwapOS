package com.swapos.launcher;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
public class Apps {
    public static List<AppInfo> launchable(Context c) {
        PackageManager pm = c.getPackageManager();
        Intent i = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> rs = pm.queryIntentActivities(i, PackageManager.MATCH_ALL);
        List<AppInfo> out = new ArrayList<>();
        for (ResolveInfo r : rs) {
            if (r.activityInfo.packageName.equals(c.getPackageName())) continue;
            AppInfo a = new AppInfo();
            a.label = r.loadLabel(pm).toString();
            a.packageName = r.activityInfo.packageName;
            a.className = r.activityInfo.name;
            a.icon = r.loadIcon(pm);
            try { a.category = pm.getApplicationInfo(a.packageName, 0).category; } catch (Exception ignored) {}
            out.add(a);
        }
        Collections.sort(out, (x, y) -> x.label.compareToIgnoreCase(y.label));
        return out;
    }
    public static List<AppInfo> filter(List<AppInfo> all, String chip) {
        if (chip.equals("All")) return all;
        List<AppInfo> out = new ArrayList<>();
        for (AppInfo a : all) {
            boolean hit;
            switch (chip) {
                case "Communication": hit = a.category == ApplicationInfo.CATEGORY_SOCIAL; break;
                case "Productivity": hit = a.category == ApplicationInfo.CATEGORY_PRODUCTIVITY; break;
                case "Entertainment": hit = a.category == ApplicationInfo.CATEGORY_AUDIO
                        || a.category == ApplicationInfo.CATEGORY_VIDEO
                        || a.category == ApplicationInfo.CATEGORY_GAME
                        || a.category == ApplicationInfo.CATEGORY_IMAGE; break;
                default: hit = a.category == ApplicationInfo.CATEGORY_UNDEFINED
                        || a.category == ApplicationInfo.CATEGORY_MAPS
                        || a.category == ApplicationInfo.CATEGORY_NEWS; break;
            }
            if (hit) out.add(a);
        }
        return out;
    }
}
