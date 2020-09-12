package ur.mi.liebestagebuch.Encryption;

import android.os.Handler;
import android.util.Base64;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class AsyncDecryptor implements Runnable {

    /*
     * Klasse die hilft einen verschlüsselten String auf Basis des verschlüsselten, vom Nutzer
     * festgelegten Passworts zu entschlüsseln und den Listener den entschlüsselten String zu
     * übermitteln.
     * Als Entschlüsselungsalgorithmus wird AES genutzt.
     *
     * Entwickelt von Jannik Wiese.
     */

    // notwendige Attribute:
    private Handler mainThreadHandler;
    private CryptoListener listener;
    private String toDecrypt;
    private String encryptedPassword;
    private byte[] iv;
    private byte[] salt;

    // Die notwendigen Attribute werden über den Konstruktor gesetzt
    public AsyncDecryptor (Handler mainThreadHandler, CryptoListener listener, String toDecrypt, String encryptedPassword, byte[] iv, byte[] salt){
        this.mainThreadHandler = mainThreadHandler;
        this.listener = listener;
        this.toDecrypt = toDecrypt;
        this.encryptedPassword = encryptedPassword;
        this.iv = iv;
        this.salt = salt;
    }

    @Override
    public void run() {
        decrypt();
    }

    /*
     * Hauptmethode der Entschlüsselung.
     * Der verschlüsselte String wird in ein Byte-Array umgewandelt, das mithilfe eines
     * SecretKeySpecs, welches auf Basis des verschlüsselten Passworts generiert wird in ein ent-
     * schlüsseltes Byte-Array umgewandelt wird, dass dann in einen String umgewandelt wird, der dem
     * Listener übergeben wird.
     */
    private void decrypt() {
        Log.d(EncryptionConfig.LOG_TAG, "Decryption started");
        Log.d(EncryptionConfig.LOG_TAG, "ToDecrypt: " + toDecrypt);
        byte[] encryptedBytes = Base64.decode(toDecrypt, Base64.DEFAULT);
        Log.d(EncryptionConfig.LOG_TAG, "Encrypted Bytes: " + encryptedBytes);
        Log.d(EncryptionConfig.LOG_TAG, "IV: " + iv);
        Log.d(EncryptionConfig.LOG_TAG, "IV length is " + iv.length);
        Log.d(EncryptionConfig.LOG_TAG, "Salt: " + salt);
        String decryptedString = "None";
        SecretKey myAESKey = AESKeyGeneratorHelper.getAESKeyFromPasswordAndGivenSalt(encryptedPassword, salt);
        Log.d(EncryptionConfig.LOG_TAG, "AES Key generated");
        try {
            Cipher cipher = Cipher.getInstance(EncryptionConfig.ENCRYPTION_ALGORITHM);
            Log.d(EncryptionConfig.LOG_TAG, "Cipher generated");

            Log.d(EncryptionConfig.LOG_TAG, "Initialising Cipher...");
            cipher.init(Cipher.DECRYPT_MODE, myAESKey, new IvParameterSpec(iv));
            Log.d(EncryptionConfig.LOG_TAG, "Cipher Initialised");

            Log.d(EncryptionConfig.LOG_TAG, "Doing Final decryption");
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            Log.d(EncryptionConfig.LOG_TAG, "Final Byte-Decryption finished");

            decryptedString = new String (decryptedBytes, "UTF-8");

        } catch (NoSuchAlgorithmException e) {
            Log.d(EncryptionConfig.LOG_TAG, "NoSuchAlgorithm");
            decryptionFailed();
        } catch (NoSuchPaddingException e) {
            Log.d(EncryptionConfig.LOG_TAG, "NoSuchPadding");
            decryptionFailed();
        } catch (InvalidKeyException e) {
            Log.d(EncryptionConfig.LOG_TAG, "InvalidKey");
            decryptionFailed();
        } catch (BadPaddingException e) {
            Log.d(EncryptionConfig.LOG_TAG, "BadPadding");
            decryptionFailed();
        } catch (IllegalBlockSizeException e) {
            Log.d(EncryptionConfig.LOG_TAG, "IllegalBlockSize");
            decryptionFailed();
        } catch (UnsupportedEncodingException e) {
            Log.d(EncryptionConfig.LOG_TAG, "UnsupportedEncoding");
            decryptionFailed();
        } catch (InvalidAlgorithmParameterException e) {
            Log.d(EncryptionConfig.LOG_TAG, "Invalid Iv Parameter");
            decryptionFailed();
        }
        informListener(decryptedString);
    }

    // Der Listener wird über den Abschluss der Entschlüsselung informiert und der entschlüsselte
    // String auf dem UI-Thread übergeben.
    private void informListener(String result){
        Log.d(EncryptionConfig.LOG_TAG, "Informing Listener");
        final String resultString = result;
        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                listener.onDecryptionFinished(resultString);
            }
        });
    }

    //Bei Absturz informieren des Listeners auf dem UI-Thread.
    private void decryptionFailed(){
        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                listener.onDecryptionFailed();
            }
        });
    }

}
