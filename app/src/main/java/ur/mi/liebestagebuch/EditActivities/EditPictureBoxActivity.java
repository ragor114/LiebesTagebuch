package ur.mi.liebestagebuch.EditActivities;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import ur.mi.liebestagebuch.DetailAndEditActivity.DetailActivityConfig;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import ur.mi.liebestagebuch.Encryption.StringTransformHelper;
import ur.mi.liebestagebuch.R;

public class EditPictureBoxActivity extends AppCompatActivity {

    /*
     * Die EditPictureBoxActivity dient dazu um neue PictureBoxen zu erzeugen oder bestehende
     * PictureBoxen zu bearbeiten.
     * Dabei wird das ausgwewählte Bild in einem ImageView angezeigt und es gibt Knöpfe um
     * ein Bild auf zu nehmen und aus der Galerie zu wählen.
     * Über den Fertigstellenknopf gelangt man in die aufrufende Activity zurück, wobei das
     * Bild im ImageView als String kommuniziert wird.
     *
     * Basis entwickelt von Jannik Wiese.
     * Bildauswahl/-aufnahme entwickelt von ...
     */

    private Button choosePictureButton;
    private Button takePictureButton;
    private ImageButton finishChoosing;
    private ImageView previewImage;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int PICK_IMAGE = 2;

    private File requestedImageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setUpViews();
    }

    private void setUpViews() {
        setContentView(R.layout.create_picturebox_activity);
        choosePictureButton = (Button) findViewById(R.id.button_choose_from_gallery);
        takePictureButton = (Button) findViewById(R.id.button_take_with_camera);
        finishChoosing = (ImageButton) findViewById(R.id.button_finish_picture_box);
        previewImage = (ImageView) findViewById(R.id.new_picture_preview);

        Intent callingIntent = getIntent();
        Bundle extras = callingIntent.getExtras();

        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });

        choosePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePhoto();
            }
        });

        finishChoosing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Detail", "Button clicked");
                Bitmap previewedBitmap = ((BitmapDrawable) previewImage.getDrawable()).getBitmap();
                Bitmap copyBitmap = previewedBitmap.copy(Bitmap.Config.RGB_565, false);
                //TODO: Make Bitmap smaller (JPEG + weniger Pixel)
                String filePath = saveFile();
                /*
                previewedBitmap = BitmapFactory.decodeFile("/data/user/0/ur.mi.liebestagebuch/app_picture/2020-10-06-14-56-00-143-photo");
                previewImage.setImageBitmap(previewedBitmap);
                Log.d("Picture", "decoded: " + filePath);
                 */
                Intent intent = new Intent();
                intent.putExtra(DetailActivityConfig.PICTUREBOX_CONTENT_KEY, filePath);
                setResult(RESULT_OK, intent);
                finish();
                /*
                String bitmapString = StringTransformHelper.convertBitmapToBase64String(copyBitmap);
                Log.d("Detail", "Got String" + bitmapString);
                Intent intent = new Intent();
                intent.putExtra(DetailActivityConfig.PICTUREBOX_CONTENT_KEY, bitmapString);
                setResult(RESULT_OK, intent);
                finish();
                 */
            }
        });

        if(extras != null){
            setUpForEdit(extras);
        }
    }

    /*
     * Wenn extras übegeben wurden weiß die Activity, dass Sie zum Bearbeiten und nicht zum
     * neu erstellen aufgerufen wurde. Daher wird das Bild im ImageView auf das als String übergebene
     * Bitmap gesetzt und der OnClickListener des Buttons so überschrieben, dass zusätzlich die
     * Position der Box überschrieben wird.
     */
    private void setUpForEdit(Bundle extras) {
        String transferedBitmapPath = extras.getString(DetailActivityConfig.EXISTING_CONTENT_KEY);
        Bitmap transferedBitmap = BitmapFactory.decodeFile(transferedBitmapPath);
        previewImage.setImageBitmap(transferedBitmap);
        File oldFile = new File(transferedBitmapPath);
        if(oldFile.exists()){
            boolean deleted = oldFile.delete();
            if(deleted){
                Log.d("Picture", "Old File deleted");
            }
        }
        final int positionInList = extras.getInt(DetailActivityConfig.POSITION_IN_LIST_KEY);
        finishChoosing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Detail", "Button clicked");
                Bitmap previewedBitmap = ((BitmapDrawable) previewImage.getDrawable()).getBitmap();
                Bitmap copyBitmap = previewedBitmap.copy(Bitmap.Config.RGB_565, false);
                //TODO: Make Bitmap smaller (JPEG + weniger Pixel)
                String bitmapString = StringTransformHelper.convertBitmapToBase64String(copyBitmap);
                String filePath = saveFile();
                Intent intent = new Intent();
                intent.putExtra(DetailActivityConfig.POSITION_IN_LIST_KEY, positionInList);
                intent.putExtra(DetailActivityConfig.PICTUREBOX_CONTENT_KEY, filePath);
                setResult(RESULT_OK, intent);
                finish();
                /*
                Log.d("Detail", "Got String" + bitmapString);
                Intent intent = new Intent();
                intent.putExtra(DetailActivityConfig.PICTUREBOX_CONTENT_KEY, bitmapString);
                intent.putExtra(DetailActivityConfig.POSITION_IN_LIST_KEY, positionInList);
                setResult(RESULT_OK, intent);
                finish();
                 */
            }
        });
    }

    private void takePhoto(){
        Intent takeImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File imageFile = null;
        try {
            imageFile = createTempImageFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(imageFile != null){
            this.requestedImageFile = imageFile;
            Uri photoUri = FileProvider.getUriForFile(this, "ur.mi.liebestagebuch.fileprovider", imageFile);
            takeImage.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            if (takeImage.resolveActivity(getPackageManager()) != null){
                startActivityForResult(takeImage, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createTempImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = timeStamp + "_image";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        return image;
    }

    private void choosePhoto(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Choose Image"),PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Foto aufnehmen
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap fullSize = BitmapFactory.decodeFile(requestedImageFile.getPath());
            previewImage.setImageBitmap(fullSize);
            if(requestedImageFile.exists()){
                requestedImageFile.delete();
            }
        }
        //Foto aus Galerie wählen
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK){
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),imageUri);
                previewImage.setImageBitmap(compressBitmap(bitmap));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Bitmap compressBitmap(Bitmap bmp) {
        ByteArrayOutputStream by = new ByteArrayOutputStream();
        byte[] BYTE;
        BitmapFactory bitmapFactory = new BitmapFactory();

        bmp.compress(Bitmap.CompressFormat.JPEG, 80, by);
        BYTE = by.toByteArray();

        return BitmapFactory.decodeByteArray(BYTE, 0, BYTE.length);
    }

    private String getFileName(){
        Calendar cal = Calendar.getInstance();
        Date currentDate = cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");
        String dateString = sdf.format(currentDate);
        String fileName = dateString + "-photo";
        return fileName;
    }

    private String saveFile(){
        String filePath = getFileName();
        File file = new File(this.getDir("picture", this.MODE_PRIVATE), filePath);
        filePath = file.getPath();
        try {
            file.createNewFile();
        } catch (IOException e) {
            Log.d("Picture", "File could not be created because of IOException");
            e.printStackTrace();
        }
        Bitmap bitmap = ((BitmapDrawable) previewImage.getDrawable()).getBitmap();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        byte[] bitmapData = bos.toByteArray();
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapData);
            fos.flush();
            fos.close();
            Log.d("Picture", "File saved to: " + file.getPath());
            return filePath;
        } catch (FileNotFoundException e) {
            Log.d("Picture", "File not found");
            e.printStackTrace();
        } catch (IOException e) {
            Log.d("Picture", "IOException");
            e.printStackTrace();
        }
        return null;
    }

}
