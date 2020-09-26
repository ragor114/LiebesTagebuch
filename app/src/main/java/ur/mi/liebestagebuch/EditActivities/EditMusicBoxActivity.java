package ur.mi.liebestagebuch.EditActivities;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

public class EditMusicBoxActivity extends AppCompatActivity {

    private static final String CLIENT_ID = "ce7c78a74aa7477989eae35bc8725657";
    private static final String REDIRECT_URI = "ur.mi.liebestagebuch://callback";
    private SpotifyAppRemote appRemote;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ConnectionParams connectionParams = new ConnectionParams.Builder(CLIENT_ID)
                .setRedirectUri(REDIRECT_URI)
                .showAuthView(true)
                .build();
        SpotifyAppRemote.connect(this, connectionParams, new Connector.ConnectionListener() {
            @Override
            public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                appRemote = spotifyAppRemote;
                Log.d("Spotify", "App Remote connected!");
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.d("Spotify", "App Remote could not connect: " + throwable.getMessage());
            }
        });
    }
}
