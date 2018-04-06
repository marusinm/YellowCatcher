package sk.meetz.zlty_odchytavac;

import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import sk.meetz.zlty_odchytavac.saved_routes.SavedRoutes;

/**
 * Created by Marek on 26/08/16.
 */
public class MainActivityRecyclerViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener{

    public View this_itemView;
    public MainActivity activity;
    TextView stationFrom;
    TextView stationTo;
    TextView label_use;
    RelativeLayout cardView;

    public MainActivityRecyclerViewHolders(View itemView, MainActivity activity) {
        super(itemView);
        itemView.setOnClickListener(this);
        this_itemView = itemView;
        this.activity = activity;
        this.activity = activity;
        stationFrom = (TextView)itemView.findViewById(R.id.from);
        stationTo = (TextView)itemView.findViewById(R.id.to);
        cardView = (RelativeLayout) itemView.findViewById(R.id.card_view);

        label_use = (TextView)itemView.findViewById(R.id.lb_use);
        label_use.setTypeface(Typeface.createFromAsset(activity.getAssets(), "Blogger Sans-Bold.ttf"));
    }

    @Override
    public void onClick(View view) {
        activity.btn_start_point.setText(stationFrom.getText().toString());
        activity.btn_end_point.setText(stationTo.getText().toString());
        cardView.setBackgroundColor(ContextCompat.getColor(activity, R.color.filter_selected_item));
        this_itemView.setBackgroundColor(ContextCompat.getColor(activity, R.color.filter_selected_item));

        Animation animation = AnimationUtils.loadAnimation(activity.getApplicationContext(), R.anim.shake_anim);
        activity.btn_start_point.startAnimation(animation);
        activity.btn_end_point.startAnimation(animation);
    }
}