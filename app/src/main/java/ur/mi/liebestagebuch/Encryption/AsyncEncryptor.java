package ur.mi.liebestagebuch.Encryption;

import android.os.Handler;

public class AsyncEncryptor implements Runnable {

    private Handler mainThreadHandler;
    private CryptoListener listener;
    private String toEncrypt;

    public AsyncEncryptor(Handler mainThreadHandler, CryptoListener listener, String toEncrypt){
        this.mainThreadHandler = mainThreadHandler;
        this.listener = listener;
        this.toEncrypt = toEncrypt;
    }

    @Override
    public void run() {
        encrypt();
    }

    private void encrypt(){
        //encrypt toEncrypt
        //call informListener
    }

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
