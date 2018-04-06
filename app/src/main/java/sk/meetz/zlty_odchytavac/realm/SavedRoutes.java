package sk.meetz.zlty_odchytavac.realm;

import io.realm.RealmObject;

/**
 * Created by Marek on 13/08/16.
 */
//// FIXME: 13/08/16 mozno sa to vobec nebude riesit takymto sposobom, treba prekonzultovat s patrikom ako si prestavuje refreshovanie miest v ulozenych cestach
public class SavedRoutes extends RealmObject {

    private long        stationFromId;
    private long        stationToId;
    private int         seats;
    private String      arrival;
    private String      departure;
    private String      price;
    private String      tarif;
    private String      type;
    
    // Standard getters & setters generated ...
    public long   getStationFromId() { return stationFromId; }
    public void   setStationFromId(long stationFromId) { this.stationFromId = stationFromId; }

    public long   getStationToId() { return stationToId; }
    public void   setStationToId(long stationToId) { this.stationToId = stationToId; }

    public int    getSeats() {return seats;}
    public void   setSeats(int seats){this.seats = seats;}

    public String getArrival() {return arrival;}
    public void   setArrival(String arrival){this.arrival = arrival;}

    public String getDeparture() {return departure;}
    public void   setDeparture(String departure){this.departure = departure; }

    public String getPrice() { return price; }
    public void   setPrice(String price) { this.price = price; }

    public String getTarif() { return tarif; }
    public void   setTarif(String tarif) { this.tarif = tarif; }

    public String getType() {return type;}
    public void   setType(String type){this.type = type ;}
}
