package ur.mi.liebestagebuch.Boxes;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import ur.mi.liebestagebuch.DetailAndEditActivity.DetailActivityConfig;
import ur.mi.liebestagebuch.R;

public class SpotifyBox implements Box {

    /*
     * Eine SpotifyBox repräsentiert einen Song im ListView. Dabei werden ein Play-Button, der den
     * Song mit der Spotify-App abspielt und der Titel und Künstler des Songs angezeigt.
     * Damit die SpotifyBox funktioniert sind auf Grund des Spotify SDK und der Spotify Web API
     * einige Bedingungen zu erfüllen, so muss die Spotify App installiert sein, man muss mit einem
     * Premium Konto angemeldet sein, man muss Internet-Verbindung haben und die Spotify-App darf
     * nicht im Online Modus sein. Sind diese Bedingungen nicht erfüllt funktionieren die Elemente
     * der SpotifyBox nur teilweise oder gar nicht.
     *
     * Entwickelt von Jannik Wiese.
     */

    private String songUri;

    private String songTitleString;
    private String songArtistString;
    private View.OnClickListener playButtonOnClickListener;

    private SpotifyAppRemote appRemote;
    private SpotifyApi api;
    private SpotifyService spotify;

    private SpotifyBoxReadyListener listener;

    /*
     * Da das Spotify SDK nur mit einem context Objekt funktioniert und die Abfrage der Songinformationen
     * asynchron läuft, müssen ein Context und ein Listener zusätzlich zur songUri übergeben werden.
     */
    public SpotifyBox(String songUri, Context context, SpotifyBoxReadyListener listener) {
        this.songUri = songUri;
        this.listener = listener;
        Log.d("Spotify", "New SongUri set to: " + this.songUri);

        setUpAppRemoteConnection(context);

        // Ist noch kein Access Token für die Spotify Web API vorhanden wird der Listener gebeten einen
        // an zu fordern, dieser muss sich dann bei der Spotify Box zurück melden.
        if (DetailActivityConfig.ACCESS_TOKEN == null || DetailActivityConfig.ACCESS_TOKEN.equals("")) {
            listener.needsAccessToken();
        } else {
            setUpSpotifyWebApi();
        }
    }

    // Die Spotify App Remote wird mit der App verbunden und wenn das geglückt ist, kann der
    // OnClickListener des PlayButtons initialisiert werden.
    private void setUpAppRemoteConnection(Context context) {
        ConnectionParams connectionParams = new ConnectionParams.Builder(context.getString(R.string.spotify_client_id))
                .setRedirectUri(context.getResources().getString(R.string.spotify_redirect_uri))
                .showAuthView(true)
                .build();
        Log.d("Spotify", "ConnectionParams built");
        SpotifyAppRemote.connect(context, connectionParams, new Connector.ConnectionListener() {
            @Override
            public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                appRemote = spotifyAppRemote;
                //DEBUG: appRemote.getPlayerApi().play("spotify:playlist:6uJdeXLzNtFPEhuZ0XFid0");
                Log.d("Spotify", "App Remote connected!");
                if (playButtonOnClickListener == null) {
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

    // Meldet der Listener, das der Access Token vorhanden ist kann die Spotify Web API initialisiert werden.
    public void gotAccessToken() {
        setUpSpotifyWebApi();
    }

    // Ist die Spotify Web API initialisert können die Inhalte der TextViews aus dem Internet geladen werden.
    private void setUpSpotifyWebApi() {
        api = new SpotifyApi();
        api.setAccessToken(DetailActivityConfig.ACCESS_TOKEN);
        spotify = api.getService();
        if (songTitleString == null) {
            fillInTextViews();
        }
    }

    @Override
    public String getString() {
        return songUri;
    }

    @Override
    public Type getType() {
        return Type.MUSIC;
    }

    /*
     * Sind onClickListener oder Strings schon vorhanden werden diese in die entsprechenden Views
     * ändern sich die Inhalte ändern sich demnach auch die Anzeigen in den Views.
     */
    @Override
    public View getView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View convertView = inflater.inflate(R.layout.music_box_layout, null);

        TextView songTitleView = (TextView) convertView.findViewById(R.id.music_box_song_title);
        ;
        TextView songArtistView = (TextView) convertView.findViewById(R.id.music_box_song_artist);
        ImageButton playButtonView = (ImageButton) convertView.findViewById(R.id.music_box_play_button);

        if (songTitleString != null) {
            songTitleView.setText(songTitleString);
            songArtistView.setText(songArtistString);
        } else {
            setUpSpotifyWebApi();
        }

        if (playButtonOnClickListener != null) {
            playButtonView.setOnClickListener(playButtonOnClickListener);
        }

        if (appRemote != null) {
            setPlayButtonClickListener();
        }

        if (spotify != null) {
            fillInTextViews();
        }

        //Android erlaubt keine fokussierbaren Elemente in ListViews, daher muss der Button bei jedem
        // Aufruf von getView nicht fokussierbar gemacht werden.
        playButtonView.setFocusable(false);
        playButtonView.setFocusableInTouchMode(false);

        return convertView;
    }

    // Die Inhalte der TextViews werden geladen und in den Strings gespeichert, der Listener wird
    // über eine Veränderung informiert.
    private void fillInTextViews() {
        Log.d("Spotify", "Filling in TextViews");
        String[] splits = songUri.split(":");
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

    // Beim Klick auf den PlayButton wird der entsprechende Song gespielt.
    private void setPlayButtonClickListener() {
        playButtonOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appRemote.getPlayerApi().play(songUri);
            }
        };
        Log.d("Spotify", "PlayButton onClickListener set.");
    }

    @Override
    public void setContent(String content) {
        this.songUri = content;
        fillInTextViews();
    }
}
