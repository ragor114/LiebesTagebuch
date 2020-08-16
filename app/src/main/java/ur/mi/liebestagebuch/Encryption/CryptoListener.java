package ur.mi.liebestagebuch.Encryption;

public interface CryptoListener {

    /*
     * Klassen die dieses Interface implementieren lauschen auf abgeschlossene Ver- und
     * Entschlüsselungsvorgänge.
     */

    void onEncryptionFinished (String result);
    void onDecryptionFinished (String result);

}
