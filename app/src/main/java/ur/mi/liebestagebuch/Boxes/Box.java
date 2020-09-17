package ur.mi.liebestagebuch.Boxes;

import android.content.Context;
import android.view.View;

public interface Box {

    public String getString();
    public Type getType();
    public View getView(Context context);
    public void setContent(String content);

}
