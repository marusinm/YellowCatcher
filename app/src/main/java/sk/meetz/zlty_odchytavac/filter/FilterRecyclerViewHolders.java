package sk.meetz.zlty_odchytavac.filter;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import sk.meetz.zlty_odchytavac.R;

/**
 * Created by Marek on 20/07/16.
 */
public class FilterRecyclerViewHolders  extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView title;
    public View this_itemView;
    public Filter activity;

    public FilterRecyclerViewHolders(View itemView, Filter activity) {
        super(itemView);
        itemView.setOnClickListener(this);
        this_itemView = itemView;
        this.activity = activity;

        title = (TextView)itemView.findViewById(R.id.title);

        //create fonts variables
//        Typeface hind_bold = Typeface.createFromAsset(catalog.getAssets(), "Hind-Bold.ttf");
//        Typeface hind_regular = Typeface.createFromAsset(catalog.getAssets(), "Hind-Regular.ttf");
//        productName.setTypeface(hind_bold);
//        categoryName.setTypeface(hind_regular);
//        price.setTypeface(hind_regular);
    }

    @Override
    public void onClick(View view) {
        this_itemView.setBackgroundColor(ContextCompat.getColor(activity, R.color.filter_selected_item));

        Intent returnIntent = new Intent();
        returnIntent.putExtra("title",title.getText().toString());
        activity.setResult(Activity.RESULT_OK,returnIntent);
        activity.finish();
    }
}

