package sk.meetz.zlty_odchytavac.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Marek on 12/08/16.
 */
public class Station extends RealmObject {

    @PrimaryKey
    private long             id;

    private String          title;
    private String          countryCode;
    private String          longitude;
    private String          latitude;
    private String          priority;
    private String          titleAscii; // title without diacritics

    // Standard getters & setters generated ...
    public long    getId() { return id; }
    public void   setId(long id) { this.id = id; }

    public String getTitle() {return title;}
    public void   setTitle(String title){this.title = title;}

    public String getCountryCode() {return countryCode;}
    public void   setCountryCode(String countryCode){this.countryCode = countryCode;}

    public String getLongitude() {return longitude;}
    public void   setLongitude(String longitude){this.longitude = longitude;}

    public String getLatitude() { return latitude; }
    public void   setLatitude(String latitude) { this.latitude = latitude; }

    public String getPriority() { return priority; }
    public void   setPriority(String priority) { this.priority = priority; }

    public String getTitleAscii() {return titleAscii;}
    public void   setTitleAscii(String titleAscii){this.titleAscii = titleAscii ;}

}
