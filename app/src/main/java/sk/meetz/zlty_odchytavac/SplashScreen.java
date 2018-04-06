package sk.meetz.zlty_odchytavac;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

import java.util.Locale;

import pl.droidsonroids.gif.GifImageView;

public class SplashScreen extends AppCompatActivity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        GifImageView gif = (GifImageView)findViewById(R.id.splash);

        Locale.getDefault().getLanguage();
        String language = Locale.getDefault().getDisplayLanguage();
        System.out.println("langu: "+language);
        if (language.equals("English")){
            gif.setBackgroundResource(R.drawable.splash_en);
        }else if(language.equals("Slovak")){
            gif.setBackgroundResource(R.drawable.splash_sk);
        }else if(language.equals("Czech")){
            gif.setBackgroundResource(R.drawable.splash_cz);
        }else{
            gif.setBackgroundResource(R.drawable.splash);
        }

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent i = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(i);

                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
