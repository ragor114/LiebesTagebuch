package ur.mi.liebestagebuch.Encryption;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class SecurePasswordSaver {


    /*
     * Diese Klasse stellt statische Methoden zur Verfügung um das Passwort sicher zu speichern
     * und als String wieder ab zu rufen.
     *
     * Entwickelt von Jannik Wiese.
     *
     * ACHTUNG: Noch nicht getestet (da Passwort-Screen noch nicht implementiert), kann Fehler
     * enthalten!
     *
     * TODO: Testen, verbessern, in Login-Screen integrieren.
     * TODO: Exceptions müssen besser gehandelt werden, wenn in Login-Screen integriert.
     *
     * QUELLEN:
     * https://developer.android.com/training/articles/keystore
     * https://www.youtube.com/watch?v=VeUhQvCObJY
     */

    private static final String SP_PASSWORD_KEY = "password";
    private static final String SP_IV_KEY = "encryptionIv";
    private static final String KEY_ALIAS = "mypaswordkey1";

    /*
     * Speichert das eingegebene Passwort im Android-Keystore (also sicher).
     */
    public static void storePasswordSecure(String passwordClearText, Context context){
        String encryptedPassword = encryptPassword(passwordClearText, context);

        saveStringInSharedPreference(context, SP_PASSWORD_KEY, encryptedPassword);
    }

    private static void saveStringInSharedPreference(Context context, String key, String value){
        SharedPreferences.Editor sharedPreferencesEditor = context.getSharedPreferences("SP", Activity.MODE_PRIVATE).edit();
        sharedPreferencesEditor.putString(key, value);
        sharedPreferencesEditor.apply();
    }

    private static String encryptPassword(String passwordClearText, Context context) {
        SecretKey secretKey = createKey();
        String encryptedPassword = "";
        try {
            Cipher cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            byte[] encryptionIv = cipher.getIV();
            saveStringInSharedPreference(context, SP_IV_KEY, Base64.encodeToString(encryptionIv, Base64.DEFAULT));
            Log.d("login", "passwordClearText is: " + passwordClearText);
            byte[] passwordBytes = passwordClearText.getBytes(EncryptionConfig.CHARSET_NAME);
            byte[] encryptedPasswordBytes = cipher.doFinal(passwordBytes);
            encryptedPassword = Base64.encodeToString(encryptedPasswordBytes, Base64.DEFAULT);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encryptedPassword;
    }

    /*
     * Passwort kann zum Vergleich mit der Nutzereingabe oder zum Verschlüsseln geladen werden.
     */
    public static String getStoredPassword(Context context){
        String passwordClearText = null;

        String encryptedPassword = getStringFromSharedPreference(context, SP_PASSWORD_KEY);
        String base64IvString = getStringFromSharedPreference(context, SP_IV_KEY);
        byte[] encryptedPasswordBytes = Base64.decode(encryptedPassword, Base64.DEFAULT);
        byte[] encryptionIv = Base64.decode(base64IvString, Base64.DEFAULT);

        passwordClearText = decryptPassword(encryptedPasswordBytes, encryptionIv);
        return passwordClearText;
    }

    private static String decryptPassword(byte[] encryptedPasswordBytes, byte[] encryptionIv) {
        String password = "";
        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            KeyStore.SecretKeyEntry secretKeyEntry = (KeyStore.SecretKeyEntry) keyStore.getEntry(KEY_ALIAS, null);
            SecretKey secretKey = secretKeyEntry.getSecretKey();
            Cipher cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(encryptionIv));
            byte[] passwordBytes = cipher.doFinal(encryptedPasswordBytes);
            password = new String(passwordBytes, EncryptionConfig.CHARSET_NAME);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (UnrecoverableEntryException e) {
            Log.d("Login", "Unrecoverable Entry");
            e.printStackTrace();
        }
        return password;
    }

    private static String getStringFromSharedPreference(Context context, String key){
        SharedPreferences sharedPreferences = context.getSharedPreferences("SP", Activity.MODE_PRIVATE);
        return sharedPreferences.getString(key, null);
    }

    private static SecretKey createKey(){
        SecretKey secretKey = null;
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
            keyGenerator.init(new KeyGenParameterSpec.Builder(KEY_ALIAS, KeyProperties.PURPOSE_DECRYPT | KeyProperties.PURPOSE_ENCRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(false)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            secretKey = keyGenerator.generateKey();
            Log.d("login", "Key: " + secretKey.toString());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return secretKey;
    }

}
