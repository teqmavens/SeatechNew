package teq.development.seatech;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.support.v7.app.AppCompatDelegate;

import com.facebook.drawee.backends.pipeline.Fresco;

public class App extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
