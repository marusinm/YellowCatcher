package sk.meetz.zlty_odchytavac;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import sk.meetz.zlty_odchytavac.realm.Tarif;

/**
 * Created by Marek on 12/08/16.
 */
public class ZltyOdchytavac extends Application {

    //api doc: http://46.101.209.48:5000/documentation
//    public static String SERVER_URL = "http://46.101.209.48:5000";
//    public static String SERVER_URL = "http://odchytavac.com:5000";
    public static String SERVER_URL = "http://odchytavac.com/api";
    public static String STATIONS_URL = SERVER_URL+"/stations";
    public static String ROUTES_URL = SERVER_URL+"/routes";
    public static String ADD_NEW_ROAD_URL = SERVER_URL+"/newTrackingRoute";
    public static String SAVED_ROUTES_URL = SERVER_URL+"/userRoutes";
    public static String DELETE_ROUTE_URL = SERVER_URL+"/deleteSavedRoute";
    public static String ONE_ROUTE_URL = SERVER_URL+"/route";

    public static String GCM_TOKEN = ""; //token priradime v mainactivity

    //tarify aj s ich klucmi su nahadzane rucne, nie su stahovane z api (napr. Dospely, Isic ...)
    public static HashMap<String, String> TARIF = new HashMap<>();

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        // The realm file will be located in Context.getFilesDir() with name "default.realm"
        RealmConfiguration config = new RealmConfiguration.Builder(this)
                .name("zlty_odchytavac.realm")
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);

        TARIF.put("REGULAR",                  "Dospelý");
        TARIF.put("CZECH_STUDENT_PASS_26",    "Žiacky preukaz <26");
        TARIF.put("CZECH_STUDENT_PASS_15",    "Žiacky preukaz <15");
        TARIF.put("ISIC",                     "ISIC");
        TARIF.put("CHILD",                    "Dieťa <15");
//        TARIF.put("ATTENDED_CHILD",           "Dieťa 0-6 s doprovodom");
        TARIF.put("SENIOR",                   "Senior >60");
        TARIF.put("SENIOR_70",                "Senior >70");
        TARIF.put("YOUTH",                    "Mládežník <26");
        TARIF.put("DISABLED",                 "ŤZP (ŤZP/S)");
        TARIF.put("DISABLED_ATTENDANCE",      "Sprievodca ŤZP/S");
        TARIF.put("EURO26",                   "Euro 26/Alive");

        //vsetky tarify vlozime do databazy pokial tam este nie su vlozene
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Tarif> tarifs = realm.where(Tarif.class).findAll();
        if (tarifs.size() == 0) {
            realm.beginTransaction();

            for (Map.Entry<String, String> entry : TARIF.entrySet()) {
                Tarif tarif = new Tarif();
                tarif.setKey(entry.getKey());
                tarif.setTitle(entry.getValue());

                //create id for tarif
                int id;
                try {
                    id = realm.where(Tarif.class).max("id").intValue() + 1; //nieco ako autoinkrement
                    tarif.setId(id);
                } catch (Exception ex) {
                    tarif.setId(0);
                }

                realm.copyToRealmOrUpdate(tarif);
            }
            realm.commitTransaction();
        }
    }

    /**
     * check if network is available
     * @param ctx Context
     * @return true if network is available.
     */
    public static boolean isNetworkAvailable(Context ctx) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * http://jizdenky.studentagency.sk/m/Booking/from/"+stationFrom.id+"/to/"+stationTo.id+"/tarif/"+tarif+"/departure/"+dateFormatted+"/retdep/"+dateFormatted+"/return/False/credit/True
     *
     * e.g. http://jizdenky.studentagency.sk/m/Booking/from/10202036/to/10202001/tarif/REGULAR/departure/20160829/retdep/20160829/return/False/credit/True
     *
     * @return composed url to buy ticket
     */
    public static String composeUrlBuyUrl(long fromStationId, long toStationId, String tarif, String departure){
        String buy_url = "http://jizdenky.studentagency.sk/m/Booking";
        buy_url += "/from/"+fromStationId+"/to/"+toStationId+"/tarif/"+tarif+"/departure/"+departure+"/retdep/"+departure+"/return/False/credit/True";
        return buy_url;
    }


    /**
     * method formate data string
     * @param inputFormat old date format
     * @param outputFormat new date format
     * @param inputDate date in old format
     * @return date in new format
     */
    public static String formateDateFromstring(String inputFormat, String outputFormat, String inputDate){

        Date parsed;
        String outputDate = "";

        SimpleDateFormat df_input = new SimpleDateFormat(inputFormat, java.util.Locale.getDefault());
        SimpleDateFormat df_output = new SimpleDateFormat(outputFormat, java.util.Locale.getDefault());

        try {
            parsed = df_input.parse(inputDate);
            outputDate = df_output.format(parsed);

        } catch (ParseException e) {
            Log.e("parse date", e.toString());
        }

        return outputDate;

    }
}
