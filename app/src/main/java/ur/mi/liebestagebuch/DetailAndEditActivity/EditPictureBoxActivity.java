package ur.mi.liebestagebuch.DetailAndEditActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

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

}