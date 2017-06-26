package in.techmafiya.neversettlewallpaper.Permission;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * Created by saikiran on 6/20/17.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
    }
}