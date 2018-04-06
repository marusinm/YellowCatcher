package sk.meetz.zlty_odchytavac;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import sk.meetz.zlty_odchytavac.results.Results;

public class CreditsActivity extends AppCompatActivity {
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icon_back_bl);

        TextView toolbar_title = (TextView)findViewById(R.id.toolbar_title);
        toolbar_title.setTypeface(Typeface.createFromAsset(getAssets(), "Blogger Sans-Bold.ttf"));

        TextView tv_how_to_use_it = (TextView)findViewById(R.id.tv_how_to_use);
        tv_how_to_use_it.setTypeface(Typeface.createFromAsset(getAssets(), "Blogger Sans-Bold.ttf"));

        TextView tv_creators = (TextView)findViewById(R.id.tv_creators);
        tv_creators.setTypeface(Typeface.createFromAsset(getAssets(), "Blogger Sans-Bold.ttf"));

//        int versionCode = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;

        TextView tv_version = (TextView)findViewById(R.id.tv_version_number);
        tv_version.setText(versionName);

        Button btn_contact_us = (Button)findViewById(R.id.btn_contact_us);
        btn_contact_us.setTypeface(Typeface.createFromAsset(getAssets(), "Blogger Sans-Bold.ttf"));
        btn_contact_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("plain/text");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[] { "support@odchytavac.com" });
//                intent.putExtra(Intent.EXTRA_SUBJECT, "subject");
//                intent.putExtra(Intent.EXTRA_TEXT, "mail body");
                startActivity(Intent.createChooser(intent, ""));
            }
        });
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
                Intent intent = NavUtils.getParentActivityIntent(CreditsActivity.this);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                NavUtils.navigateUpTo(CreditsActivity.this, intent);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
