package com.naren.kagga;

import android.app.Application;

import com.naren.kagga.db.DatabaseHelper;

/**
 * Created by narensmac on 15/03/18.
 */

public class KaggaApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        DatabaseHelper.copyFile(this);
    }
}
