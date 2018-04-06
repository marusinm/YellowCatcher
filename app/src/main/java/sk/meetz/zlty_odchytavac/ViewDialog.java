package sk.meetz.zlty_odchytavac;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.async.http.body.StringBody;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import io.realm.Realm;
import sk.meetz.zlty_odchytavac.R;
import sk.meetz.zlty_odchytavac.realm.Station;
import sk.meetz.zlty_odchytavac.results.FindedRoute;
import sk.meetz.zlty_odchytavac.results.ResultsRecyclerViewAdapter;

public class ViewDialog {

    private long stationFrom = 0;
    private String tarif = "";
    private String type="";
    private int seats=0;
    private long stationTo=0;
    private String currency = "";
    private String date="";
    private String arrival="";
    private String departure="";
    private String price="";
    
    public void showDialog(final Activity activity, String route_id, String user_token){
        System.out.println("from show dialog: "+route_id);
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.ticket_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        ((ViewGroup)dialog.getWindow().getDecorView()).getChildAt(0).startAnimation(AnimationUtils.loadAnimation(
                activity,R.anim.fadeut));

        dialog.show();

        final TextView tv_title = (TextView)dialog.findViewById(R.id.tv_title);
        tv_title.setTypeface(Typeface.createFromAsset(activity.getAssets(), "Blogger Sans-Bold.ttf"));
        TextView route_title = (TextView)dialog.findViewById(R.id.route_title);
        route_title.setTypeface(Typeface.createFromAsset(activity.getAssets(), "Blogger Sans-Bold.ttf"));
        TextView when_title = (TextView)dialog.findViewById(R.id.when_title);
        when_title.setTypeface(Typeface.createFromAsset(activity.getAssets(), "Blogger Sans-Bold.ttf"));
        final TextView empty_seats_title = (TextView)dialog.findViewById(R.id.empty_seats_title);
        empty_seats_title.setTypeface(Typeface.createFromAsset(activity.getAssets(), "Blogger Sans-Bold.ttf"));

        final TextView tv_from = (TextView) dialog.findViewById(R.id.tv_from);
        final TextView tv_to = (TextView) dialog.findViewById(R.id.tv_to);
        final TextView tv_time = (TextView) dialog.findViewById(R.id.tv_time);
        final TextView tv_date = (TextView) dialog.findViewById(R.id.tv_date);
        final TextView tv_empty_seats = (TextView) dialog.findViewById(R.id.tv_empty_seats);
        final LinearLayout tv_layout = (LinearLayout)dialog.findViewById(R.id.tv_layout);
        final ProgressBar progress = (ProgressBar)dialog.findViewById(R.id.progress);

        progress.setVisibility(View.VISIBLE);
//        tv_layout.setVisibility(View.GONE);

        final Button buyButton = (Button) dialog.findViewById(R.id.btn_buy_ticket);
        buyButton.setTypeface(Typeface.createFromAsset(activity.getAssets(), "Blogger Sans-Bold.ttf"));

        Button closeButton = (Button) dialog.findViewById(R.id.btn_close);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        String url = ZltyOdchytavac.ONE_ROUTE_URL+"/route_id/"+route_id+"/user_token/"+user_token;
//        String url = ZltyOdchytavac.ONE_ROUTE_URL+"/route_id/"+"10"+"/user_token/"+user_token;
        System.out.println("url: "+url);
        Ion.with(activity)
                .load(url)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {

                        System.out.println("my result "+ result);
                        progress.setVisibility(View.GONE);
//                        tv_layout.setVisibility(View.VISIBLE);

                        try {

                            JSONObject obj = new JSONObject(result);

                                try {

                                    stationFrom = obj.getLong("stationFrom");
                                    tarif = obj.getString("tarif");
                                    type=obj.getString("type");
                                    seats=obj.getInt("seats");
                                    stationTo=obj.getLong("stationTo");
                                    currency = obj.getString("currency");
                                    date=obj.getString("date");
                                    arrival=obj.getString("arrival");
                                    departure=obj.getString("departure");
                                    price=obj.getString("price");

                                    Realm realm = Realm.getDefaultInstance();
                                    Station station = realm.where(Station.class).equalTo("id", stationFrom).findFirst();
                                    tv_from.setText(station.getTitle());
                                    station = realm.where(Station.class).equalTo("id", stationTo).findFirst();
                                    tv_to.setText(station.getTitle());
                                    tv_time.setText(departure);
                                    tv_date.setText(ZltyOdchytavac.formateDateFromstring("yyyy-MM-dd hh:mm:ss","dd/MM/yyyy",date));

                                    if (seats == 0)
                                        tv_empty_seats.setText(String.valueOf(seats)+" "+activity.getResources().getString(R.string.seat3));
                                    if (seats == 1)
                                        tv_empty_seats.setText(String.valueOf(seats)+" "+activity.getResources().getString(R.string.seat1));
                                    if (seats > 1 && seats < 5)
                                        tv_empty_seats.setText(String.valueOf(seats)+" "+activity.getResources().getString(R.string.seat2));
                                    if (seats >= 5)
                                        tv_empty_seats.setText(String.valueOf(seats)+" "+activity.getResources().getString(R.string.seat3));

                                    if (seats == 0){
                                        buyButton.setBackgroundResource(R.drawable.buy_button_disabled);
                                        buyButton.setTextColor(ContextCompat.getColor(activity, R.color.buy_button_disabled_text));
                                        tv_title.setText(R.string.unavailable_ticket);
                                        empty_seats_title.setVisibility(View.GONE);
                                        tv_empty_seats.setVisibility(View.GONE);
                                    }else{
                                        buyButton.setBackgroundResource(R.drawable.buy_button);
                                        buyButton.setTextColor(ContextCompat.getColor(activity, R.color.black));
                                        tv_title.setText(R.string.ticket);
                                        empty_seats_title.setVisibility(View.VISIBLE);
                                        tv_empty_seats.setVisibility(View.VISIBLE);
                                    }

                                    if(seats == 0) {
                                        buyButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                //compose url
                                                Toast.makeText(activity, R.string.sold_out, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }else{
                                        buyButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                //compose url
                                                String url = ZltyOdchytavac.composeUrlBuyUrl(stationFrom,
                                                        stationTo,
                                                        tarif,
                                                        ZltyOdchytavac.formateDateFromstring("yyyy-MM-dd hh:mm:ss", "yyyyMMdd", date));

                                                System.out.println("url: " + url);
                                                Uri uri = Uri.parse(url);
                                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                                activity.startActivity(intent);
                                            }
                                        });
                                    }

                                } catch (Exception exc) {
                                    Log.e("json_data", "no data");
                                    Log.e("json_data", exc.toString());
                                }


                        } catch (JSONException ex) {
                            ex.printStackTrace();

                            tv_title.setText(R.string.unavailable_ticket);
                            tv_from.setText(R.string.non_exist_route);
                            tv_to.setText(R.string.non_exist_route);
                            tv_time.setText("");
                            tv_date.setText("");
                            tv_empty_seats.setText("");
                            empty_seats_title.setVisibility(View.GONE);
                            tv_empty_seats.setVisibility(View.GONE);
                            Toast.makeText(activity, R.string.non_exist_route, Toast.LENGTH_SHORT).show();

                            buyButton.setBackgroundResource(R.drawable.buy_button_disabled);
                            buyButton.setTextColor(ContextCompat.getColor(activity, R.color.buy_button_disabled_text));
                            buyButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Toast.makeText(activity, R.string.non_exist_route, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
        

    }
}
