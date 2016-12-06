package de.ruf2.rube.fridgeorganizer;

import android.app.Application;
import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Bernhard Ruf on 25.09.2016.
 */
public class FridgeApplication extends Application {
    private static FridgeApplication application;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        // The Realm file will be located in Context.getFilesDir() with name "default.realm"
        RealmConfiguration config = new RealmConfiguration.Builder(this)
                .name("myfridge.realm")
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
    }

    public static FridgeApplication getApplication(){
        return application;
    }

    public static Context getContext(){
        return application;
    }
}
