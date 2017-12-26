package com.github.khangtran.realmencryptionhelperdemo;

import android.app.Application;

import com.github.tntkhang.realmencryptionhelper.RealmEncryptionHelper;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by KhangTran on 12/26/17.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        RealmEncryptionHelper realmEncryptionHelper = RealmEncryptionHelper.initHelper(this, getString(R.string.app_name));

        Realm.init(this);

        RealmConfiguration config;

        if (BuildConfig.DEBUG) {
            config = new RealmConfiguration.Builder()
                    .name("myrealm.realm")
                    .build();
        } else {
            config = new RealmConfiguration.Builder()
                    .name("myrealm.realm")
                    .encryptionKey(realmEncryptionHelper.getEncryptKey())
                    .build();
        }

        Realm.setDefaultConfiguration(config);
    }
}
