package sk.meetz.zlty_odchytavac.results;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;
import sk.meetz.zlty_odchytavac.R;
import sk.meetz.zlty_odchytavac.ZltyOdchytavac;
import sk.meetz.zlty_odchytavac.filter.FilterRecyclerViewAdapter;
import sk.meetz.zlty_odchytavac.realm.LastRoute;
import sk.meetz.zlty_odchytavac.realm.Station;
import sk.meetz.zlty_odchytavac.realm.Tarif;

public class Results extends AppCompatActivity {

    ArrayList<FindedRoute> findedRoutes = new ArrayList<>();
    Toolbar toolbar;
    RecyclerView recycler_view;
    ProgressBar progress;
    ProgressBar action_progress;
    LinearLayout no_route_founded;

    long fromStationId;
    long toStationId;
    String currency = "EUR";
    String date = null;
    String dateInOldFormat = null;
    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        realm = Realm.getDefaultInstance();

        //set font
        TextView toolbar_title = (TextView)findViewById(R.id.toolbar_title);
        toolbar_title.setTypeface(Typeface.createFromAsset(getAssets(), "Blogger Sans-Bold.ttf"));
        TextView from_title = (TextView)findViewById(R.id.from_title);
        from_title.setTypeface(Typeface.createFromAsset(getAssets(), "Blogger Sans-Bold.ttf"));
        TextView from = (TextView)findViewById(R.id.from);
        from.setTypeface(Typeface.createFromAsset(getAssets(), "Blogger Sans-Bold.ttf"));
        TextView to_title = (TextView)findViewById(R.id.to_title);
        to_title.setTypeface(Typeface.createFromAsset(getAssets(), "Blogger Sans-Bold.ttf"));
        TextView to = (TextView)findViewById(R.id.to);
        to.setTypeface(Typeface.createFromAsset(getAssets(), "Blogger Sans-Bold.ttf"));
        TextView day_title = (TextView)findViewById(R.id.day_title);
        day_title.setTypeface(Typeface.createFromAsset(getAssets(), "Blogger Sans-Bold.ttf"));
        TextView day = (TextView)findViewById(R.id.day);
        day.setTypeface(Typeface.createFromAsset(getAssets(), "Blogger Sans-Bold.ttf"));


        // if connection does not founded we show picture (this is selection of picture language)
        ImageView img_missed = (ImageView)findViewById(R.id.img_missed);
        Locale.getDefault().getLanguage();
        String language = Locale.getDefault().getDisplayLanguage();
        System.out.println("langu: "+language);
        if (language.equals("English")){
            img_missed.setImageResource(R.drawable.plcauci_clovek_en);
        }else if(language.equals("Slovak")){
            img_missed.setImageResource(R.drawable.placuci_clovek_sk);
        }else if(language.equals("Czech")){
            img_missed.setImageResource(R.drawable.placuci_clovek_cz);
        }else{
            img_missed.setImageResource(R.drawable.plcauci_clovek_en);
        }

        progress = (ProgressBar)findViewById(R.id.progress);
        no_route_founded = (LinearLayout) findViewById(R.id.no_routes_founded);
        no_route_founded.setVisibility(View.GONE);
        action_progress = (ProgressBar)findViewById(R.id.action_progress);
        action_progress.setVisibility(View.GONE);


        //get arguments from previous activity and download new data
        Bundle extras = getIntent().getExtras();
        if(extras !=null)
        {
            //first we format date
            dateInOldFormat = extras.getString("date");
            String formated_date = formateDateFromstring("dd/mm/yyyy", "yyyymmdd", extras.getString("date"));
            System.out.println("formared: "+formated_date);

            downloadAndParseData(
                extras.getLong("from_id"),
                extras.getLong("to_id"),
                extras.getString("tarif"),
                formated_date,
                extras.getString("currency"));

            fromStationId = extras.getLong("from_id");
            toStationId = extras.getLong("to_id");
            currency = extras.getString("currency");
            date = extras.getString("date");
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icon_close_wh);


        from.setText(realm.where(Station.class).equalTo("id",fromStationId).findFirst().getTitle());
        to.setText(realm.where(Station.class).equalTo("id",toStationId).findFirst().getTitle());
        day.setText(date);

        recycler_view = (RecyclerView) findViewById(R.id.recycler_view);
        recycler_view.setLayoutManager(new LinearLayoutManager(Results.this));
        ResultsRecyclerViewAdapter rcAdapter = new ResultsRecyclerViewAdapter(findedRoutes, Results.this);
        recycler_view.setAdapter(rcAdapter);


        //code has been moved to MainActiviti Floating button click listener
        //save last finding routes to realm with timestamp
//        boolean isThisRouteExists = false;
//        RealmResults<LastRoute> lastRoutes = realm.where(LastRoute.class).findAll();
//        realm.beginTransaction();
//        if (lastRoutes.size() > 0){
//            //upravime timestamp
//            for (LastRoute lastRoute : lastRoutes) {
//                if (lastRoute.getStationFromId() == fromStationId) {
//                    if (lastRoute.getStationToId() == toStationId) {
//
//                        long seconds = System.currentTimeMillis()/1000;
//                        lastRoute.setTimestamp(seconds);
//                        realm.copyToRealmOrUpdate(lastRoute);
//                        isThisRouteExists = true;
//                        break;
//                    }
//                }else {
//                    isThisRouteExists = false;
//                }
//            }
//        }
//        if (!isThisRouteExists || lastRoutes.size() == 0){
//            LastRoute lastRoute = new LastRoute();
//            lastRoute.setStationFromId(fromStationId);
//            lastRoute.setStationToId(toStationId);
//
//            int id;
//            try {
//                id = realm.where(LastRoute.class).max("id").intValue() + 1; //nieco ako autoinkrement
//                lastRoute.setId(id);
//            } catch (Exception ex) {
//                lastRoute.setId(0);
//            }
//
//
//            long seconds = System.currentTimeMillis()/1000;
//            lastRoute.setTimestamp(seconds);
//            realm.copyToRealmOrUpdate(lastRoute);
//        }
//        realm.commitTransaction();
    }

    /**
     * @param fromId id of start point station
     * @param toId id of target station
     * @param tarif e.g. REGULAR (Doslpely) ...
     * @param date format yyyymmdd
     * @param currency CZK or EUR
     */
    private void downloadAndParseData(long fromId, long toId, String tarif, final String date, final String currency){
        //vyskladame si url podla dokumentacie http://46.101.209.48:5000/documentation
        //napr:http://46.101.209.48:5000/routes/from/10204038/to/10204002/tarif/REGULAR/date/20160813/currency/CZK

        String url = ZltyOdchytavac.ROUTES_URL+"/from/"+fromId+"/to/"+toId+"/tarif/"+tarif+"/date/"+date+"/currency/"+currency+"/user_token/"+ZltyOdchytavac.GCM_TOKEN;
        System.out.println("url: "+url);
        Ion.with(this)
                .load(url)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {

                        System.out.println("result: "+result);

                        progress.setVisibility(View.GONE);

                        try {

                            if(result == null ){
                                throw new RuntimeException("Server error");
                            }

                            JSONArray arr = new JSONArray(result);
                            for (int i = 0; i < arr.length(); i++) {
                                JSONObject obj = arr.getJSONObject(i);

                                try {

                                    FindedRoute findedRoute = new FindedRoute();
                                    findedRoute.setStationFromId(obj.getLong("stationFrom"));
                                    findedRoute.setStationToId(obj.getLong("stationTo"));
                                    findedRoute.setSeats(obj.getInt("seats"));
                                    findedRoute.setArrival(obj.getString("arrival"));
                                    findedRoute.setDeparture(obj.getString("departure"));
                                    findedRoute.setPrice(obj.getString("price"));
                                    findedRoute.setTarif(obj.getString("tarif"));
                                    findedRoute.setType(obj.getString("type"));
                                    findedRoute.setCurrency(currency);
                                    findedRoute.setDate(dateInOldFormat);
                                    findedRoute.setIsSaved(obj.getBoolean("saved"));

                                    findedRoutes.add(findedRoute);

                                } catch (Exception exc) {
                                    Log.e("json_data", "no data");
                                    Log.e("json_data", exc.toString());
                                    break;
                                }
                            }

                            if (findedRoutes.size()!=0) {
                                //setup listview
                                recycler_view = (RecyclerView) findViewById(R.id.recycler_view);
                                recycler_view.setLayoutManager(new LinearLayoutManager(Results.this));
                                ResultsRecyclerViewAdapter rcAdapter = new ResultsRecyclerViewAdapter(findedRoutes, Results.this);
                                recycler_view.setAdapter(rcAdapter);
                            }else {
                                no_route_founded.setVisibility(View.VISIBLE);

                                //fixme toto nefunguje treba to opravit
                                Snackbar.make(toolbar, R.string.no_routes, Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                                //fixme dokonca ani toto nefunguje
                                Toast.makeText(Results.this,R.string.no_routes,Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException ex) {
                            ex.printStackTrace();
                            no_route_founded.setVisibility(View.VISIBLE);
                        } catch (RuntimeException ex){
                            ex.printStackTrace();
                            no_route_founded.setVisibility(View.VISIBLE);
                            Toast.makeText(Results.this, R.string.error_network, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = NavUtils.getParentActivityIntent(Results.this);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                NavUtils.navigateUpTo(Results.this, intent);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
