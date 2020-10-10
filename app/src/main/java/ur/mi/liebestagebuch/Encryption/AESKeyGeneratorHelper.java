package ur.mi.liebestagebuch.Encryption;

import android.content.Context;
import android.util.Log;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import ur.mi.liebestagebuch.R;

public class AESKeyGeneratorHelper {

    /*
     * Diese Klasse generiert einen SecretKey auf Basis des vom Nutzer festgelegten, übergebenen
     * Passworts
     *
     * Entwickelt von Jannik Wiese
     */

    public static SecretKey getAESKeyFromPassword(String password, Context context){
        byte[] salt = getSalt(context);
        return getAESKeyFromPasswordAndGivenSalt(password, salt, context);
    }

    public static SecretKey getAESKeyFromPasswordAndGivenSalt(String password, byte[] salt, Context context){
        char[] passChars = password.toCharArray();
        SecretKey secretKey = null;
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance(context.getString(R.string.key_factory_algorithm));
            KeySpec spec = new PBEKeySpec(passChars, salt, context.getResources().getInteger(R.integer.iteration_count), context.getResources().getInteger(R.integer.key_length));
            secretKey = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), context.getString(R.string.encryption_algorithm));
        } catch (NoSuchAlgorithmException e) {
            Log.d(EncryptionConfig.LOG_TAG, "NoSuchAlgorithm: " + context.getString(R.string.encryption_algorithm));
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            Log.d(EncryptionConfig.LOG_TAG, "InvalidKeySpec");
            e.printStackTrace();
        }
        return secretKey;
    }

    public static byte[] getSalt(Context context){
        byte[] salt = new byte[context.getResources().getInteger(R.integer.salt_size)];
        new SecureRandom().nextBytes(salt);
        return salt;
    }
}
