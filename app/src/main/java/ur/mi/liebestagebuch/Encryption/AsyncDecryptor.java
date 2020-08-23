package ur.mi.liebestagebuch.Encryption;

import android.os.Handler;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class AsyncDecryptor implements Runnable {

    /*
     * Klasse die hilft einen verschlüsselten String auf Basis des verschlüsselten, vom Nutzer
     * festgelegten Passworts zu entschlüsseln und den Listener den entschlüsselten String zu
     * übermitteln.
     * Als Entschlüsselungsalgorithmus wird AES genutzt.
     *
     * Entwickelt von Jannik Wiese.
     */

    // notwendigr Attribute:
    private Handler mainThreadHandler;
    private CryptoListener listener;
    private String toDecrypt;
    private String encryptedPassword;

    // Die notwendigen Attribute werden über den Konstruktor gesetzt
    public AsyncDecryptor (Handler mainThreadHandler, CryptoListener listener, String toDecrypt, String encryptedPassword){
        this.mainThreadHandler = mainThreadHandler;
        this.listener = listener;
        this.toDecrypt = toDecrypt;
        this.encryptedPassword = encryptedPassword;
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
        byte[] encryptedBytes = toDecrypt.getBytes();
        String decryptedString = "None";
        SecretKeySpec myAESKey = getAESKey();
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, myAESKey);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            decryptedString = new String (decryptedBytes, "UTF-8");
        } catch (NoSuchAlgorithmException e) {
            Log.d("Encryption", "NoSuchAlgorithm");
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            Log.d("Encryption", "NoSuchPadding");
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            Log.d("Encryption", "InvalidKey");
            e.printStackTrace();
        } catch (BadPaddingException e) {
            Log.d("Encryption", "BadPadding");
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            Log.d("Encryption", "IllegalBlockSize");
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        informListener(decryptedString);
    }

    // Ein SecretKeySpec wird auf Basis des verschlüsselten Passworts generiert.
    private SecretKeySpec getAESKey() {
        SecretKeySpec keySpec = null;
        try {
            byte[] key = encryptedPassword.getBytes("UTF-8");
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            keySpec = new SecretKeySpec(key, "AES");
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return keySpec;
    }

    // Der Listener wird über den Abschluss der Entschlüsselung informiert und der entschlüsselte
    // String auf dem UI-Thread übergeben.
    private void informListener(String result){
        final String resultString = result;
        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                listener.onDecryptionFinished(resultString);
            }
        });
    }

}
