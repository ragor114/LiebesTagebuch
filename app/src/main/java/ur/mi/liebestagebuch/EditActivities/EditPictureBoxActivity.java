package ur.mi.liebestagebuch.EditActivities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import ur.mi.liebestagebuch.DetailAndEditActivity.DetailActivityConfig;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

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

    private String currentPhotoPath;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int PICK_IMAGE = 2;

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
                String bitmapString = StringTransformHelper.convertBitmapToBase64String(copyBitmap);
                Log.d("Detail", "Got String" + bitmapString);
                Intent intent = new Intent();
                intent.putExtra(DetailActivityConfig.PICTUREBOX_CONTENT_KEY, bitmapString);
                setResult(RESULT_OK, intent);
                finish();
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
        String transferedBitmapString = extras.getString(DetailActivityConfig.EXISTING_CONTENT_KEY);
        Bitmap transferedBitmap = StringTransformHelper.convertBase64StringToBitmap(transferedBitmapString);
        previewImage.setImageBitmap(transferedBitmap);
        final int positionInList = extras.getInt(DetailActivityConfig.POSITION_IN_LIST_KEY);
        finishChoosing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Detail", "Button clicked");
                Bitmap previewedBitmap = ((BitmapDrawable) previewImage.getDrawable()).getBitmap();
                Bitmap copyBitmap = previewedBitmap.copy(Bitmap.Config.RGB_565, false);
                //TODO: Make Bitmap smaller (JPEG + weniger Pixel)
                String bitmapString = StringTransformHelper.convertBitmapToBase64String(copyBitmap);
                Log.d("Detail", "Got String" + bitmapString);
                Intent intent = new Intent();
                intent.putExtra(DetailActivityConfig.PICTUREBOX_CONTENT_KEY, bitmapString);
                intent.putExtra(DetailActivityConfig.POSITION_IN_LIST_KEY, positionInList);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void takePhoto(){
        Intent takeImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takeImage.resolveActivity(getPackageManager()) != null){
            startActivityForResult(takeImage, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void choosePhoto(){
        Intent pick = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pick.setType("image/*");

        Intent choose = Intent.createChooser(getIntent(), "Select Image");
        choose.putExtra(Intent.EXTRA_INITIAL_INTENTS,new Intent[] {pick});

        startActivityForResult(choose,PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Foto aufnehmen
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap bitmap = (Bitmap) extras.get("data");
            previewImage.setImageBitmap(bitmap);
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

        bmp.compress(Bitmap.CompressFormat.JPEG, 10, by);
        BYTE = by.toByteArray();

        return BitmapFactory.decodeByteArray(BYTE, 0, BYTE.length);
    }
}
