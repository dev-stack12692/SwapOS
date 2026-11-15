package com.swapos.launcher;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.GraphicsMode;
import org.robolectric.shadows.Shadows;
import java.io.File;
import java.io.FileOutputStream;
import static org.junit.Assert.assertEquals;
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 34)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
public class RenderTest {
    private static void shot(Activity a, String name, int w, int h) throws Exception {
        View root = a.getWindow().getDecorView();
        root.measure(View.MeasureSpec.makeMeasureSpec(w, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(h, View.MeasureSpec.EXACTLY));
        root.layout(0, 0, w, h);
        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        root.draw(new Canvas(bmp));
        File dir = new File("build/render"); dir.mkdirs();
        FileOutputStream out = new FileOutputStream(new File(dir, name));
        bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
        out.close();
    }
    private static void addFixtures(Activity a) {
        String[] names = {"Fixture Phone", "Fixture Chat", "Fixture Browser", "Fixture Camera",
                "Fixture Video", "Fixture Music", "Fixture Files", "Fixture Store"};
        PackageManager pm = a.getPackageManager();
        Intent launcher = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER);
        for (int i = 0; i < names.length; i++) {
            ResolveInfo ri = new ResolveInfo();
            ActivityInfo ai = new ActivityInfo();
            ai.packageName = "com.fixture.app" + i;
            ai.name = "com.fixture.app" + i + ".Main";
            ai.nonLocalizedLabel = names[i];
            ri.activityInfo = ai;
            Shadows.shadowOf(pm).addResolveInfoForIntent(launcher, ri);
        }
    }
    @Test public void homePhone() throws Exception {
        Activity a = Robolectric.buildActivity(MainActivity.class).setup().get();
        shot(a, "home_phone.png", 1080, 2200);
    }
    @Test public void homePhoneFixtures() throws Exception {
        Activity a = Robolectric.buildActivity(MainActivity.class).create().get();
        addFixtures(a);
        Robolectric.buildActivity(MainActivity.class);
        shot(a, "home_phone_fixtures.png", 1080, 2200);
    }
    @Test @Config(sdk = 34, qualifiers = "land")
    public void homeTabletFixtures() throws Exception {
        Activity a = Robolectric.buildActivity(MainActivity.class).create().get();
        addFixtures(a);
        shot(a, "home_tablet_fixtures.png", 1920, 1200);
    }
    @Test public void settingsPickAndPersist() throws Exception {
        Activity s = Robolectric.buildActivity(SettingsActivity.class).setup().get();
        LinearLayout row = s.findViewById(R.id.wallRow);
        // pick "Forest" (index 1)
        row.getChildAt(1).performClick();
        assertEquals(1, Prefs.wallpaper(RuntimeEnvironment.getApplication()));
        // blur toggle flips and persists
        s.findViewById(R.id.rowBlur).performClick();
        assertEquals(false, Prefs.blur(RuntimeEnvironment.getApplication()));
        TextView state = s.findViewById(R.id.blurState);
        assertEquals("Off", state.getText().toString());
        // fresh SettingsActivity instance sees persisted values
        Activity s2 = Robolectric.buildActivity(SettingsActivity.class).setup().get();
        assertEquals("Off", ((TextView) s2.findViewById(R.id.blurState)).getText().toString());
        // home now shows Forest wallpaper
        Activity h = Robolectric.buildActivity(MainActivity.class).setup().get();
        shot(h, "home_forest.png", 1080, 2200);
        // restore defaults for other tests
        Prefs.setWallpaper(RuntimeEnvironment.getApplication(), 0);
        Prefs.setBlur(RuntimeEnvironment.getApplication(), true);
    }
}
