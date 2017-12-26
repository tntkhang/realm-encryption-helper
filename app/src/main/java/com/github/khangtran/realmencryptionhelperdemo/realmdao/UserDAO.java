package com.github.khangtran.realmencryptionhelperdemo.realmdao;

import com.github.khangtran.realmencryptionhelperdemo.models.User;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by KhangTran on 12/26/17.
 */

public class UserDAO {

    public static void saveUser(Realm realm, final User user) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.insertOrUpdate(user);
            }
        });
    }

    public static User getUser(Realm realm) {
        User result = new User();
        RealmResults<User> usersRealm = realm.where(User.class).findAll();
        if (usersRealm != null && usersRealm.size() > 0) {
            result = usersRealm.last();
        }
        return result;
    }

}
