package ur.mi.liebestagebuch.Boxes;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import ur.mi.liebestagebuch.DetailAndEditActivity.DetailActivityConfig;
import ur.mi.liebestagebuch.R;

public class SpotifyBox implements Box {

    private String content;

    private String songTitleString;
    private String songArtistString;
    private View.OnClickListener playButtonOnClickListener;

    private SpotifyAppRemote appRemote;
    private SpotifyApi api;
    private SpotifyService spotify;

    private SpotifyBoxReadyListener listener;
    private boolean textViewssetUp = false;

    public SpotifyBox(String songUri, Context context, SpotifyBoxReadyListener listener){
        this.content = songUri;
        this.listener = listener;
        Log.d("Spotify", "New SongUri set to: " + this.content);

        setUpAppRemoteConnection(context);

        if(DetailActivityConfig.ACCESS_TOKEN == null || DetailActivityConfig.ACCESS_TOKEN.equals("")){
            listener.needsAccessToken();
        } else{
            setUpSpotifyWebApi();
        }
    }

    private void setUpAppRemoteConnection(Context context) {
        ConnectionParams connectionParams = new ConnectionParams.Builder(DetailActivityConfig.CLIENT_ID)
                .setRedirectUri(DetailActivityConfig.REDIRECT_URI)
                .showAuthView(true)
                .build();
        Log.d("Spotify", "ConnectionParams built");
        SpotifyAppRemote.connect(context, connectionParams, new Connector.ConnectionListener() {
            @Override
            public void onConnected(SpotifyAppRemote spotifyAppRemote){
                appRemote = spotifyAppRemote;
                //DEBUG: appRemote.getPlayerApi().play("spotify:playlist:6uJdeXLzNtFPEhuZ0XFid0");
                Log.d("Spotify", "App Remote connected!");
                if(playButtonOnClickListener == null){
                    setPlayButtonClickListener();
                    listener.updatedViews();
                }
            }
            @Override
            public void onFailure(Throwable throwable) {
                Log.d("Spotify", "App Remote could not connect: " + throwable.getMessage());
            }
        });
    }

    public void gotAccessToken(){
        setUpSpotifyWebApi();
    }

    private void setUpSpotifyWebApi() {
        api = new SpotifyApi();
        api.setAccessToken(DetailActivityConfig.ACCESS_TOKEN);
        spotify = api.getService();
        if(songTitleString == null){
            fillInTextViews();
        }
    }

    @Override
    public String getString() {
        return content;
    }

    @Override
    public Type getType() {
        return Type.MUSIC;
    }

    @Override
    public View getView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View convertView = inflater.inflate(R.layout.music_box_layout, null);

        TextView songTitleView = (TextView) convertView.findViewById(R.id.music_box_song_title);;
        TextView songArtistView = (TextView) convertView.findViewById(R.id.music_box_song_artist);
        ImageButton playButtonView = (ImageButton) convertView.findViewById(R.id.music_box_play_button);

        if(songTitleString != null){
            songTitleView.setText(songTitleString);
            songArtistView.setText(songArtistString);
        } else{
            setUpSpotifyWebApi();
        }

        if(playButtonOnClickListener != null){
            playButtonView.setOnClickListener(playButtonOnClickListener);
        }

        if(appRemote != null){
            setPlayButtonClickListener();
        }

        if(spotify != null){
            fillInTextViews();
        }

        playButtonView.setFocusable(false);
        playButtonView.setFocusableInTouchMode(false);

        return convertView;
    }

    private void fillInTextViews() {
        Log.d("Spotify", "Filling in TextViews");
        if(textViewssetUp == false) {
            String[] splits = content.split(":");
            String trackId = splits[2];
            Log.d("Spotify", "trackId is: " + trackId);
            spotify.getTrack(trackId, new Callback<Track>() {
                @Override
                public void success(Track track, Response response) {
                    songTitleString = track.name;
                    songArtistString = track.artists.get(0).name;
                    listener.updatedViews();
                }
                @Override
                public void failure(RetrofitError error) {
                    Log.d("Spotify", "Connection Error!");
                }
            });
        }
    }

    private void setPlayButtonClickListener() {
        playButtonOnClickListener = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                appRemote.getPlayerApi().play(content);
            }
        };
        Log.d("Spotify", "PlayButton onClickListener set.");
    }

    @Override
    public void setContent(String content) {
        this.content = content;
        fillInTextViews();
    }
}
