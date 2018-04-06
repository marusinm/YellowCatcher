package sk.meetz.zlty_odchytavac.filter;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.text.Normalizer;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import sk.meetz.zlty_odchytavac.R;
import sk.meetz.zlty_odchytavac.realm.Station;
import sk.meetz.zlty_odchytavac.realm.Tarif;
import sk.meetz.zlty_odchytavac.results.Results;

import static sk.meetz.zlty_odchytavac.R.string.tarif;

public class Filter extends AppCompatActivity {

    Toolbar toolbar;

    Realm realm;
    RecyclerView recycler_view;

    boolean tarif = false; // hovori o tom ci zobrazit tarify alebo stanice

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icon_close_wh);

        realm = Realm.getDefaultInstance();
        Bundle extras = getIntent().getExtras();
        if(extras !=null)
        {
            tarif = extras.getBoolean("tarif");
        }
        recycler_view = (RecyclerView)findViewById(R.id.recycler_view);
        recycler_view.setLayoutManager(new LinearLayoutManager(this));
        FilterRecyclerViewAdapter rcAdapter;
        if (!tarif) {
            RealmResults<Station> stations = realm.where(Station.class).findAll();
            stations = stations.sort("priority"); // zoradit podla priority
//            for (int i = 0; i < stations.size(); i++){
//                System.out.println("priority: "+stations.get(i).getPriority() );
//            }
            rcAdapter = new FilterRecyclerViewAdapter(stations, this);
        }else {
            RealmResults<Tarif> tarifs = realm.where(Tarif.class).findAll();
            rcAdapter = new FilterRecyclerViewAdapter(tarifs, this, true);
        }
        recycler_view.setAdapter(rcAdapter);
        final EditText ed_search = (EditText)findViewById(R.id.ed_search);
        if (tarif){
            ed_search.setVisibility(View.GONE);
        }
        ed_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {ed_search.setCursorVisible(true);}});
        //set listener for typing
        ed_search.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                filterProductsByQuery(ed_search.getText().toString());
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {/*do nothing*/}
            public void onTextChanged(CharSequence s, int start, int before, int count) { /*do nothing*/}
        });
    }


    /**
     * vyhlada podla zadaneho vyrazu nazov stanice alebo jej cast
     * @param title cast hladaneho vyrazu alebo cely vyraz
     */
    private void filterProductsByQuery(String title){

        //odstranime mekcene a podobne
        title = Normalizer.normalize(title, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        RealmResults<Station> stations;
        RealmQuery<Station> query = realm.where(Station.class);
        query.contains("titleAscii", title, Case.INSENSITIVE).findAll();
        stations = query.findAll();
//        stations.sort("priority");
        stations = stations.sort("titleAscii"); // zoradit abecedne
        FilterRecyclerViewAdapter rcAdapter = new FilterRecyclerViewAdapter(stations,this);
        recycler_view.setAdapter(rcAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = NavUtils.getParentActivityIntent(Filter.this);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                NavUtils.navigateUpTo(Filter.this, intent);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
