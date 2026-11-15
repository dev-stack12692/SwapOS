package com.swapos.launcher;
public class Wallpapers {
    public static final int COUNT = 3;
    public static final String[] NAMES = {"Sage Gold", "Forest", "Dune"};
    public static int phone(int i) {
        switch (i) {
            case 1: return R.drawable.wall_b_phone;
            case 2: return R.drawable.wall_c_phone;
            default: return R.drawable.wall_phone;
        }
    }
    public static int tab(int i) {
        switch (i) {
            case 1: return R.drawable.wall_b_tab;
            case 2: return R.drawable.wall_c_tab;
            default: return R.drawable.wall_tab;
        }
    }
    public static int forScreen(int i, boolean landscapeTablet) {
        return landscapeTablet ? tab(i) : phone(i);
    }
}
