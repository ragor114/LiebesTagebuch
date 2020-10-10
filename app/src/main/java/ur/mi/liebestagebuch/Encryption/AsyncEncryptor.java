package ur.mi.liebestagebuch.Encryption;

import android.content.Context;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.AlgorithmParameters;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidParameterSpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import ur.mi.liebestagebuch.R;

public class AsyncEncryptor implements Runnable {

    /*
     * Diese Klasse soll die Verschlüsselung eines Strings in einem nebenläufigen Thread übernehmen,
     * wobei der Schlüssel auf Basis des verschlüsselten, vom Nutzer festgelegten Passworts
     * generiert wird.
     * Als Verschlüsselungsalgorithmus wird AES genutzt.
     *
     * Entwickelt von Jannik Wiese
     */

    // Notwendige Attribute zur Verschlüsselung
    private Handler mainThreadHandler;
    private CryptoListener listener;
    private String toEncrypt;
    private String encryptedPassword;
    private Context context;

    // Die notwendigen Attribute werden über den Konstruktor gesetzt
    public AsyncEncryptor(Handler mainThreadHandler, CryptoListener listener, String toEncrypt, String encryptedPassword, Context context) {
        this.context = context;
        this.mainThreadHandler = mainThreadHandler;
        this.listener = listener;
        this.toEncrypt = toEncrypt;
        this.encryptedPassword = encryptedPassword;
    }

    @Override
    public void run() {
        encrypt();
    }

    /*
     * Hauptmethode zur Verschlüsselung zuerst wird ein SecretKeySpec generiert, der dann zur
     * Verschlüsselung mittels der von Java-vorgegebenen Klassen und Methoden genutzt wird.
     * Das verschlüsselte Byte Array wird in einen String umgewandelt und dem Listener übermittelt.
     */
    private void encrypt() {
        //encrypt toEncrypt
        String encryptedString = "";
        byte[] iv = null;
        byte[] salt = AESKeyGeneratorHelper.getSalt(context);
        SecretKey myAESKey = AESKeyGeneratorHelper.getAESKeyFromPasswordAndGivenSalt(encryptedPassword, salt, context);
        try {
            Cipher cipher = Cipher.getInstance(context.getString(R.string.encryption_algorithm));
            cipher.init(Cipher.ENCRYPT_MODE, myAESKey);
            AlgorithmParameters params = cipher.getParameters();
            iv = params.getParameterSpec(IvParameterSpec.class).getIV();
            byte[] encrypted = cipher.doFinal(toEncrypt.getBytes(context.getString(R.string.charset_name)));
            encryptedString = Base64.encodeToString(encrypted, Base64.DEFAULT);
        } catch (NoSuchAlgorithmException e) {
            encryptionFailed();
        } catch (NoSuchPaddingException e) {
            encryptionFailed();
        } catch (InvalidKeyException e) {
            encryptionFailed();
        } catch (BadPaddingException e) {
            encryptionFailed();
        } catch (IllegalBlockSizeException e) {
            encryptionFailed();
        } catch (UnsupportedEncodingException e) {
            encryptionFailed();
        } catch (InvalidParameterSpecException e) {
            // Log.d(EncryptionConfig.LOG_TAG, "Invalid Parameter Spec");
            encryptionFailed();
        }
        //call informListener
        informListener(encryptedString, iv, salt);
    }

    // Der Listener wird auf dem UI-Thread informiert und der verschlüsselte String übergeben.
    private void informListener(String result, byte[] iv, byte[] salt) {
        //Nur final Werte können an einen anderen Thread übergeben werden.
        final String resultString = result;
        final byte[] resultIv = iv;
        final byte[] resultSalt = salt;
        //Informieren des listeners auf einem asynchronen Thread.
        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                // Der initiale Vektor und das randomisierte salt-Array müssen übergeben werden,
                // damit die Entschlüsselung gelingen kann, dürfen aber (laut Best-Practise)
                // unverschlüsselt gespeichert werden, solange der key geheim bleibt.
                listener.onEncryptionFinished(resultString, resultIv, resultSalt);
            }
        });
    }

    //Bei Absturz informieren des Listeners auf dem UI-Thread.
    private void encryptionFailed() {
        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                listener.onEncryptionFailed();
            }
        });
    }

}
