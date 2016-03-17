package fi.benson.fleaapp.defaults;

import android.app.Application;

import com.backendless.Backendless;

/**
 * Created by bkamau on 3/16/16.
 */
public class Global extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Backendless.initApp(this, Defaults.APPLICATION_ID, Defaults.SECRET_KEY, Defaults.VERSION);

    }
}