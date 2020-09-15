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

public class CreatePictureBoxActivity extends AppCompatActivity {

    private Button choosePictureButton;
    private Button takePictureButton;
    private ImageButton finishChoosing;
    private ImageView previewImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.create_picturebox_activity);
        choosePictureButton = (Button) findViewById(R.id.button_choose_from_gallery);
        takePictureButton = (Button) findViewById(R.id.button_take_with_camera);
        finishChoosing = (ImageButton) findViewById(R.id.button_finish_picture_box);
        previewImage = (ImageView) findViewById(R.id.new_picture_preview);

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
    }

}
