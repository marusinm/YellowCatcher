package sk.meetz.zlty_odchytavac.saved_routes;

import android.graphics.Typeface;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import sk.meetz.zlty_odchytavac.R;
import sk.meetz.zlty_odchytavac.ZltyOdchytavac;
import sk.meetz.zlty_odchytavac.results.FindedRoute;
import sk.meetz.zlty_odchytavac.results.Results;
import sk.meetz.zlty_odchytavac.results.ResultsRecyclerViewAdapter;

public class SavedRoutes extends AppCompatActivity {

    ArrayList<FindedRoute> findedRoutes = new ArrayList<>();
    ProgressBar progress;
    ProgressBar action_progress;
    TextView no_route_founded;

    RecyclerView recycler_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_routes);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icon_back_bl);

        TextView toolbar_title = (TextView)findViewById(R.id.toolbar_title);
        toolbar_title.setTypeface(Typeface.createFromAsset(getAssets(), "Blogger Sans-Bold.ttf"));

        progress = (ProgressBar)findViewById(R.id.progress);
        no_route_founded = (TextView)findViewById(R.id.no_routes_founded);
        no_route_founded.setVisibility(View.GONE);
        action_progress = (ProgressBar)findViewById(R.id.action_progress);
        action_progress.setVisibility(View.GONE);

        recycler_view = (RecyclerView)findViewById(R.id.recycler_view);
        recycler_view.setLayoutManager(new LinearLayoutManager(SavedRoutes.this));

        downloadData();

    }

    public void downloadData(){
        //http://46.101.209.48:5000/userRoutes/Wgx8cNOKdF
        findedRoutes.clear();

        no_route_founded.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);

        System.out.println(ZltyOdchytavac.SAVED_ROUTES_URL+"/"+ZltyOdchytavac.GCM_TOKEN);
        Ion.with(this)
                .load(ZltyOdchytavac.SAVED_ROUTES_URL+"/"+ZltyOdchytavac.GCM_TOKEN)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {


                        System.out.println("result fom saved: "+result);

                        no_route_founded.setVisibility(View.VISIBLE);
                        progress.setVisibility(View.GONE);

                        try {
                            if(result == null ){
                                throw new RuntimeException("Server error");
                            }

                            JSONArray arr = new JSONArray(result);
                            for (int i = 0; i < arr.length(); i++) {
                                JSONObject obj = arr.getJSONObject(i);

                                no_route_founded.setVisibility(View.GONE);

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
                                    findedRoute.setDate(obj.getString("date"));
                                    findedRoute.setCurrency(obj.getString("currency"));
                                    findedRoute.setRouteId(obj.getString("route_id"));
                                    findedRoute.setUserId(obj.getString("user_id"));

                                    System.out.println("routeID:" +obj.getString("route_id"));

                                    findedRoutes.add(findedRoute);

                                } catch (Exception exc) {
                                    Log.e("json_data", "no data");
                                    Log.e("json_data", exc.toString());
                                    break;
                                }
                            }

                            if (findedRoutes.size()!=0) {
                                //setup listview
//                                RecyclerView recycler_view = (RecyclerView)findViewById(R.id.recycler_view);
//                                recycler_view.setLayoutManager(new LinearLayoutManager(SavedRoutes.this));
                                SavedRoutesRecyclerViewAdapter rcAdapter = new SavedRoutesRecyclerViewAdapter(findedRoutes, SavedRoutes.this);
                                recycler_view.setAdapter(rcAdapter);
                            }else {
                                //fixme dokonca ani toto nefunguje
                                Toast.makeText(SavedRoutes.this,R.string.no_routes,Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException ex) {
                            ex.printStackTrace();
                            Toast.makeText(SavedRoutes.this, R.string.error_network, Toast.LENGTH_SHORT).show();
                        } catch (RuntimeException ex){
                            ex.printStackTrace();
                            no_route_founded.setVisibility(View.VISIBLE);
                            Toast.makeText(SavedRoutes.this, R.string.error_network, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
