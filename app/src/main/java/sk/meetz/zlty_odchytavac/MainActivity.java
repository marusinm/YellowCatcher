package sk.meetz.zlty_odchytavac;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import sk.meetz.zlty_odchytavac.filter.Filter;
import sk.meetz.zlty_odchytavac.gcm.GCMPushReceiverService;
import sk.meetz.zlty_odchytavac.gcm.GCMRegistrationIntentService;
import sk.meetz.zlty_odchytavac.realm.LastRoute;
import sk.meetz.zlty_odchytavac.realm.Station;
import sk.meetz.zlty_odchytavac.realm.Tarif;
import sk.meetz.zlty_odchytavac.results.Results;
import sk.meetz.zlty_odchytavac.saved_routes.SavedRoutes;

/**
 * API doc http://46.101.209.48:5000/documentation
 */
public class MainActivity extends AppCompatActivity {

    Realm realm;

    final int FILTER_REQUEST_CODE = 1;

    private Calendar calendar;
    private int year, month, day;
    private Button pick_date;

    Button btn_start_point;
    Button btn_end_point;
    Button btn_tarif;
    ImageButton btn_exchange_stations;
    enum LAST_FILTER_STARTER {BTN_END_POINT, BTN_START_POINT, BTN_TARIF}
    LAST_FILTER_STARTER last_filter_starter = null;

    enum LAST_SELECTED_CURRENCY {BTN_CZK, BTN_EUR}
    LAST_SELECTED_CURRENCY last_selected_currency = LAST_SELECTED_CURRENCY.BTN_EUR;

    RecyclerView recycler_view;

    public static final String SAVED_TOKEN = "saved_token";


    //Creating a broadcast receiver for gcm registration
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Intent myIntent = new Intent(MainActivity.this, SplashScreen.class);
//        MainActivity.this.startActivity(myIntent);

        //code for gcm - notificatons
        //Initializing our broadcast receiver
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            //When the broadcast received
            //We are sending the broadcast from GCMRegistrationIntentService
            @Override
            public void onReceive(Context context, Intent intent) {
                //If the broadcast has received with success
                //that means device is registered successfully
                if(intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_SUCCESS)){
                    //Getting the registration token from the intent
                    ZltyOdchytavac.GCM_TOKEN = intent.getStringExtra("token");

                    //save token to sharedPreferences
                    SharedPreferences settings = getSharedPreferences(SAVED_TOKEN, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("token", intent.getStringExtra("token"));

                    // Commit the edits!
                    editor.commit();

                    //if the intent is not with success then displaying error messages
                } else if(intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_ERROR)){
//                    Toast.makeText(getApplicationContext(), "GCM registration error!", Toast.LENGTH_LONG).show();
                    Log.e("gcm","GCM registration error!");
                } else {
                    Log.e("gcm","Error occurred");
                }
            }
        };

        //Checking play service is available or not
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());

        //if play service is not available
        if(ConnectionResult.SUCCESS != resultCode) {
            //If play service is supported but not installed
            if(GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                //Displaying message that play service is not installed
                Toast.makeText(getApplicationContext(), "Google Play Service is not install/enabled in this device!", Toast.LENGTH_LONG).show();
                GooglePlayServicesUtil.showErrorNotification(resultCode, getApplicationContext());

                //If play service is not supported
                //Displaying an error message
            } else {
                Toast.makeText(getApplicationContext(), "This device does not support for Google Play Service!", Toast.LENGTH_LONG).show();
            }

            //If play service is available
        } else {
            //Starting intent to register device
            Intent itent = new Intent(new Intent(getApplicationContext(), GCMRegistrationIntentService.class));
            startService(itent);
        }



        realm = Realm.getDefaultInstance();
        //check if some stations was already downloaded
        RealmResults<Station> stations = realm.where(Station.class).findAll();
        if (stations.size() == 0){
            Intent intent = new Intent(MainActivity.this, DownloadStations.class);
            MainActivity.this.startActivity(intent);
        }

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        TextView toolbar_title = (TextView)findViewById(R.id.toolbar_title);
        toolbar_title.setTypeface(Typeface.createFromAsset(getAssets(), "Blogger Sans-Bold.ttf"));
        TextView lb_last_finding_routes = (TextView)findViewById(R.id.lb_last_finding_routes);
        lb_last_finding_routes.setTypeface(Typeface.createFromAsset(getAssets(), "Blogger Sans-Bold.ttf"));

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        pick_date = (Button)findViewById(R.id.btn_date);
        showDate(year, month+1, day);
        assert pick_date != null;
        pick_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(999);
            }
        });

        //list of last finding routes
        RealmResults<LastRoute> lastRoutes = realm.where(LastRoute.class).findAll();
        recycler_view = (RecyclerView) findViewById(R.id.recycler_view);
        recycler_view.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        if (lastRoutes.size() != 0) {
            lastRoutes = lastRoutes.sort("timestamp", Sort.DESCENDING);
            MainActivityRecyclerViewAdapter rcAdapter = new MainActivityRecyclerViewAdapter(lastRoutes, MainActivity.this);
            recycler_view.setAdapter(rcAdapter);
        }

        FloatingActionButton fab_search = (FloatingActionButton)findViewById(R.id.fab_search);
        fab_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ZltyOdchytavac.isNetworkAvailable(MainActivity.this)) {
                    boolean isStationSelected = true;
                    Intent intent = new Intent(MainActivity.this, Results.class);

                    long fromStationId = 0;

                    Station station = realm.where(Station.class).equalTo("title", btn_start_point.getText().toString()).findFirst();
                    if (station!=null){
                        intent.putExtra("from_id", station.getId());
                        fromStationId = station.getId();
                    } else {
                        isStationSelected = false;
                    }


                    long toStationId = 0;

                    station = realm.where(Station.class).equalTo("title",btn_end_point.getText().toString()).findFirst();
                    if (station!=null) {
                        intent.putExtra("to_id", station.getId());
                        toStationId = station.getId();
                    } else {
                        isStationSelected = false;
                    }



                    Tarif tarif = realm.where(Tarif.class).equalTo("title", btn_tarif.getText().toString()).findFirst();
                    intent.putExtra("tarif", tarif.getKey());

                    //save to db for next launch app
                    SharedPreferences sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("tarif", btn_tarif.getText().toString());
                    editor.commit();


                    intent.putExtra("date", pick_date.getText().toString());

                    if (last_selected_currency == LAST_SELECTED_CURRENCY.BTN_EUR)
                        intent.putExtra("currency", "EUR");
                    else
                        intent.putExtra("currency", "CZK");

                    if( isStationSelected ) {
                        //save last finding routes to realm with timestamp
                        boolean isThisRouteExists = false;
                        RealmResults<LastRoute> lastRoutes = realm.where(LastRoute.class).findAll();
                        realm.beginTransaction();
                        if (lastRoutes.size() > 0) {
                            //upravime timestamp
                            for (LastRoute lastRoute : lastRoutes) {
                                if (lastRoute.getStationFromId() == fromStationId) {
                                    if (lastRoute.getStationToId() == toStationId) {

                                        long seconds = System.currentTimeMillis() / 1000;
                                        lastRoute.setTimestamp(seconds);
                                        realm.copyToRealmOrUpdate(lastRoute);
                                        isThisRouteExists = true;
                                        break;
                                    }
                                } else {
                                    isThisRouteExists = false;
                                }
                            }
                        }
                        if (!isThisRouteExists || lastRoutes.size() == 0) {
                            LastRoute lastRoute = new LastRoute();
                            lastRoute.setStationFromId(fromStationId);
                            lastRoute.setStationToId(toStationId);

                            int id;
                            try {
                                id = realm.where(LastRoute.class).max("id").intValue() + 1; //nieco ako autoinkrement
                                lastRoute.setId(id);
                            } catch (Exception ex) {
                                lastRoute.setId(0);
                            }


                            long seconds = System.currentTimeMillis() / 1000;
                            lastRoute.setTimestamp(seconds);
                            realm.copyToRealmOrUpdate(lastRoute);
                        }
                        realm.commitTransaction();

                        //...and refresh last finding routes list
                        recycler_view = (RecyclerView) findViewById(R.id.recycler_view);
                        recycler_view.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                        if (lastRoutes.size() != 0) {
                            lastRoutes = lastRoutes.sort("timestamp", Sort.DESCENDING);
                            MainActivityRecyclerViewAdapter rcAdapter = new MainActivityRecyclerViewAdapter(lastRoutes, MainActivity.this);
                            recycler_view.setAdapter(rcAdapter);
                        }
                    }



                    if (isStationSelected)
                        startActivity(intent);
                    else
                        Toast.makeText(MainActivity.this, R.string.no_route_selected, Toast.LENGTH_SHORT).show();

                }else {
                    Toast.makeText(MainActivity.this, R.string.error_network,Toast.LENGTH_LONG).show();
                }
//                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//                overridePendingTransition(R.anim.slide_up, R.anim.no_change);

            }
        });

        btn_start_point = (Button)findViewById(R.id.btn_start_point);
        assert btn_start_point != null;
        btn_start_point.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, Filter.class);
                MainActivity.this.startActivityForResult(myIntent, FILTER_REQUEST_CODE);
                last_filter_starter = LAST_FILTER_STARTER.BTN_START_POINT;
            }
        });
        btn_end_point = (Button)findViewById(R.id.btn_end_point);
        assert btn_end_point != null;
        btn_end_point.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, Filter.class);
                MainActivity.this.startActivityForResult(myIntent, FILTER_REQUEST_CODE);
                last_filter_starter = LAST_FILTER_STARTER.BTN_END_POINT;
            }
        });


        //get default tarif value for user
        SharedPreferences sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
        String defaultTarifValue = sharedPref.getString("tarif", realm.where(Tarif.class).findFirst().getTitle());

        btn_tarif = (Button)findViewById(R.id.btn_tarif);
        assert btn_tarif != null;
        if (defaultTarifValue == null) {
            Tarif tarif = realm.where(Tarif.class).equalTo("key", "REGULAR").findFirst();
            btn_tarif.setText(tarif.getTitle());
        }else{
            btn_tarif.setText(defaultTarifValue);
        }
        btn_tarif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, Filter.class);
                myIntent.putExtra("tarif", true); //Optional parameters
                MainActivity.this.startActivityForResult(myIntent, FILTER_REQUEST_CODE);
                last_filter_starter = LAST_FILTER_STARTER.BTN_TARIF;
            }
        });

        final Button btn_eur = (Button)findViewById(R.id.btn_eur);
        final Button btn_czk = (Button)findViewById(R.id.btn_czk);

        assert btn_eur != null;
        btn_eur.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btn_eur.setBackgroundResource(R.drawable.main_currency_button_selected_left);
                    btn_czk.setBackgroundResource(R.drawable.main_currency_button_unselected_right);
                    last_selected_currency = LAST_SELECTED_CURRENCY.BTN_EUR;
                }
        });

        assert btn_czk != null;
        btn_czk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_eur.setBackgroundResource(R.drawable.main_currency_button_unselected_left);
                btn_czk.setBackgroundResource(R.drawable.main_currency_button_selected_right);
                last_selected_currency = LAST_SELECTED_CURRENCY.BTN_CZK;
            }
        });


        btn_exchange_stations = (ImageButton)findViewById(R.id.btn_exchage_stations);
        btn_exchange_stations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(realm.where(Station.class).equalTo("title",btn_start_point.getText().toString()).findFirst() != null && realm.where(Station.class).equalTo("title",btn_end_point.getText().toString()).findFirst() != null) {
                    //just swipe two variables
                    String helper_var = btn_start_point.getText().toString();
                    btn_start_point.setText(btn_end_point.getText().toString());
                    btn_end_point.setText(helper_var);

                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_anim);
                    btn_start_point.startAnimation(animation);
                    btn_end_point.startAnimation(animation);
                }else{
                    Toast.makeText(MainActivity.this, getString(R.string.lb_please_select_stations),Toast.LENGTH_SHORT).show();
                }
            }
        });


        ImageButton btn_credits = (ImageButton)findViewById(R.id.btn_credits);
        btn_credits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, CreditsActivity.class);
                MainActivity.this.startActivity(myIntent);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == FILTER_REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK){
                String title=data.getStringExtra("title");
                if (last_filter_starter == LAST_FILTER_STARTER.BTN_START_POINT){
                    btn_start_point.setText(title);
                }else if (last_filter_starter == LAST_FILTER_STARTER.BTN_END_POINT){
                    btn_end_point.setText(title);
                }else if (last_filter_starter == LAST_FILTER_STARTER.BTN_TARIF){
                    btn_tarif.setText(title);
                }
            }
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this, myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
            // arg1 = year
            // arg2 = month
            // arg3 = day
            showDate(arg1, arg2+1, arg3);
        }
    };

    private void showDate(int year, int month, int day) {
        pick_date.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_photovisualiser_done, menu);
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = NavUtils.getParentActivityIntent(MainActivity.this);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                NavUtils.navigateUpTo(MainActivity.this, intent);
                return true;

            case R.id.action_saved_routes:
                if (ZltyOdchytavac.isNetworkAvailable(this)) {
                    Intent myIntent = new Intent(MainActivity.this, SavedRoutes.class);
                    MainActivity.this.startActivity(myIntent);
                }else {
                    Toast.makeText(this, R.string.error_network,Toast.LENGTH_LONG).show();
                }
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

        //Registering receiver on activity resume
    @Override
    protected void onResume() {
        super.onResume();
        Log.w("MainActivity", "onResume");
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_SUCCESS));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_ERROR));

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if(extras.containsKey("route_id")) {
                String route_id = extras.getString("route_id");
                getIntent().removeExtra("route_id");

                // Restore preferences
                SharedPreferences settings = getSharedPreferences(SAVED_TOKEN, 0);
                String token = settings.getString("token","");

                ViewDialog alert = new ViewDialog();
//                alert.showDialog(this, route_id, ZltyOdchytavac.GCM_TOKEN);
                alert.showDialog(this, route_id, token);
            }
        }
    }


    //Unregistering receiver on activity paused
    @Override
    protected void onPause() {
        super.onPause();
        Log.w("MainActivity", "onPause");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }

    //testing notificaiton generation
//    //This method is generating a notification and displaying the notification
//    private void sendNotification(String message, String route_id) {
//        Intent intent = new Intent(this, MainActivity.class);
//        intent.putExtra("route_id",route_id);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//        int requestCode = 0;
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestCode, intent, PendingIntent.FLAG_ONE_SHOT);
//        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        NotificationCompat.Builder noBuilder = new NotificationCompat.Builder(this)
//                .setSmallIcon(R.mipmap.noti)
//                .setContentTitle("Žltý odchytávač")
//                .setContentText("Uvolnilo sa jedno miesto. Poponáhlaj sa! \n Bratislava -> Liptovský Mikuláš (25/11/2016)")
//                .setAutoCancel(true)
//                .setSound(sound)
//                .setContentIntent(pendingIntent);
//
//        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.notify(0, noBuilder.build()); //0 = ID of notification
//    }
}
