package ur.mi.liebestagebuch.Encryption;

import android.util.Log;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class AESKeyGeneratorHelper {

    /*
     * Diese Klasse generiert einen SecretKey auf Basis des vom Nutzer festgelegten, übergebenen
     * Passworts
     *
     * Entwickelt von Jannik Wiese
     */

    public static SecretKey getAESKeyFromPassword(String password){
        byte[] salt = getSalt();
        return getAESKeyFromPasswordAndGivenSalt(password, salt);
    }

    public static SecretKey getAESKeyFromPasswordAndGivenSalt(String password, byte[] salt){
        char[] passChars = password.toCharArray();
        SecretKey secretKey = null;
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance(EncryptionConfig.KEY_FACTORY_ALGORITHM);
            KeySpec spec = new PBEKeySpec(passChars, salt, EncryptionConfig.INTERATRION_COUNT, EncryptionConfig.KEY_LENGTH);
            secretKey = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), EncryptionConfig.ENCRYPTION_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            Log.d(EncryptionConfig.LOG_TAG, "NoSuchAlgorithm: " + EncryptionConfig.KEY_FACTORY_ALGORITHM);
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            Log.d(EncryptionConfig.LOG_TAG, "InvalidKeySpec");
            e.printStackTrace();
        }
        return secretKey;
    }

    public static byte[] getSalt(){
        byte[] salt = new byte[EncryptionConfig.SALT_SIZE];
        new SecureRandom().nextBytes(salt);
        return salt;
    }
}
