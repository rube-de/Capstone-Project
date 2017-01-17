package de.ruf2.rube.fridgeorganizer;

import android.app.Application;
import android.content.Context;

/**
 * Created by Bernhard Ruf on 25.09.2016.
 */
public class FridgeApplication extends Application {
    private static FridgeApplication application;


    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
    }

    public static FridgeApplication getApplication(){
        return application;
    }

    public static Context getContext(){
        return application;
    }

}
