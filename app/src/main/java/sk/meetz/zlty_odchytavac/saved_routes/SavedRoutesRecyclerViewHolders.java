package sk.meetz.zlty_odchytavac.saved_routes;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import sk.meetz.zlty_odchytavac.R;

/**
 * Created by Marek on 02/08/16.
 */
public class SavedRoutesRecyclerViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener{

    public View this_itemView;
    public SavedRoutes activity;
    TextView stationFrom;
    TextView stationTo;
    TextView seats;
    TextView arrival;
    TextView departure;
    TextView price;
    TextView date;
    TextView type;
    LinearLayout priceLayout;

    Button btn_delete;
    Button btn_buy;

    View dashView;
    LinearLayout timeInfoLayout;
    ImageView busIcon;


    public SavedRoutesRecyclerViewHolders(View itemView, SavedRoutes activity) {
        super(itemView);
        itemView.setOnClickListener(this);
        this_itemView = itemView;
        this.activity = activity;
        this.activity = activity;
        stationFrom = (TextView)itemView.findViewById(R.id.tv_from);
        stationTo = (TextView)itemView.findViewById(R.id.tv_to);
        seats = (TextView)itemView.findViewById(R.id.tickets_count);
        arrival = (TextView)itemView.findViewById(R.id.tv_arrival);
        departure = (TextView)itemView.findViewById(R.id.tv_departure);
        price = (TextView)itemView.findViewById(R.id.tv_price);
        date = (TextView)itemView.findViewById(R.id.tv_date);

        dashView = (View)itemView.findViewById(R.id.view_dash);
        timeInfoLayout = (LinearLayout)itemView.findViewById(R.id.time_info_layout);
        busIcon = (ImageView)itemView.findViewById(R.id.ic_bus);

        priceLayout = (LinearLayout)itemView.findViewById(R.id.price_laytou);

        btn_delete = (Button)itemView.findViewById(R.id.btn_delete);
        btn_delete.setTypeface(Typeface.createFromAsset(activity.getAssets(), "Blogger Sans-Bold.ttf"));
        btn_buy = (Button)itemView.findViewById(R.id.btn_buy);
        btn_buy.setTypeface(Typeface.createFromAsset(activity.getAssets(), "Blogger Sans-Bold.ttf"));

        //create fonts variables
//        Typeface hind_bold = Typeface.createFromAsset(catalog.getAssets(), "Hind-Bold.ttf");
//        Typeface hind_regular = Typeface.createFromAsset(catalog.getAssets(), "Hind-Regular.ttf");
//        productName.setTypeface(hind_bold);
//        categoryName.setTypeface(hind_regular);
//        price.setTypeface(hind_regular);
    }

    @Override
    public void onClick(View view) {
    }
}
