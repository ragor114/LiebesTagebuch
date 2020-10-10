package ur.mi.liebestagebuch.database;

import java.util.List;

import ur.mi.liebestagebuch.database.data.DBEntry;

public interface DatabaseListener {

    void updateFinished(int updateCode);

    void entryFound(DBEntry foundEntry);

    void allEntriesFound(List<DBEntry> allEntries);

}
