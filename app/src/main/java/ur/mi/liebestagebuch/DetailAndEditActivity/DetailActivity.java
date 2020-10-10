package ur.mi.liebestagebuch.DetailAndEditActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import java.io.File;
import java.util.Date;
import java.util.List;

import ur.mi.liebestagebuch.Boxes.Box;
import ur.mi.liebestagebuch.Boxes.HeaderBox;
import ur.mi.liebestagebuch.Boxes.MapBox;
import ur.mi.liebestagebuch.Boxes.PictureBox;
import ur.mi.liebestagebuch.Boxes.SpotifyBox;
import ur.mi.liebestagebuch.Boxes.SpotifyBoxReadyListener;
import ur.mi.liebestagebuch.Boxes.TextBox;
import ur.mi.liebestagebuch.Boxes.Type;
import ur.mi.liebestagebuch.EditActivities.EditHeaderBoxActivity;
import ur.mi.liebestagebuch.EditActivities.EditMusicBoxActivity;
import ur.mi.liebestagebuch.EditActivities.EditPictureBoxActivity;
import ur.mi.liebestagebuch.EditActivities.EditTextBoxActivity;
import ur.mi.liebestagebuch.EditActivities.TypeChooserActivity;
import ur.mi.liebestagebuch.Encryption.CryptoListener;
import ur.mi.liebestagebuch.Encryption.StringTransformHelper;
import ur.mi.liebestagebuch.GridView.DateUtil;
import ur.mi.liebestagebuch.GridView.Emotion;
import ur.mi.liebestagebuch.R;
import ur.mi.liebestagebuch.Settings.CheckEncryptionSettingHelper;
import ur.mi.liebestagebuch.database.DBHelper;
import ur.mi.liebestagebuch.database.DatabaseListener;
import ur.mi.liebestagebuch.database.data.DBEntry;

public class DetailActivity extends AppCompatActivity implements CryptoListener, DatabaseListener, BoxListEncryptionListener, SpotifyBoxReadyListener {

    /*
     * In der DetailActivity werden das Datum, die ausgewählte Emotion und der Inhalt in Form von
     * Boxen eines Eintrags angezeigt.
     * Die nötigen Informationen werden aus der Datenbank abgerufen (bzw. ein neuer Datenbankeintrag
     * angelegt) und in einem EntryDetail-Objekt gespeichert.
     * In einem TextView ganz oben wird das Datum des Eintrags angezeigt.
     * Darunter liegen 5 Knöpfe, die mit den 5 auswählbaren Emotionen korrespondieren.
     * Darunter findet sich ein ListView in dem die einzelnen Boxen angezeigt werden.
     * Über einen Plus-Button können neue Einträge hinzugefügt werden.
     * Durch langes halten eines Eintrags kann dieser gelöscht.
     * Ist mit der Bearbeitung/Ansicht fertig kann über den zurück-Knopf oder den entsprechenden
     * Button in der ActionBar zurück in die Grid-Activity gelangt werden, in der die Änderungen
     * in der Datenbank gespeichert und verschlüsselt werden. Dazu werden die Informationen aus dem
     * EntryDetail ausgelesen und per Intent an die GridActivity übergeben.
     *
     * Entwickelt von Jannik Wiese.
     */

    private TextView dateTextView;
    private ListView boxListView;
    private ImageButton[] emotionButtons;
    private ProgressBar progressBar;

    private DBHelper dbHelper;
    private EntryDetail entryDetail;
    private Date entryDate;
    private boolean isReadyToFinish;

    private int currentlyActivatedButton;

    private BoxListAdapter boxListAdapter;

    // Das übergebene Datum wird ausgelesen und die nach dem korrespondierendem Datenbankeintrag gesucht.
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        isReadyToFinish = true;
        setContentView(R.layout.detail_activity);

        progressBar = findViewById(R.id.loading);
        progressBar.setVisibility(View.VISIBLE);

        Intent callingIntent = getIntent();
        Bundle extras = callingIntent.getExtras();
        this.entryDate = DateUtil.setToMidnight((Date) extras.get(getString(R.string.entry_date_key)));
        Log.d("Detail", "Date loaded: " + entryDate.toString());
        dbHelper = new DBHelper(this, this);
        dbHelper.getEntryByDate(this.entryDate);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.detail_activity_action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        switch (id){
            case R.id.back_button:
                finishDetail();
                break;
            case R.id.add_box_menu_item:
                startAddingNewBox();
                break;
        }
        return true;
    }

    /*
     * Wurde ein existierender Datenbankeintrag gefunden werden die Views referenziert und ihre
     * Funktionalität initialisiert.
     * Das dateTextView ändert seinen Text aber erst wenn die BoxList entschlüsselt und angezeigt
     * wurde.
     */
    private void setUpViews() {
        Log.d("Detail", "Setting up Views");
        dateTextView = (TextView) findViewById(R.id.datum_text_view);
        boxListView = (ListView) findViewById(R.id.box_list_view);

        setUpEmotionButtons();

        FloatingActionButton fab = findViewById(R.id.floating_action_button);
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startAddingNewBox();
            }
        });

        Log.d("Detail", "setUpViews finished");
    }

    /*
     * Jeder EmotionButton wird referenziert und bekommt einen OnClickListener, der die Emotion im
     * EntryDetail auf den korrespondierenden Wert setzt und den entsprechenden Button farbig und
     * die anderen S/W macht.
     * Zudem wird beim SetUp der mit dem EntryDetail korrespondierende Button aktiviert.
     */
    private void setUpEmotionButtons() {
        emotionButtons = new ImageButton[5];
        emotionButtons[0] = (ImageButton) findViewById(R.id.button_very_good);
        emotionButtons[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                entryDetail.setEmotion(Emotion.VERY_GOOD);
                activateEmotionButton(0);
            }
        });
        emotionButtons[1] = (ImageButton) findViewById(R.id.button_good);
        emotionButtons[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                entryDetail.setEmotion(Emotion.GOOD);
                activateEmotionButton(1);
            }
        });
        emotionButtons[2] = (ImageButton) findViewById(R.id.button_normal);
        emotionButtons[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                entryDetail.setEmotion(Emotion.NORMAL);
                activateEmotionButton(2);
            }
        });
        emotionButtons[3] = (ImageButton) findViewById(R.id.button_bad);
        emotionButtons[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                entryDetail.setEmotion(Emotion.BAD);
                activateEmotionButton(3);
            }
        });
        emotionButtons[4] = (ImageButton) findViewById(R.id.button_very_bad);
        emotionButtons[4].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                entryDetail.setEmotion(Emotion.VERY_BAD);
                activateEmotionButton(4);
            }
        });
        activateEmotionButton(entryDetail.getEmotionInt());
    }

    /*
     * Der aktuell aktivierte Button wird zurückgesetzt und der Background des übergebenen (angeklickten)
     * Buttons auf die farbige Variante gesetzt.
     */
    private void activateEmotionButton (int position){
        resetActivatedEmotionButton();
        currentlyActivatedButton = position;
        switch(position){
            case 0:
                emotionButtons[0].setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_very_good_color_24dp, null));
                break;
            case 1:
                emotionButtons[1].setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_good_color_24dp, null));
                break;
            case 2:
                emotionButtons[2].setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_normal_color_24dp, null));
                break;
            case 3:
                emotionButtons[3].setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_bad_color_24dp, null));
                break;
            case 4:
                emotionButtons[4].setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_very_bad_color_24dp, null));
                break;
        }
    }

    /*
     * Der Hintergrund des aktuell aktivierten Buttons wird auf die S/W Variante des Drawables gesetzt.
     */
    private void resetActivatedEmotionButton(){
        switch (currentlyActivatedButton){
            case 0:
                emotionButtons[0].setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.ic__very_good_bw_24dp, null));
                break;
            case 1:
                emotionButtons[1].setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_good_black_24dp, null));
                break;
            case 2:
                emotionButtons[2].setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_normal_black_24dp, null));
                break;
            case 3:
                emotionButtons[3].setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_bad_black_24dp, null));
                break;
            case 4:
                emotionButtons[4].setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_very_bad_black_24dp, null));
                break;
            default:
                Toast.makeText(this, "No Button activated, try again later", Toast.LENGTH_SHORT);
                break;
        }
    }

    /*
     * Wenn der Zurück-Knopf oder der Speichern Menü-Eintrag gedürckt wird, wird überprüft, ob
     * gerade eine Entschlüsselungs- oder Datenbankoperation läuft. Falls nicht werden die relevanten
     * Informationen aus dem EntryDetail-Objekt aus gelesen und als Extras an die aufrufende Grid-
     * Activity übergeben. Auch wenn der Zurück-Knopf gedrückt wird, wird der ResultCode auf OKAY
     * gesetzt.
     */
    private void finishDetail(){
        if(isReadyToFinish == true){
            Intent returnIntent = new Intent();

            Date entryDate = DateUtil.setToMidnight(entryDetail.getDate());
            Emotion entryEmotion = entryDetail.getEmotion();
            String boxListString = entryDetail.getBoxListString();

            returnIntent.putExtra(getString(R.string.entry_date_key), entryDate);
            returnIntent.putExtra(getString(R.string.emotion_key), entryEmotion);
            returnIntent.putExtra(getString(R.string.box_list_key), boxListString);

            setResult(RESULT_OK, returnIntent);
            finish();
        } else{
            Toast.makeText(this, "Decryption running, try again later.", Toast.LENGTH_SHORT);
        }
    }

    // Der Zurück-Knopf wird überschrieben, damit auch mit diesem die Informationen in die DB
    // zurückgespeichert werden können.
    @Override
    public void onBackPressed(){
        finishDetail();
    }

    // Falls eine neue Box ergänzt werden soll, wird die TypeChooserActivity forResult gestartet.
    private void startAddingNewBox(){
        Intent intent = new Intent(DetailActivity.this, TypeChooserActivity.class);
        startActivityForResult(intent, getResources().getInteger(R.integer.type_chooser_request_code));
    }

    /*
     * Falls der ResultCode Okay ist, wird überprüft, ob die Activity die sich zurückmeldet gestartet
     * wurde, um eine neue Box zu erstellen oder eine vorhandene Box zu bearbeiten und dementsprechende
     * Methoden aufgerufen. Am Ende wird der boxListAdapter informiert, dass sich die angeschlossene
     * ArrayList verändert hat.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            Log.d("Detail", "Result in Detail OK");
            Bundle extras = data.getExtras();
            if(requestCode == getResources().getInteger(R.integer.spotify_auth_request_code)){
                gotAccessToken(resultCode, data);
            } else if(requestCode != getResources().getInteger(R.integer.edit_box_request_code)) {
                newBoxResult(extras);
            } else if (requestCode == getResources().getInteger(R.integer.edit_box_request_code)){
                editBoxResult(extras);
            }
            boxListAdapter.notifyDataSetChanged();
        }
    }

    private void gotAccessToken(int resultCode, Intent data) {
        AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, data);
        switch (response.getType()){
            case TOKEN:
                DetailActivityConfig.ACCESS_TOKEN = response.getAccessToken();
                setUpSpotifyWebApis();
                break;
            case ERROR:
                Toast.makeText(this, "Error trying to authentificate Spotify", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    // Alle Spotify Boxen werden informiert, dass jetzt ein Access Token zur Verfügung steht.
    private void setUpSpotifyWebApis() {
        for(Box current : entryDetail.getBoxList()){
            if(current.getType() == Type.MUSIC){
                ((SpotifyBox) current).gotAccessToken();
            }
        }
    }

    /*
     * Falls die sich zurückmeldende Activity aufgerufen wurde um einen vorhandenen Eintrag zu
     * bearbeiten wird der Bearbeitete Inhalt aus den Extras abgerufen und der Content
     * der entsprechenden Box damit überschrieben.
     */
    private void editBoxResult(Bundle extras) {
        int positionInList = extras.getInt(getString(R.string.position_in_list_key));
        String newContent = "";
        if(extras.getString(getString(R.string.textbox_content_key)) != null){
            newContent = extras.getString(getString(R.string.textbox_content_key));
        }
        if(extras.getString(getString(R.string.picturebox_content_key)) != null){
            newContent = extras.getString(getString(R.string.picturebox_content_key));
        }
        if(extras.getString(getString(R.string.mapbox_content_key)) != null){
            newContent = extras.getString(getString(R.string.mapbox_content_key));
        }
        if(extras.getString(getString(R.string.musicbox_content_key)) != null){
            newContent = extras.getString(getString(R.string.musicbox_content_key));
            Log.d("Spotify", "Position " +positionInList + " changed to: " + newContent);
        }
        if(extras.getString(getString(R.string.headerbox_content_key)) != null){
            newContent = extras.getString(getString(R.string.headerbox_content_key));
        }
        entryDetail.getBoxList().get(positionInList).setContent(newContent);
    }

    /*
     * Falls die sich zurückmeldende Activity aufgerufen wurde um eine neue Box zu erstellen,
     * wird anhand des Schlüssels überprüft um welche Art von Box es sich handelt und eine neue
     * Box dieses Typs erstellt, die dann in die Arraylist, die im EntryDetail gespeichert ist,
     * eingefügt wird.
     */
    private void newBoxResult(Bundle extras) {
        if (extras.getString(getString(R.string.textbox_content_key)) != null) {
            TextBox createdTextBox = new TextBox(extras.getString(getString(R.string.textbox_content_key)));
            entryDetail.addBoxToBoxList(createdTextBox);
        } else if (extras.getString(getString(R.string.picturebox_content_key)) != null) {
            Log.d("Detail", "Creating new PictureBox");
            PictureBox createdPictureBox = new PictureBox(extras.getString(getString(R.string.picturebox_content_key)));
            entryDetail.addBoxToBoxList(createdPictureBox);
        } else if(extras.get(getString(R.string.mapbox_content_key)) != null){
            Log.d("Detail", "Creating new MapBox");
            LatLng coordinates = (LatLng) extras.get(getString(R.string.mapbox_content_key));
            Log.d("MapView", "Got LatLng with: " + coordinates.toString());
            MapBox createdMapBox = new MapBox(coordinates);
            entryDetail.addBoxToBoxList(createdMapBox);
        } else if(extras.getString(getString(R.string.musicbox_content_key)) != null){
            Log.d("Spotify", "Creating new MusicBox");
            String songUri = extras.getString(getString(R.string.musicbox_content_key));
            SpotifyBox createdSpotifyBox = new SpotifyBox(songUri, this, this);
            entryDetail.addBoxToBoxList(createdSpotifyBox);
            Log.d("Spotify", "Added to BoxList: " + entryDetail.getBoxList().toString());
        } else if(extras.getString(getString(R.string.headerbox_content_key)) != null){
            Log.d("Detail", "Creating new Headerbox");
            String header = extras.getString(getString(R.string.headerbox_content_key));
            HeaderBox createdHeaderBox = new HeaderBox(header);
            entryDetail.addBoxToBoxList(createdHeaderBox);
        }
    }

    // Um einen neuen Datenbank Eintrag zu erstellen wird ein verschlüsselter Inhalt benötigt.
    @Override
    public void onEncryptionFinished(String result, byte[] iv, byte[] salt){
        Log.d("Detail", "Encryption finished");
        Log.d("Encryption", "IV length at Encryption is " + iv.length);
        dbHelper.newEntry(entryDate, 2, result, salt, iv);
        Log.d("Encryption", iv.toString());
    }

    @Override
    public void onDecryptionFinished(String result) {
        return;
    }

    @Override
    public void onEncryptionFailed() {
        Toast.makeText(this, "Encryption failed, if this keeps happening, change password.", Toast.LENGTH_SHORT);
    }

    @Override
    public void onDecryptionFailed() {
        return;
    }

    // Falls die Datenbank zurückmeldet, dass erfolgreich ein neuer Eintrag erstellt wurde wird eine
    // erneute Abfrage nach diesem Eintrag gestartet.
    @Override
    public void updateFinished(int updateCode) {
        if(updateCode == getResources().getInteger(R.integer.new_entry_update_code)){
            Log.d("Detail", "New Entry created");
            dbHelper.getEntryByDate(entryDate);
        }
    }

    /*
     * Falls die Datenbank zu dem übergebenen Datum keinen Eintrag findet (also null liefert) wird
     * ein neuer Eintrag mit einem vordefinierten Inhalt erstellt.
     * Falls ein Datenbankeintrag gefunden wurde, wird ein EntryDetail-Objekt auf Basis des gefundenen
     * Datenbank-Entrys erstellt und die Views initialisiert.
     */
    @Override
    public void entryFound(DBEntry foundEntry) {
        if(foundEntry == null){
            Log.d("Detail", "No Entry found");
            isReadyToFinish = false;
            String emptyContent = "|<Text | Schreib deine Erlebnisse auf.";
            if(CheckEncryptionSettingHelper.encryptionActivated(this)){
                StringTransformHelper.startEncryption(emptyContent, this, this);
            } else{
                byte[] emptyIv = new byte[]{00,00};
                byte[] emptySalt = new byte[]{00,00};
                dbHelper.newEntry(entryDate, 2, emptyContent, emptySalt, emptyIv);
            }
        } else{
            Log.d("Detail", "Entry found");
            isReadyToFinish = true;
            Log.d("Encryption", "found Entry: " + foundEntry.toString());
            this.entryDetail = new EntryDetail(foundEntry, this, this, this);
            Log.d("Passwort", "EntryDetail created: " + entryDetail.toString());
            setUpViews();
            if(!CheckEncryptionSettingHelper.encryptionActivated(this)){
                setUpBoxlistView();
            }
        }
    }

    @Override
    public void allEntriesFound(List<DBEntry> allEntries) {

    }

    @Override
    public void onBoxListEncrypted(String encryptedBoxListString, byte[] iv, byte[] salt) {
        return;
    }

    // Wenn das EntryDetail meldet, dass erfolgreich eine ArrayList von Boxen erstellt wurde,
    // wird diese an den Adapter angeschlossen.
    @Override
    public void onBoxListDecryptionFinished() {
        Log.d("Passwort", "EntryDetail is: " + entryDetail.getBoxListString());
        setUpBoxlistView();
    }

    @Override
    public void onEncryptionFailed(int code) {
        if(code == getResources().getInteger(R.integer.decryption_failed_code)){
            Toast.makeText(this, "Decryption failed, if this keeps happening, change password", Toast.LENGTH_SHORT);
        }
    }

    /*
     * Wenn Datenbankabfrage und Entschlüsselung erfolgreich waren, wird die ArrayList von Boxen im
     * EntryDetail-Objekt über einen BoxListAdapter an die boxListView angeschloßen, dass dateTextView
     * auf das Datum gesetzt und die onClickListener der boxListView gesetzt.
     */
    private void setUpBoxlistView() {
        Log.d("Passwort", "EntryDetail is: " + entryDetail.getBoxListString());
        boxListAdapter = new BoxListAdapter(this.entryDetail.getBoxList(), this);
        boxListView.setAdapter(boxListAdapter);
        boxListAdapter.notifyDataSetChanged();

        dateTextView.setText(this.entryDetail.getDateString());

        setBoxListClickListener();

        progressBar.setVisibility(View.GONE);
    }

    /*
     * Falls auf einen Eintrag im boxListView lang geklickt wird, wird der korrespondierende
     * Eintrag aus der ArrayList gelöscht.
     * Falls auf einen Eintrag in der Liste lang geklickt wird, wird je nach Typ der Box die entsprechende
     * EditActivity gestartet und dieser der aktuelle Inhalt der Box (als String) und die Position in
     * der Liste übergeben, so dass der Inhalt bearbeitet werden kann.
     */
    private void setBoxListClickListener() {
        setBoxListShortClickListener();
        setBoxListLongClickListener();
    }

    private void setBoxListShortClickListener() {
        boxListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Detail", "BoxList Entry clicked at position " + position + " clicked.");
                Log.d("Detail", "Boxlist is: " + entryDetail.getBoxList().toString());
                Box clickedBox = entryDetail.getBoxFromBoxList(position);
                Type boxType = clickedBox.getType();
                switch (boxType){
                    case TEXT:
                        Intent startTextboxEditingIntent = new Intent(DetailActivity.this, EditTextBoxActivity.class);
                        startTextboxEditingIntent.putExtra(getString(R.string.position_in_list_key), position);
                        startTextboxEditingIntent.putExtra(getString(R.string.existing_content_key), clickedBox.getString());
                        startActivityForResult(startTextboxEditingIntent, getResources().getInteger(R.integer.edit_box_request_code));
                        break;
                    case PICTURE:
                        Intent startPictureboxEditingIntent = new Intent(DetailActivity.this, EditPictureBoxActivity.class);
                        startPictureboxEditingIntent.putExtra(getString(R.string.position_in_list_key), position);
                        startPictureboxEditingIntent.putExtra(getString(R.string.existing_content_key), clickedBox.getString());
                        startActivityForResult(startPictureboxEditingIntent, getResources().getInteger(R.integer.edit_box_request_code));
                        break;
                    case MAP:
                        MapBox mapBox = (MapBox) clickedBox;
                        Intent startMapBoxDetailIntent = new Intent(DetailActivity.this, MapBoxDetailActivity.class);
                        startMapBoxDetailIntent.putExtra(getString(R.string.position_in_list_key), position);
                        startMapBoxDetailIntent.putExtra(getString(R.string.existing_content_key), mapBox.coordinates);
                        startActivityForResult(startMapBoxDetailIntent, getResources().getInteger(R.integer.edit_box_request_code));
                        break;
                    case MUSIC:
                        Log.d("Spotify", "Spotifybox at position " + position + " clicked.");
                        Intent startMusicboxEditingIntent = new Intent(DetailActivity.this, EditMusicBoxActivity.class);
                        startMusicboxEditingIntent.putExtra(getString(R.string.position_in_list_key), position);
                        startMusicboxEditingIntent.putExtra(getString(R.string.existing_content_key), clickedBox.getString());
                        startActivityForResult(startMusicboxEditingIntent, getResources().getInteger(R.integer.edit_box_request_code));
                        break;
                    case HEADER:
                        Intent startHeaderboxEditingIntent = new Intent(DetailActivity.this, EditHeaderBoxActivity.class);
                        startHeaderboxEditingIntent.putExtra(getString(R.string.position_in_list_key), position);
                        startHeaderboxEditingIntent.putExtra(getString(R.string.existing_content_key), clickedBox.getString());
                        startActivityForResult(startHeaderboxEditingIntent, getResources().getInteger(R.integer.edit_box_request_code));
                        break;
                }
            }
        });
    }

    /*
     * Boxen werden bei einem langen Klick aus der Arraylist und damit dem ListView entfernt. Handelt
     * es sich bei der geklickten Box um eine Bildbox wird außerdem die Datei gelöscht, um Speicher
     * frei zu machen.
     */
    private void setBoxListLongClickListener() {
        boxListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Detail", "LongClicked Position: " + position);
                String filePath = null;
                if(entryDetail.getBoxList().get(position).getType() == Type.PICTURE){
                    filePath = entryDetail.getBoxList().get(position).getString();
                }
                entryDetail.getBoxList().remove(position);
                boxListAdapter.notifyDataSetChanged();
                if(filePath != null){
                    File fileToDelete = new File(filePath);
                    if(fileToDelete.exists()){
                        fileToDelete.delete();
                    }
                }
                return true;
            }
        });
    }

    /*
     * Falls eine SpotifyBox meldet keinen Access Token zu haben startet die DetailActivity eine
     * Abfrage für den Access Token, wofür entweder die Spotify App oder eine Website geöffnet wird.
     * Ist der Nutzer angemeldet, erhält die DetailActivity sofort einen validen Token.
     */
    @Override
    public void needsAccessToken() {
        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(getString(R.string.spotify_client_id), AuthenticationResponse.Type.TOKEN, getString(R.string.spotify_redirect_uri));
        builder.setScopes(new String[]{"streaming"});
        AuthenticationRequest request = builder.build();
        AuthenticationClient.openLoginActivity(this, getResources().getInteger(R.integer.spotify_auth_request_code), request);
    }

    /*
     * Meldet eine SpotifyBox, dass sie asynchron neuen Inhalt erhalten hat, wird der Adapter über
     * eine Veränderung im ListView informiert, wodurch dieser wieder die getView() Methode der SpotifyBox
     * aufruft, die dadurch den veränderten Inhalt anzeigt.
     */
    @Override
    public void updatedViews() {
        Log.d("Spotify", "Callback: updatedView()");
        boxListAdapter.notifyDataSetChanged();
    }
}
