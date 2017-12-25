package com.github.tntkhang.realmencryptionhelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.security.auth.x500.X500Principal;

import khangtran.preferenceshelper.PreferencesHelper;

/**
 * Created by KhangTran on 3/31/2017.
 */

public class RealmEncryptionHelper {
    private static RealmEncryptionHelper instance;
    private Context mContext;

    private static final boolean IS_M = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;

    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";
    private static final String RSA_MODE =  "RSA/ECB/PKCS1Padding";
    private static final String SHARED_PREFERENCE_NAME = "SHARED_PREFERENCE_NAME";
    private static final String ENCRYPTED_KEY = "ENCRYPTED_KEY";

    private String mKeyName = "KEY_NAME";

    private KeyStore keyStore;

    private PreferencesHelper mPrefsHelper;

    public static RealmEncryptionHelper getInstance(Context context, String keyName) {
        if (instance == null) {
            instance = new RealmEncryptionHelper(context, keyName);
        }
        return instance;
    }

    private RealmEncryptionHelper(Context context, String keyName) {
        this.mContext = context;
        this.mKeyName = keyName;
        mPrefsHelper = PreferencesHelper.initHelper(context);
    }

    @SuppressWarnings("NewApi")
    public byte[] getEncryptKey() {
        byte[] encryptedKey = new byte[64];
        try {
            keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
            keyStore.load(null);

            Log.i("tntkhang", "Not containsAlias: " + !keyStore.containsAlias(mKeyName));
            if (!keyStore.containsAlias(mContext.getString(R.string.app_name))) {
                // Create new key and save to KeyStore
                KeyPairGenerator kpg = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, ANDROID_KEY_STORE);
                if (IS_M) {
                    KeyGenParameterSpec spec = new KeyGenParameterSpec.Builder(mContext.getString(R.string.app_name),
                            KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                            .setRandomizedEncryptionRequired(false)
                            .build();

                    kpg.initialize(spec);
                } else {
                    // Generate a key pair for encryption
                    Calendar start = Calendar.getInstance();
                    Calendar end = Calendar.getInstance();
                    end.add(Calendar.YEAR, 30);
                    KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(mContext)
                            .setAlias(mContext.getString(R.string.app_name))
                            .setSubject(new X500Principal("CN=" + mContext.getString(R.string.app_name)))
                            .setSerialNumber(BigInteger.TEN)
                            .setStartDate(start.getTime())
                            .setEndDate(end.getTime())
                            .build();

                    kpg.initialize(spec);
                }
                kpg.generateKeyPair();
                encryptedKey = setSecretKey();
            } else {
                // Get key from KeyStore
                encryptedKey = getSecretKey();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i("tntkhang", "encryptedKey: " + encryptedKey.length + " -- " + Arrays.toString(encryptedKey));
        return encryptedKey;
    }

    private byte[] getSecretKey() {
        String encryptedKeyB64 = mPrefsHelper.getStringValue(ENCRYPTED_KEY, null);
        byte[] key = new byte[64];
        try {
            byte[] encryptedKey = Base64.decode(encryptedKeyB64, Base64.DEFAULT);
            key = rsaDecrypt(encryptedKey);

        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i("tntkhang", "getSecretKey string: " + encryptedKeyB64);

        Log.i("tntkhang", "getSecretKey key: " + Arrays.toString(key));
        return key;
    }

    private byte[] setSecretKey() {
        byte[] key = new byte[64];
        try {
            String encryptedKeyB64 = mPrefsHelper.getStringValue(ENCRYPTED_KEY, null);
            if (encryptedKeyB64 == null) {
                SecureRandom secureRandom = new SecureRandom();
                secureRandom.nextBytes(key);
                byte[] encryptedKey = rsaEncrypt(key);
                encryptedKeyB64 = Base64.encodeToString(encryptedKey, Base64.DEFAULT);
                mPrefsHelper.setValue(ENCRYPTED_KEY, encryptedKeyB64);
                Log.i("tntkhang", "setSecretKey string: " + encryptedKeyB64);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i("tntkhang", "setSecretKey key: " + Arrays.toString(key));
        return key;
    }

    private byte[] rsaEncrypt(byte[] secret) throws Exception {
        KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(mKeyName, null);
        Cipher inputCipher = Cipher.getInstance(RSA_MODE);
        inputCipher.init(Cipher.ENCRYPT_MODE, privateKeyEntry.getCertificate().getPublicKey());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, inputCipher);
        cipherOutputStream.write(secret);
        cipherOutputStream.close();

        return outputStream.toByteArray();
    }

    private byte[] rsaDecrypt(byte[] encrypted) throws Exception {
        KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(mKeyName, null);
        Cipher output = Cipher.getInstance(RSA_MODE);
        output.init(Cipher.DECRYPT_MODE, privateKeyEntry.getPrivateKey());
        CipherInputStream cipherInputStream = new CipherInputStream(
                new ByteArrayInputStream(encrypted), output);
        ArrayList<Byte> values = new ArrayList<>();
        int nextByte;
        while ((nextByte = cipherInputStream.read()) != -1) {
            values.add((byte)nextByte);
        }

        byte[] bytes = new byte[values.size()];
        for(int i = 0; i < bytes.length; i++) {
            bytes[i] = values.get(i);
        }
        return bytes;
    }
}
