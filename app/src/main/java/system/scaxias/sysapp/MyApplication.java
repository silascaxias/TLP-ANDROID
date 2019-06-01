package system.scaxias.sysapp;

import android.app.Application;
import android.content.res.Configuration;


public class MyApplication extends Application {

    @Override
    public void onCreate(){
        super.onCreate();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
