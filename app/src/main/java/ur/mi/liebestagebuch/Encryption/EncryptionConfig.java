package ur.mi.liebestagebuch.Encryption;

public class EncryptionConfig {

    public final static String LOG_TAG = "Encryption";
    public final static String ENCRYPTION_ALGORITHM = "AES/CBC/NoPadding";
    public final static String KEY_FACTORY_ALGORITHM = "PBKDF2WithHmacSHA256";
    public final static int SALT_SIZE = 8;
    public final static int INTERATRION_COUNT = 65536;
    public final static int KEY_LENGTH = 256;

}
