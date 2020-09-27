package ur.mi.liebestagebuch.EditActivities;

import android.content.Intent;
import android.os.Bundle;
import android.telecom.Call;
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
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import ur.mi.liebestagebuch.DetailAndEditActivity.DetailActivityConfig;
import ur.mi.liebestagebuch.R;

public class EditMusicBoxActivity extends AppCompatActivity {

    private SpotifyAppRemote appRemote;

    private EditText linkEditText;
    private Button linkOkButton;
    private ImageButton playButton;
    private ImageButton finishButton;
    private TextView trackNameView;
    private TextView artistNameView;

    private String songUri;
    private SpotifyApi api;
    private SpotifyService spotify;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_music_box_activity);
        Log.d("Spotify", "EditMusicBox created");

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

        if(songUri.equals("")){
            playButton.setVisibility(View.INVISIBLE);
            trackNameView.setVisibility(View.INVISIBLE);
            artistNameView.setVisibility(View.INVISIBLE);
        }

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

        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(DetailActivityConfig.CLIENT_ID, AuthenticationResponse.Type.TOKEN, DetailActivityConfig.REDIRECT_URI);
        builder.setScopes(new String[]{"streaming"});
        AuthenticationRequest request = builder.build();
        AuthenticationClient.openLoginActivity(this, DetailActivityConfig.SPOTIFY_AUTH_REQUEST_CODE, request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == DetailActivityConfig.SPOTIFY_AUTH_REQUEST_CODE){
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, data);
            switch (response.getType()){
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

    private void setUpSpotifyWebApi() {
        api = new SpotifyApi();
        api.setAccessToken(DetailActivityConfig.ACCESS_TOKEN);
        spotify = api.getService();
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
        spotify.getTrack(trackId, new Callback<Track>() {
            @Override
            public void success(Track track, Response response) {
                Log.d("Spotify", "Title of track: " + track.name);
                trackNameView.setText(track.name);
                artistNameView.setText(track.artists.get(0).name);
                trackNameView.setVisibility(View.VISIBLE);
                artistNameView.setVisibility(View.VISIBLE);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("Spotify", "Error getting track information");
                Toast.makeText(getApplicationContext(), "Error getting Song Information, check connection and try again later.", Toast.LENGTH_SHORT).show();;
            }
        });
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
