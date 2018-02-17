package anton.peer_id.audd;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Handler;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

public class Application extends android.app.Application {

    @SuppressLint("StaticFieldLeak")
    public static volatile Context context;
    public static volatile Handler handler;
    public static String version = "unknown", packageName = "unknown";
    public static int build_number = -1;
    public static String lang = "en";

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        Fresco.initialize(this);
        lang = System.getProperty("user.language");
        context = getApplicationContext();
        handler = new Handler(context.getMainLooper());
        try {
            packageName = getPackageName();
            PackageInfo packageInfo = getPackageManager().getPackageInfo(packageName, 0);
            version = packageInfo.versionName;
            build_number = packageInfo.versionCode;
        } catch (Throwable ignored) { }
    }

    public static void runOnUIThread(Runnable runnable) {
        runOnUIThread(runnable, 0);
    }

    public static void runOnUIThread(Runnable runnable, long delay) {
        if (delay == 0) {
            Application.handler.post(runnable);
        } else {
            Application.handler.postDelayed(runnable, delay);
        }
    }

    public static void cancelRunOnUIThread(Runnable runnable) {
        Application.handler.removeCallbacks(runnable);
    }


}
