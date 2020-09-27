package ur.mi.liebestagebuch.Boxes;

import android.content.Context;
import android.view.View;

public class SpotifyBox implements Box {

    String content;

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
        return null;
    }

    @Override
    public void setContent(String content) {

    }
}
