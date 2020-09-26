package ur.mi.liebestagebuch.EditActivities;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import ur.mi.liebestagebuch.R;

public class EditMusicBoxActivity extends AppCompatActivity {

    private static final String CLIENT_ID = "ce7c78a74aa7477989eae35bc8725657";
    private static final String REDIRECT_URI = "ur.mi.liebestagebuch://callback";
    private SpotifyAppRemote appRemote;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_music_box_activity);
        Log.d("Spotify", "EditMusicBox created");
    }

    @Override
    protected void onStart() {
        super.onStart();
        ConnectionParams connectionParams = new ConnectionParams.Builder(CLIENT_ID)
                .setRedirectUri(REDIRECT_URI)
                .showAuthView(true)
                .build();
        Log.d("Spotify", "ConnectionParams built");
        SpotifyAppRemote.connect(this, connectionParams, new Connector.ConnectionListener() {
            @Override
            public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                appRemote = spotifyAppRemote;
                appRemote.getPlayerApi().play("spotify:playlist:6uJdeXLzNtFPEhuZ0XFid0");
                Log.d("Spotify", "App Remote connected!");
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.d("Spotify", "App Remote could not connect: " + throwable.getMessage());
            }
        });
        Log.d("Spotify", "Tried to connect");
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
