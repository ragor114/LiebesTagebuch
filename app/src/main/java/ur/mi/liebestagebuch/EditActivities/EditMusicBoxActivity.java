package ur.mi.liebestagebuch.EditActivities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import java.util.Arrays;

import ur.mi.liebestagebuch.DetailAndEditActivity.DetailActivityConfig;
import ur.mi.liebestagebuch.R;

public class EditMusicBoxActivity extends AppCompatActivity {

    private SpotifyAppRemote appRemote;

    private EditText linkEditText;
    private Button linkOkButton;
    private ImageButton playButton;
    private ImageButton finishButton;

    private String songUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_music_box_activity);
        Log.d("Spotify", "EditMusicBox created");

        songUri = "";

        playButton = findViewById(R.id.edit_spotify_play);
        if(songUri.equals("")){
            playButton.setVisibility(View.INVISIBLE);
        }
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appRemote.getPlayerApi().play(songUri);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        ConnectionParams connectionParams = new ConnectionParams.Builder(DetailActivityConfig.CLIENT_ID)
                .setRedirectUri(DetailActivityConfig.REDIRECT_URI)
                .showAuthView(true)
                .build();
        Log.d("Spotify", "ConnectionParams built");
        SpotifyAppRemote.connect(this, connectionParams, new Connector.ConnectionListener() {
            @Override
            public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                appRemote = spotifyAppRemote;
                //DEBUG: appRemote.getPlayerApi().play("spotify:playlist:6uJdeXLzNtFPEhuZ0XFid0");
                setUpViews();
                Log.d("Spotify", "App Remote connected!");
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.d("Spotify", "App Remote could not connect: " + throwable.getMessage());
            }
        });
        Log.d("Spotify", "Tried to connect");
    }

    private void setUpViews() {
        linkEditText = findViewById(R.id.spotify_link_edit);
        linkOkButton = findViewById(R.id.spotify_link_ok);
        finishButton = findViewById(R.id.finish_spotify_edit);

        linkOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linkOkPressed();
            }
        });
    }

    private void linkOkPressed() {
        String pastedLink = linkEditText.getText().toString();
        String[] splits = pastedLink.split("/");
        if(splits.length > 3) {
            if (splits[3].equals("track")) {
                logSplit(splits);
                String[] secondSplit = splits[4].split("\\Q?\\E");
                logSecondSplit(secondSplit);
                setSongUri(secondSplit[0]);
                linkEditText.setText("");
            } else {
                invalidLinkMessage();
            }
        } else{
            invalidLinkMessage();
        }
    }

    private void invalidLinkMessage() {
        Toast.makeText(this, "Please enter a valid Link to a Song on Spotify", Toast.LENGTH_SHORT).show();
    }

    private void setSongUri(String trackId) {
        songUri = "spotify:track:" + trackId;
        Log.d("Spotify", "songUri is set to: " + songUri);
        //appRemote.getPlayerApi().play(songUri);
        playButton.setVisibility(View.VISIBLE);
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

    @Override
    protected void onStop() {
        super.onStop();
    }
}
