package sk.meetz.zlty_odchytavac.realm;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Marek on 25/08/16.
 */
public class LastRoute extends RealmObject {
    
    @PrimaryKey
    private int         id;

    private long        stationFromId;
    private long        stationToId;
    private long         timestamp; // bude sluzit na zoradenie poloziek

    // Standard getters & setters generated ...
    public int   getId() { return id; }
    public void   setId(int id) { this.id = id; }
    
    public long   getStationFromId() { return stationFromId; }
    public void   setStationFromId(long stationFromId) { this.stationFromId = stationFromId; }

    public long   getStationToId() { return stationToId; }
    public void   setStationToId(long stationToId) { this.stationToId = stationToId; }

    public long    getTimestamp() { return timestamp; }
    public void   setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
