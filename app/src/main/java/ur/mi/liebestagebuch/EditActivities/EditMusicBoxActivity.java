package ur.mi.liebestagebuch.EditActivities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import java.util.Arrays;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.TracksPager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import ur.mi.liebestagebuch.DetailAndEditActivity.DetailActivityConfig;
import ur.mi.liebestagebuch.R;

public class EditMusicBoxActivity extends AppCompatActivity {

    /*
     * Die EditMusicBoxActivity dient dazu eine SpotifyBox zu erzeugen. Dazu können Links zu Songs
     * auf Spotify oder die Suchleiste genutzt werden.
     * Dafür sind aber bestimmte Voraussetzungen nötig, ohne die das Spotify SDK und die Spotify Web
     * API nicht wie geplant funktionieren. So muss die Spotify App installiert und in Online-Modus sein
     * und der Nutzer muss über einen Premium Account verfügen. Sind diese Bedingungen nicht erfüllt
     * treten leider unschöne Fehler auf.
     *
     * Entwickelt von Jannik Wiese.
     */

    private SpotifyAppRemote appRemote;

    private EditText linkEditText;
    private Button linkOkButton;
    private ImageButton playButton;
    private ImageButton finishButton;
    private TextView trackNameView;
    private TextView artistNameView;
    private EditText searchEditText;

    private String songUri;
    private SpotifyApi api;
    private SpotifyService spotify;
    private boolean editMode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_music_box_activity);
        //Log.d("Spotify", "EditMusicBox created");
        setUpInvisibleViews();
    }

    // Die drei Views am unteren Bildschirmrand werden erst sichtbar (und sinnvoll) wenn ein Song
    // gewählt wurde.
    private void setUpInvisibleViews() {
        editMode = false;

        songUri = "";

        playButton = findViewById(R.id.edit_spotify_play);
        trackNameView = findViewById(R.id.spotify_edit_track_name);
        artistNameView = findViewById(R.id.spotify_edit_artist_name);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appRemote.getPlayerApi().play(songUri);
            }
        });

        playButton.setVisibility(View.INVISIBLE);
        trackNameView.setVisibility(View.INVISIBLE);
        artistNameView.setVisibility(View.INVISIBLE);
    }

    // Kann sich der Nutzer als Premium-Member authentifizieren, wird die SpotifyAppRemote verbunden.
    // Erst wenn das möglich ist werden die anderen Views mit Funktionalität ausgestattet.
    @Override
    protected void onStart() {
        super.onStart();
        ConnectionParams connectionParams = new ConnectionParams.Builder(getString(R.string.spotify_client_id))
                .setRedirectUri(getString(R.string.spotify_redirect_uri))
                .showAuthView(true)
                .build();
        //Log.d("Spotify", "ConnectionParams built");
        SpotifyAppRemote.connect(this, connectionParams, new Connector.ConnectionListener() {
            @Override
            public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                appRemote = spotifyAppRemote;
                //DEBUG: appRemote.getPlayerApi().play("spotify:playlist:6uJdeXLzNtFPEhuZ0XFid0");
                setUpViews();
                //Log.d("Spotify", "App Remote connected!");
            }

            @Override
            public void onFailure(Throwable throwable) {
                //Log.d("Spotify", "App Remote could not connect: " + throwable.getMessage());
            }
        });
        //Log.d("Spotify", "Tried to connect");
    }

    /*
     * Ist noch kein Access Token vorhanden wird ein neuer angefragt, ansonsten wird die Spotify
     * Web API initialisiert.
     * Wird der linkOkButton gedrückt wird die mit dem Link korrespondierende SongUri gespeichert
     * und die Informationen über den Song angezeigt.
     * Wird der finishButton geklickt wird die SongUri und eventuell die Position der aufrufenden
     * Box an die aufrufende Activity zurück kommuniziert.
     */
    private void setUpViews() {
        linkEditText = findViewById(R.id.spotify_link_edit);
        searchEditText = findViewById(R.id.spotify_search_edit);
        linkOkButton = findViewById(R.id.spotify_link_ok);
        finishButton = findViewById(R.id.finish_spotify_edit);

        if (DetailActivityConfig.ACCESS_TOKEN == null || DetailActivityConfig.ACCESS_TOKEN.equals("")) {
            getAccessToken();
        } else {
            setUpSpotifyWebApi();
        }

        linkOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                okPressed();
            }
        });

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishEditing();
            }
        });
    }

    private void getAccessToken() {
        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(getString(R.string.spotify_client_id), AuthenticationResponse.Type.TOKEN, getString(R.string.spotify_redirect_uri));
        builder.setScopes(new String[]{"streaming"});
        AuthenticationRequest request = builder.build();
        //Log.d("Spotify", "trying to open window");
        AuthenticationClient.openLoginActivity(this, getResources().getInteger(R.integer.spotify_auth_request_code), request);
    }

    private void finishEditing() {
        Intent intent = new Intent();
        intent.putExtra(getString(R.string.musicbox_content_key), songUri);

        if (editMode) {
            Bundle callingExtras = getCallingExtras();
            intent.putExtra(getString(R.string.position_in_list_key), callingExtras.getInt(getString(R.string.position_in_list_key)));
        }

        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == getResources().getInteger(R.integer.spotify_auth_request_code)) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, data);
            switch (response.getType()) {
                case TOKEN:
                    DetailActivityConfig.ACCESS_TOKEN = response.getAccessToken();
                    setUpSpotifyWebApi();
                    break;
                case ERROR:
                    Toast.makeText(this, "Error trying to authentificate Spotify", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    /*
     * Konnte die Spotify Web API erfolgreich initialisiert werden, können die Informationen über den
     * übergebenen Song (beim Bearbeiten) abgerufen und in die Views platziert werden.
     */
    private void setUpSpotifyWebApi() {
        api = new SpotifyApi();
        api.setAccessToken(DetailActivityConfig.ACCESS_TOKEN);
        spotify = api.getService();

        Bundle extras = getCallingExtras();
        if (extras != null) {
            if (extras.getString(getString(R.string.existing_content_key)) != null) {
                songUri = extras.getString(getString(R.string.existing_content_key));
                String[] splits = songUri.split(":");
                String trackId = splits[2];
                setSongUri(trackId);
                editMode = true;
            }
        }
    }

    private Bundle getCallingExtras() {
        Intent callingIntent = getIntent();
        return callingIntent.getExtras();
    }

    // Wurde der Ok-Button gedrückt wird überprüft ob eine Suche oder eine Link-Umwandlung ausgeführt werden soll.
    private void okPressed() {
        if (linkEditText.getText().toString() != null && !linkEditText.getText().toString().equals("")) {
            linkOkPressed();
        } else if (searchEditText.getText().toString() != null && !searchEditText.getText().toString().equals("")) {
            searchOkPressed();
        } else {
            invalidLinkMessage();
        }
    }

    /*
     * Soll eine Suche ausgeführt werden, wird der Inhalt der Suchleiste an die Spotify Web API
     * übergeben, die nach der ID des bestmöglichen Ergebnisses sucht.
     * Auf Basis dieser ID werden dann die SongUri und die Views aktualisiert.
     */
    private void searchOkPressed() {
        if (spotify != null) {
            spotify.searchTracks(searchEditText.getText().toString(), new Callback<TracksPager>() {
                @Override
                public void success(TracksPager tracksPager, Response response) {
                    searchEditText.setText("");
                    if (tracksPager.tracks.items.size() > 0) {
                        String songId = tracksPager.tracks.items.get(0).id;
                        setSongUri(songId);
                    } else {
                        sendNoItemsFoundMessage();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    sendCantReachSpotifyError();
                }
            });
        } else {
            sendCantReachSpotifyError();
        }
    }

    /*
     * Soll ein Link umgewandelt werden wird überprüft ob es sich um einen Link zu einem Spotify-Song
     * handelt. Falls ja wird die Id des Songs aus dem Link extrahiert und in die SongUri gespeichert
     * und die Views aktualisiert.
     */
    private void linkOkPressed() {
        String pastedLink = linkEditText.getText().toString();
        String[] splits = pastedLink.split("/");
        if (splits.length > 3) {
            if (splits[3].equals("track")) {
                //logSplit(splits);
                String[] secondSplit = splits[4].split("\\Q?\\E");
                //logSecondSplit(secondSplit);
                setSongUri(secondSplit[0]);
                linkEditText.setText("");
            } else {
                invalidLinkMessage();
            }
        } else {
            invalidLinkMessage();
        }
    }

    private void sendNoItemsFoundMessage() {
        Toast.makeText(this, "Can't find song", Toast.LENGTH_SHORT).show();
    }

    private void sendCantReachSpotifyError() {
        Toast.makeText(this, "Can not reach Spotify, please check connection.", Toast.LENGTH_SHORT).show();
    }

    private void invalidLinkMessage() {
        Toast.makeText(this, "Please enter a valid Link to a Song on Spotify", Toast.LENGTH_SHORT).show();
    }

    /*
     * Aus einer trackId wird eine SongUri gemacht, mit der der Song abgespielt werden kann.
     * Außerdem werden mit Hilfe der Spotify Web API die Informationen über den Track geladen und
     * die Views entsprechend angepasst.
     */
    private void setSongUri(String trackId) {
        songUri = "spotify:track:" + trackId;
        //Log.d("Spotify", "songUri is set to: " + songUri);
        //appRemote.getPlayerApi().play(songUri);
        playButton.setVisibility(View.VISIBLE);
        spotify.getTrack(trackId, new Callback<Track>() {
            @Override
            public void success(Track track, Response response) {
                //Log.d("Spotify", "Title of track: " + track.name);
                refreshViews(track);
            }

            @Override
            public void failure(RetrofitError error) {
                //Log.d("Spotify", "Error getting track information");
                sendCantReachSpotifyError();
            }
        });
    }

    private void refreshViews(Track track) {
        trackNameView.setText(track.name);
        artistNameView.setText(track.artists.get(0).name);
        trackNameView.setVisibility(View.VISIBLE);
        artistNameView.setVisibility(View.VISIBLE);
    }

    private void logSecondSplit(String[] secondSplit) {
        Log.d("Spotify", "Second Split:");
        Log.d("Spotify", "0: " + secondSplit[0]);
        Log.d("Spotify", "1: " + secondSplit[1]);
    }

    private void logSplit(String[] splits) {
        Log.d("Spotify", Arrays.toString(splits));
        Log.d("Spotify", "0: " + splits[0]);
        Log.d("Spotify", "1: " + splits[1]);
        Log.d("Spotify", "2: " + splits[2]);
        Log.d("Spotify", "3: " + splits[3]);
        Log.d("Spotify", "4: " + splits[4]);
    }


}
