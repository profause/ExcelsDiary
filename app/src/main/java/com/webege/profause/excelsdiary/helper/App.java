package com.webege.profause.excelsdiary.helper;

import android.app.Application;
import android.os.Environment;

import java.io.File;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Emmanuel Mensah on 8/19/2016.
 */
public class App extends Application {
File f;
    public static final String dirPath=Environment.getExternalStorageDirectory()+File.separator+"ExcelsDiary";
    @Override
    public void onCreate() {
        super.onCreate();
        f=new File(Environment.getExternalStorageDirectory(),"ExcelsDiary");
        f.mkdir();

        RealmConfiguration configuration = new RealmConfiguration.Builder(this)
                .name("excelsDiary.realm")
                .schemaVersion(2)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(configuration);
    }
}
