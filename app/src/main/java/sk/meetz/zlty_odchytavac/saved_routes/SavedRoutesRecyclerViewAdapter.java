package sk.meetz.zlty_odchytavac.saved_routes;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

import io.realm.Realm;
import sk.meetz.zlty_odchytavac.R;
import sk.meetz.zlty_odchytavac.ZltyOdchytavac;
import sk.meetz.zlty_odchytavac.realm.Station;
import sk.meetz.zlty_odchytavac.results.FindedRoute;

/**
 * Created by Marek on 02/08/16.
 */
public class SavedRoutesRecyclerViewAdapter extends RecyclerView.Adapter<SavedRoutesRecyclerViewHolders> {

    ArrayList<FindedRoute> findedRoutes;
    SavedRoutes saved_routes_activity;

    public SavedRoutesRecyclerViewAdapter(ArrayList<FindedRoute> findedRoutes, SavedRoutes saved_routes_activity) {
        this.findedRoutes = findedRoutes;
        this.saved_routes_activity = saved_routes_activity;
    }

    @Override
    public SavedRoutesRecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.saved_routes_card_list_view, null);
        SavedRoutesRecyclerViewHolders rcv = new SavedRoutesRecyclerViewHolders(layoutView, saved_routes_activity);
        return rcv;
    }

    @Override
    public void onBindViewHolder(SavedRoutesRecyclerViewHolders holder, final int position) {
        final Realm realm = Realm.getDefaultInstance();
        Station station = realm.where(Station.class).equalTo("id", findedRoutes.get(position).getStationFromId()).findFirst();
        holder.stationFrom.setText(station.getTitle());
        station = realm.where(Station.class).equalTo("id", findedRoutes.get(position).getStationToId()).findFirst();
        holder.stationTo.setText(station.getTitle());
        holder.arrival.setText(findedRoutes.get(position).getArrival());
        holder.arrival.setTypeface(Typeface.createFromAsset(saved_routes_activity.getAssets(), "Blogger Sans-Bold.ttf"));
        holder.departure.setText(findedRoutes.get(position).getDeparture());
        holder.departure.setTypeface(Typeface.createFromAsset(saved_routes_activity.getAssets(), "Blogger Sans-Bold.ttf"));
        holder.price.setText(findedRoutes.get(position).getPrice()+" "+findedRoutes.get(position).getCurrency());
        holder.date.setText(ZltyOdchytavac.formateDateFromstring("yyyy-MM-dd hh:mm:ss", "dd/MM/yyyy", findedRoutes.get(position).getDate()));

        if (findedRoutes.get(position).getSeats() > 0) {
            if (findedRoutes.get(position).getSeats() > 4 ) {
                holder.seats.setText(String.valueOf(findedRoutes.get(position).getSeats())+" "+saved_routes_activity.getResources().getString(R.string.empty_seats3));
            }else if (findedRoutes.get(position).getSeats() > 1 && findedRoutes.get(position).getSeats() < 5){
                holder.seats.setText(String.valueOf(findedRoutes.get(position).getSeats())+" "+saved_routes_activity.getResources().getString(R.string.empty_seats2));
            }else if (findedRoutes.get(position).getSeats() == 1){
                holder.seats.setText(String.valueOf(findedRoutes.get(position).getSeats())+" "+saved_routes_activity.getResources().getString(R.string.empty_seats1));
            }
            holder.arrival.setTextColor(ContextCompat.getColor(saved_routes_activity, R.color.use_label_text_color));
            holder.departure.setTextColor(ContextCompat.getColor(saved_routes_activity, R.color.use_label_text_color));
            holder.dashView.setBackgroundColor(ContextCompat.getColor(saved_routes_activity, R.color.use_label_text_color));
            holder.timeInfoLayout.setBackgroundColor(ContextCompat.getColor(saved_routes_activity, R.color.bus_icon_background));
            if (findedRoutes.get(position).getType().equals("Autobus") || findedRoutes.get(position).getType().equals("Coach")){
                holder.busIcon.setImageDrawable(ContextCompat.getDrawable(saved_routes_activity, R.drawable.icon_bus));
            }else if (findedRoutes.get(position).getType().equals("Vlak") || findedRoutes.get(position).getType().equals("Rail car")){
                holder.busIcon.setImageDrawable(ContextCompat.getDrawable(saved_routes_activity, R.drawable.icon_train));
            }else {
                holder.busIcon.setImageDrawable(ContextCompat.getDrawable(saved_routes_activity, R.drawable.icon_bus_train));
            }

            holder.priceLayout.setVisibility(View.VISIBLE);
        }else{
            holder.seats.setText(saved_routes_activity.getResources().getString(R.string.sold_out));
            holder.arrival.setTextColor(ContextCompat.getColor(saved_routes_activity, R.color.sold_out_text_color));
            holder.arrival.setTypeface(Typeface.createFromAsset(saved_routes_activity.getAssets(), "Blogger Sans-Bold.ttf"));
            holder.departure.setTextColor(ContextCompat.getColor(saved_routes_activity, R.color.sold_out_text_color));
            holder.departure.setTypeface(Typeface.createFromAsset(saved_routes_activity.getAssets(), "Blogger Sans-Bold.ttf"));
            holder.dashView.setBackgroundColor(ContextCompat.getColor(saved_routes_activity, R.color.sold_out_text_color));
            holder.timeInfoLayout.setBackgroundColor(ContextCompat.getColor(saved_routes_activity, R.color.sold_out_background));
            if (findedRoutes.get(position).getType().equals("Autobus") || findedRoutes.get(position).getType().equals("Coach")){
                holder.busIcon.setImageDrawable(ContextCompat.getDrawable(saved_routes_activity, R.drawable.icon_bus_red));
            }else if (findedRoutes.get(position).getType().equals("Vlak") || findedRoutes.get(position).getType().equals("Rail car")){
                holder.busIcon.setImageDrawable(ContextCompat.getDrawable(saved_routes_activity, R.drawable.icon_train_red));
            }else {
                holder.busIcon.setImageDrawable(ContextCompat.getDrawable(saved_routes_activity, R.drawable.icon_bus_train_red));
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
                        ZltyOdchytavac.formateDateFromstring("yyyy-MM-dd hh:mm:ss", "yyyyMMdd", findedRoutes.get(position).getDate()));

                System.out.println("url: "+url);
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                saved_routes_activity.startActivity(intent);
            }
        });

        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (findedRoutes.size()!=0) {
                    if (ZltyOdchytavac.isNetworkAvailable(saved_routes_activity)) {

                        saved_routes_activity.action_progress.setVisibility(View.VISIBLE);

                        Ion.with(saved_routes_activity)
                                .load(ZltyOdchytavac.DELETE_ROUTE_URL)
                                .setBodyParameter("user_id", findedRoutes.get(position).getUserId())
                                .setBodyParameter("route_id", findedRoutes.get(position).getRouteId())
                                .asString()
                                .setCallback(new FutureCallback<String>() {
                                    @Override
                                    public void onCompleted(Exception e, String result) {
                                        saved_routes_activity.action_progress.setVisibility(View.GONE);

                                        System.out.println("result: " + result);
                                        saved_routes_activity.downloadData();

                                    }
                                });
                    } else {
                        Toast.makeText(saved_routes_activity, R.string.error_network, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.findedRoutes.size();
    }

}
