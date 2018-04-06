package sk.meetz.zlty_odchytavac.filter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import io.realm.RealmResults;
import sk.meetz.zlty_odchytavac.R;
import sk.meetz.zlty_odchytavac.realm.Station;
import sk.meetz.zlty_odchytavac.realm.Tarif;

/**
 * Created by Marek on 20/07/16.
 */
public class FilterRecyclerViewAdapter extends RecyclerView.Adapter<FilterRecyclerViewHolders> {

    RealmResults<Station> stations;
    RealmResults<Tarif> tarifs;
    Filter filter_activity;
    boolean tarif = false;

    public FilterRecyclerViewAdapter(RealmResults<Station> stations, Filter filter_activity) {
        this.stations = stations;
        this.filter_activity = filter_activity;
        tarif = false;
    }

    public FilterRecyclerViewAdapter(RealmResults<Tarif> tarifs, Filter filter_activity, boolean tarif) {
        this.tarifs = tarifs;
        this.filter_activity = filter_activity;
        this.tarif = tarif;
    }


    @Override
    public FilterRecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.filter_card_list_view, null);
        FilterRecyclerViewHolders rcv = new FilterRecyclerViewHolders(layoutView, filter_activity);
        return rcv;
    }

    @Override
    public void onBindViewHolder(FilterRecyclerViewHolders holder, final int position) {
        if (!tarif) {
            holder.title.setText(stations.get(position).getTitle());
        }else{
            holder.title.setText(tarifs.get(position).getTitle());
        }
    }

    @Override
    public int getItemCount() {
        if (!tarif)
            return this.stations.size();
        else
            return this.tarifs.size();
    }
}
