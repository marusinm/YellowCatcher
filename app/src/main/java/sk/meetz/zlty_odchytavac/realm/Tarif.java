package sk.meetz.zlty_odchytavac.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Marek on 12/08/16.
 */
public class Tarif extends RealmObject {

    @PrimaryKey
    private int             id;

    private String          title;
    private String          key;


    // Standard getters & setters generated ...
    public int    getId() { return id; }
    public void   setId(int id) { this.id = id; }

    public String getTitle() {return title;}
    public void   setTitle(String title){this.title = title;}

    public String getKey() {return key;}
    public void   setKey(String key){this.key = key;}


}