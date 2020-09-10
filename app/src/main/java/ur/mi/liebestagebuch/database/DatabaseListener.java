package ur.mi.liebestagebuch.database;

import ur.mi.liebestagebuch.database.data.Entry;

public interface DatabaseListener {

    void updateFinished(int updateCode);

    void entryFound(Entry foundEntry);

}
