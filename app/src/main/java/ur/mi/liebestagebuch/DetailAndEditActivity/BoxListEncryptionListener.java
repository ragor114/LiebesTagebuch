package ur.mi.liebestagebuch.DetailAndEditActivity;

public interface BoxListEncryptionListener {

    void onBoxListEncrypted(String encryptedBoxListString, byte[] iv, byte[] salt);
    void onBoxListDecryptionFinished();

}
