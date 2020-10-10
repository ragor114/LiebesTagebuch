package ur.mi.liebestagebuch.EditActivities;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;

import ur.mi.liebestagebuch.Encryption.CryptoListener;
import ur.mi.liebestagebuch.Encryption.StringTransformHelper;

public class PictureEncryptionHelper implements CryptoListener {

    /*
     * Diese Klasse sollte helfen die Bilder verschlüsselt zu speichern. Das sollte die Sicherheit
     * der Bilder erhöhen, die zwar nicht von anderen Apps abgerufen werden können, da sie
     * im privaten Speicher liegen, aber aktuell noch unverschlüsselt gespeichert werden. Die
     * Referenzen auf die Bilder in der Datenbank werden dageben verschlüsselt.
     * Auf Grund der unerwartet hohen Schwierigkeit ist diese Klasse nicht fertig.
     *
     * Entwickelt von Jannik Wiese.
     */

    private static final String PASSWORD_PATH = "encrypted_picture_pw";
    private static final String IV_PATH = "encrypted_picture_iv";
    private static final String SALT_PATH = "encrypted_picture_salt";
    private Context context;
    private boolean passwordSet;

    public PictureEncryptionHelper(Context context){
        this.context = context;
        passwordSet = false;
    }

    public void saveEncryptedBitmap(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        byte[] bitmapBytes = baos.toByteArray();
        bitmap.recycle();
        String bitmapString = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);

    }

    public void getPicturePassword(){
        File passwordFile = new File(context.getDir("picture", Context.MODE_PRIVATE), PASSWORD_PATH);
        if(passwordFile.exists()){
            getPasswordString();
        } else{
            createNewPasswordFile(passwordFile);
        }
    }

    private void getPasswordString() {

    }

    private void createNewPasswordFile(File passwordFile) {
        SecureRandom random = new SecureRandom();
        byte[] randomPasswordBytes = new byte[16];
        random.nextBytes(randomPasswordBytes);
        String generatedString = new String(randomPasswordBytes, Charset.forName("UTF-8"));
        //StringTransformHelper.startEncryption(generatedString, this);
    }

    private void overrideExistingFile(File file, String newContent){
        if(file.exists()){
            file.delete();
        }
        try {
            file.createNewFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(newContent);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void overrideExistingPasswordFile(String newPassword){
        File passwordFile = new File(context.getDir("picture", Context.MODE_PRIVATE), PASSWORD_PATH);
        overrideExistingFile(passwordFile, newPassword);
    }

    private void overrideIvFile(byte[] iv){
        File ivFile = new File(context.getDir("picture", Context.MODE_PRIVATE), IV_PATH);
        String ivString = Base64.encodeToString(iv, Base64.DEFAULT);
        overrideExistingFile(ivFile, ivString);
    }

    private void overrideSaltFile(byte[] salt){
        File saltFile = new File(context.getDir("picture", Context.MODE_PRIVATE), SALT_PATH);
        String saltString = Base64.encodeToString(salt, Base64.DEFAULT);
        overrideExistingFile(saltFile, saltString);
    }

    @Override
    public void onEncryptionFinished(String result, byte[] iv, byte[] salt) {
        if(passwordSet == false){
            overrideExistingPasswordFile(result);
            overrideIvFile(iv);
            overrideSaltFile(salt);
        }
    }

    @Override
    public void onDecryptionFinished(String result) {

    }

    @Override
    public void onEncryptionFailed() {

    }

    @Override
    public void onDecryptionFailed() {

    }
}
