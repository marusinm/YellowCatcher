package sk.meetz.zlty_odchytavac.results;

/**
 * Created by Marek on 12/08/16.
 */
public class FindedRoute {

    private long        stationFromId;
    private long        stationToId;
    private int         seats;
    private String      arrival;
    private String      departure;
    private String      price;
    private String      tarif;
    private String      type;
    private String      date;
    private String      currency;
    private String      userId;
    private String      routeId;
    private boolean     isSaved;


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

    public String getDate() {return date;}
    public void   setDate(String date){this.date = date ;}

    public String getCurrency() {return currency;}
    public void   setCurrency(String currency){this.currency = currency ;}

    public String getUserId() {return userId;}
    public void   setUserId(String userId){this.userId = userId ;}

    public String getRouteId() {return routeId;}
    public void   setRouteId(String routeId){this.routeId = routeId ;}

    public boolean getIsSaved() {return isSaved;}
    public void   setIsSaved(boolean isSaved){this.isSaved = isSaved;}

}
