package com.swapos.launcher;
import android.content.Context;
import android.content.SharedPreferences;
public class Prefs {
    private static final String FILE = "swapos";
    public static int wallpaper(Context c) {
        return Math.max(0, Math.min(Wallpapers.COUNT - 1,
                c.getSharedPreferences(FILE, 0).getInt("wall", 0)));
    }
    public static void setWallpaper(Context c, int idx) {
        c.getSharedPreferences(FILE, 0).edit().putInt("wall", idx).apply();
        Frost.invalidate();
    }
    public static boolean blur(Context c) {
        return c.getSharedPreferences(FILE, 0).getBoolean("blur", true);
    }
    public static void setBlur(Context c, boolean on) {
        c.getSharedPreferences(FILE, 0).edit().putBoolean("blur", on).apply();
    }
}
