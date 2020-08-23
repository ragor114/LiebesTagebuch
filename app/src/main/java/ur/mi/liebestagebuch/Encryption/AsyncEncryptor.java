package ur.mi.liebestagebuch.Encryption;

import android.os.Handler;

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

    // Die notwendigen Attribute werden über den Konstruktor gesetzt
    public AsyncEncryptor(Handler mainThreadHandler, CryptoListener listener, String toEncrypt, String encryptedPassword){
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
    private void encrypt(){
        //encrypt toEncrypt
        String encryptedString = "";
        SecretKeySpec myAESKey = getAESKey();
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, myAESKey);
            byte [] encrypted = cipher.doFinal(toEncrypt.getBytes());
            encryptedString = new String(encrypted, "UTF-8");
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
        //call informListener
        informListener(encryptedString);
    }

    /*
     * Der SecretKeySpec wird auf Basis des verschlüsselten Passworts generiert.
     */
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

    // Der Listener wird auf dem UI-Thread informiert und der verschlüsselte String übergeben.
    private void informListener(String result){
        final String resultString = result;
        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                listener.onEncryptionFinished(resultString);
            }
        });
    }

}
