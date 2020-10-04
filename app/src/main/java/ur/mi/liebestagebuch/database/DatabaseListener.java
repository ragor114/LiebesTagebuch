package ur.mi.liebestagebuch.database;

import java.util.List;

import ur.mi.liebestagebuch.database.data.Entry;

public interface DatabaseListener {

    void updateFinished(int updateCode);

    void entryFound(Entry foundEntry);

    void allEntriesFound(List<Entry> allEntries);

}
