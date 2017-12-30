# realm-encryption-helper

[![Android Arsenal]( https://img.shields.io/badge/Android%20Arsenal-RealmEncryptionHelper-green.svg?style=flat )]( https://android-arsenal.com/details/1/6617 )

This is a lightweight library to generate a 64-bit random key and using **AES Algorithm** to encrypt/decrypt that random number to get a Secret Key and save it to Key Store.

This library especially supports for Realm Encryption. That means when using you don't have to care about how to get a random key, where to save that key for safe,... this library will take it.

**Support all android version**

# Setup and using

1. Add library to your build.gradle in app level
```
compile 'com.github.tntkhang:realm-encryption-helper:1.0.0'
```


2.In your CustomeApplication where you init your Realm

```  
// Config RealmEncryptionHelper
RealmEncryptionHelper realmEncryptionHelper = RealmEncryptionHelper.initHelper(this, getString(R.string.app_name));

// Config Realm
Realm.init(this);
RealmConfiguration config = new RealmConfiguration.Builder()
         .name("realm_encrypt.realm")
         .encryptionKey(realmEncryptionHelper.getEncryptKey()) // Call realmEncryptionHelper to get encrypt key
         .build();
 Realm.setDefaultConfiguration(config);
```

with this code
```
realmEncryptionHelper.getEncryptKey()
```

The library will check and generate a new key if your app doesn't contain the key in Key Store before and then save it to Key Store, otherwise, it will be got from Key Store.


# DEMO

Download and run project for simple demo


**That is, let's enjoy!**
