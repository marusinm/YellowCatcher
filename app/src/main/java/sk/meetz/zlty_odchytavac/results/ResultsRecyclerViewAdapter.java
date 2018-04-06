package sk.meetz.zlty_odchytavac.results;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import io.realm.Realm;
import sk.meetz.zlty_odchytavac.R;
import sk.meetz.zlty_odchytavac.ZltyOdchytavac;
import sk.meetz.zlty_odchytavac.realm.Station;

/**
 * Created by Marek on 20/07/16.
 */
public class ResultsRecyclerViewAdapter extends RecyclerView.Adapter<ResultsRecyclerViewHolders> {

    ArrayList<FindedRoute> findedRoutes;
    Results results_activity;

    public ResultsRecyclerViewAdapter(ArrayList<FindedRoute> findedRoutes, Results results_activity) {
        this.findedRoutes = findedRoutes;
        this.results_activity = results_activity;
    }

    @Override
    public ResultsRecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.results_card_list_view, null);
        ResultsRecyclerViewHolders rcv = new ResultsRecyclerViewHolders(layoutView, results_activity);
        return rcv;
    }

    @Override
    public void onBindViewHolder(final ResultsRecyclerViewHolders holder, final int position) {
        final Realm realm = Realm.getDefaultInstance();
        Station station = realm.where(Station.class).equalTo("id", findedRoutes.get(position).getStationFromId()).findFirst();
        holder.stationFrom.setText(station.getTitle());
        station = realm.where(Station.class).equalTo("id", findedRoutes.get(position).getStationToId()).findFirst();
        holder.stationTo.setText(station.getTitle());
        holder.arrival.setText(findedRoutes.get(position).getArrival());
        holder.arrival.setTypeface(Typeface.createFromAsset(results_activity.getAssets(), "Blogger Sans-Bold.ttf"));
        holder.departure.setText(findedRoutes.get(position).getDeparture());
        holder.departure.setTypeface(Typeface.createFromAsset(results_activity.getAssets(), "Blogger Sans-Bold.ttf"));
        holder.price.setText(findedRoutes.get(position).getPrice()+" "+findedRoutes.get(position).getCurrency());

        if (findedRoutes.get(position).getSeats() > 0) {
            if (findedRoutes.get(position).getSeats() > 4 ) {
                holder.seats.setText(String.valueOf(findedRoutes.get(position).getSeats())+" "+results_activity.getResources().getString(R.string.empty_seats3));
            }else if (findedRoutes.get(position).getSeats() > 1 && findedRoutes.get(position).getSeats() < 5){
                holder.seats.setText(String.valueOf(findedRoutes.get(position).getSeats())+" "+results_activity.getResources().getString(R.string.empty_seats2));
            }else if (findedRoutes.get(position).getSeats() == 1){
                holder.seats.setText(String.valueOf(findedRoutes.get(position).getSeats())+" "+results_activity.getResources().getString(R.string.empty_seats1));
            }
            holder.arrival.setTextColor(ContextCompat.getColor(results_activity, R.color.use_label_text_color));
            holder.arrival.setTypeface(Typeface.createFromAsset(results_activity.getAssets(), "Blogger Sans-Bold.ttf"));
            holder.departure.setTextColor(ContextCompat.getColor(results_activity, R.color.use_label_text_color));
            holder.departure.setTypeface(Typeface.createFromAsset(results_activity.getAssets(), "Blogger Sans-Bold.ttf"));
            holder.dashView.setBackgroundColor(ContextCompat.getColor(results_activity, R.color.use_label_text_color));
            holder.timeInfoLayout.setBackgroundColor(ContextCompat.getColor(results_activity, R.color.bus_icon_background));
            if (findedRoutes.get(position).getType().equals("Autobus") || findedRoutes.get(position).getType().equals("Coach") ){
                holder.busIcon.setImageDrawable(ContextCompat.getDrawable(results_activity, R.drawable.icon_bus));
            }else if (findedRoutes.get(position).getType().equals("Vlak") || findedRoutes.get(position).getType().equals("Rail car")){
                holder.busIcon.setImageDrawable(ContextCompat.getDrawable(results_activity, R.drawable.icon_train));
            }else {
                holder.busIcon.setImageDrawable(ContextCompat.getDrawable(results_activity, R.drawable.icon_bus_train));
            }

            holder.priceLayout.setVisibility(View.VISIBLE);
        }else{
            holder.seats.setText(results_activity.getResources().getString(R.string.sold_out));
            holder.arrival.setTextColor(ContextCompat.getColor(results_activity, R.color.sold_out_text_color));
            holder.departure.setTextColor(ContextCompat.getColor(results_activity, R.color.sold_out_text_color));
            holder.dashView.setBackgroundColor(ContextCompat.getColor(results_activity, R.color.sold_out_text_color));
            holder.timeInfoLayout.setBackgroundColor(ContextCompat.getColor(results_activity, R.color.sold_out_background));

            if (findedRoutes.get(position).getType().equals("Autobus") || findedRoutes.get(position).getType().equals("Coach")){
                holder.busIcon.setImageDrawable(ContextCompat.getDrawable(results_activity, R.drawable.icon_bus_red));
            }else if (findedRoutes.get(position).getType().equals("Vlak") || findedRoutes.get(position).getType().equals("Rail car")){
                holder.busIcon.setImageDrawable(ContextCompat.getDrawable(results_activity, R.drawable.icon_train_red));
            }else {
                holder.busIcon.setImageDrawable(ContextCompat.getDrawable(results_activity, R.drawable.icon_bus_train_red));
            }

            holder.priceLayout.setVisibility(View.GONE);
        }

        holder.btn_buy.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   //compose url
                   String url = ZltyOdchytavac.composeUrlBuyUrl(findedRoutes.get(position).getStationFromId(),
                   findedRoutes.get(position).getStationToId(),
                           findedRoutes.get(position).getTarif(),
                           ZltyOdchytavac.formateDateFromstring("dd/mm/yyyy", "yyyymmdd", findedRoutes.get(position).getDate()));

                   System.out.println("url: "+url);
                   Uri uri = Uri.parse(url);
                   Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                   results_activity.startActivity(intent);
               }
           });


        if (findedRoutes.get(position).getIsSaved() != false){
            holder.btn_save.setText(R.string.already_saved_lb);
            holder.btn_save.setCompoundDrawablesWithIntrinsicBounds( R.drawable.icon_saved_routes_green, 0, 0, 0);
        }
        holder.btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String language_parameter ="";
                Locale.getDefault().getLanguage();
                String language = Locale.getDefault().getDisplayLanguage();
                System.out.println("langu: "+language);
                if (language.equals("English")){
                    language_parameter = "en";
                }else if(language.equals("Slovak")){
                    language_parameter= "cz";
                }else if(language.equals("Czech")){
                    language_parameter = "sk";
                }else{
                    language_parameter = "sk";
                }


                /**
                 * treba dotestovat tento json objekt
                 * a ak to nepojde este odtestovat namiesto setBodyParameter metodu setBodyParameters s hasmapom
                 */
                if (ZltyOdchytavac.isNetworkAvailable(results_activity)){

                    //tu menime format koli tomu ze mesiac je niekedy len na jedno miesto 25/8/2016 -> 25/08/2016
                    String formated_date = ZltyOdchytavac.formateDateFromstring("dd/mm/yyyy", "dd/mm/yyyy", findedRoutes.get(position).getDate()+"-"+findedRoutes.get(position).getArrival());
                    System.out.println("formated date : "+formated_date);

                    results_activity.action_progress.setVisibility(View.VISIBLE);

                    Ion.with(results_activity)
                            .load(ZltyOdchytavac.ADD_NEW_ROAD_URL)
                            .setBodyParameter("currency", findedRoutes.get(position).getCurrency())
                            .setBodyParameter("fromStation", String.valueOf(findedRoutes.get(position).getStationFromId()))
                            .setBodyParameter("dateTime", findedRoutes.get(position).getDate()+"-"+findedRoutes.get(position).getDeparture())
                            .setBodyParameter("toStation", String.valueOf(findedRoutes.get(position).getStationToId()))
                            .setBodyParameter("userToken", ZltyOdchytavac.GCM_TOKEN)
                            .setBodyParameter("tarif", findedRoutes.get(position).getTarif())
                            .setBodyParameter("freeSeats", String.valueOf(findedRoutes.get(position).getSeats()))
                            .setBodyParameter("lang", language_parameter)
                            .asString()
                            .setCallback(new FutureCallback<String>() {
                                @Override
                                public void onCompleted(Exception e, String result) {

                                    if (!holder.btn_save.getText().toString().equals(results_activity.getResources().getString(R.string.already_saved_lb))){
                                        holder.btn_save.setText(R.string.already_saved_lb);
                                        holder.btn_save.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_saved_routes_green, 0, 0, 0);
                                    }else {
                                        holder.btn_save.setText(R.string.save);
                                        holder.btn_save.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_saved_routes_wh_outline, 0, 0, 0);
                                    }


                                    results_activity.action_progress.setVisibility(View.GONE);

                                    System.out.println("result: "+result);
//
//                                    if (result.equals("Route existed"))
//                                        Toast.makeText(results_activity,R.string.already_saved,Toast.LENGTH_SHORT).show();
//                                    else
//                                        Toast.makeText(results_activity,R.string.saved,Toast.LENGTH_SHORT).show();

                                }
                            });
                }else {
                    Toast.makeText(results_activity,R.string.error_network,Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return this.findedRoutes.size();
    }

}
