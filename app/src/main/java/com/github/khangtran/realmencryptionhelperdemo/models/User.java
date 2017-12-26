package com.github.khangtran.realmencryptionhelperdemo.models;

import io.realm.RealmObject;

/**
 * Created by KhangTran on 12/26/17.
 */

public class User extends RealmObject {

    private String name;


    public User() {}

    public User(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
