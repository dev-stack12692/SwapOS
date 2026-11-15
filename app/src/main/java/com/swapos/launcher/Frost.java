package com.swapos.launcher;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.View;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
public class Frost {
    private static Bitmap blurred;
    public static synchronized void invalidate() { blurred = null; }
    @SuppressWarnings("deprecation")
    public static synchronized Bitmap blurredWall(Context c, int w, int h) {
        if (blurred != null && blurred.getWidth() == w && blurred.getHeight() == h) return blurred;
        boolean tablet = w > h;
        int sel = Prefs.wallpaper(c);
        Bitmap src = BitmapFactory.decodeResource(c.getResources(),
                tablet ? Wallpapers.tab(sel) : Wallpapers.phone(sel));
        float scale = Math.max(w / (float) src.getWidth(), h / (float) src.getHeight());
        Matrix m = new Matrix();
        m.setScale(scale, scale);
        m.postTranslate((w - src.getWidth() * scale) / 2f, (h - src.getHeight() * scale) / 2f);
        Bitmap crop = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        new android.graphics.Canvas(crop).drawBitmap(src, m, null);
        if (src != crop) src.recycle();
        Bitmap out = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        RenderScript rs = RenderScript.create(c);
        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        Allocation ain = Allocation.createFromBitmap(rs, crop);
        Allocation aout = Allocation.createFromBitmap(rs, out);
        blur.setRadius(22f); blur.setInput(ain); blur.forEach(aout);
        aout.copyTo(out); rs.destroy();
        blurred = out;
        return out;
    }
    public static void apply(View panel, View root, int wallRes, float radiusDp) {
        root.post(() -> { try {
            int rw = root.getWidth(), rh = root.getHeight();
            if (rw == 0 || rh == 0) return;
            Bitmap bw = blurredWall(root.getContext(), rw, rh);
            int[] pl = new int[2]; panel.getLocationOnScreen(pl);
            int[] rl = new int[2]; root.getLocationOnScreen(rl);
            int x = pl[0] - rl[0], y = pl[1] - rl[1];
            int pw = Math.min(panel.getWidth(), rw - x), ph = Math.min(panel.getHeight(), rh - y);
            if (pw <= 0 || ph <= 0) return;
            Bitmap piece = Bitmap.createBitmap(bw, Math.max(0, x), Math.max(0, y), pw, ph);
            RoundedBitmapDrawable rbd = RoundedBitmapDrawableFactory.create(panel.getResources(), piece);
            float d = panel.getResources().getDisplayMetrics().density;
            rbd.setCornerRadius(radiusDp * d);
            panel.setBackground(rbd);
        } catch (Throwable ignored) { /* no RenderScript: keep default glass background */ } });
    }
}
