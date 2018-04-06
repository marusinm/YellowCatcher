package sk.meetz.zlty_odchytavac;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.realm.Realm;
import io.realm.RealmResults;
import sk.meetz.zlty_odchytavac.realm.LastRoute;
import sk.meetz.zlty_odchytavac.realm.Station;

/**
 * Created by Marek on 26/08/16.
 */
public class MainActivityRecyclerViewAdapter extends RecyclerView.Adapter<MainActivityRecyclerViewHolders> {

    RealmResults<LastRoute> lastRoutes;
    MainActivity main_activity;

    public MainActivityRecyclerViewAdapter(RealmResults<LastRoute> lastRoutes, MainActivity main_activity) {
        this.lastRoutes = lastRoutes;
        this.main_activity = main_activity;
    }

    @Override
    public MainActivityRecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_card_list_view, null);
        MainActivityRecyclerViewHolders rcv = new MainActivityRecyclerViewHolders(layoutView, main_activity);
        return rcv;
    }

    @Override
    public void onBindViewHolder(MainActivityRecyclerViewHolders holder, final int position) {
        final Realm realm = Realm.getDefaultInstance();
        Station station = realm.where(Station.class).equalTo("id", lastRoutes.get(position).getStationFromId()).findFirst();
        holder.stationFrom.setText(station.getTitle());
        station = realm.where(Station.class).equalTo("id", lastRoutes.get(position).getStationToId()).findFirst();
        holder.stationTo.setText(station.getTitle());

    }


    @Override
    public int getItemCount() {
        return this.lastRoutes.size();
    }
}
