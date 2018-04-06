package sk.meetz.zlty_odchytavac;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.Normalizer;

import io.realm.Realm;
import sk.meetz.zlty_odchytavac.realm.Station;
import sk.meetz.zlty_odchytavac.saved_routes.SavedRoutes;

public class DownloadStations extends AppCompatActivity {

    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_stations);
        realm = Realm.getDefaultInstance();

        if (ZltyOdchytavac.isNetworkAvailable(this)){
            //if network is available, download stations from server
            Ion.with(this)
                    .load(ZltyOdchytavac.STATIONS_URL)
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String result) {
                            parseJsonArrayAndSaveToRealm(result);
                        }
                    });
        }else{
            //if network is not available, take stations from folder res/raw/default_stations_json_file
            InputStream is = getResources().openRawResource(R.raw.default_stations_json_file);
            Writer writer = new StringWriter();
            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            String jsonString = writer.toString();
            parseJsonArrayAndSaveToRealm(jsonString);
        }
    }

    /**
     * Parsuje json string
     * @param json json string
     */
    private void parseJsonArrayAndSaveToRealm(String json){
        try {

            JSONArray arr = new JSONArray(json);
            realm.beginTransaction();
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);

                try {

                    Station station = new Station();
                    station.setId(obj.getLong("id"));
                    station.setTitle(obj.getString("title"));
                    station.setCountryCode(obj.getString("countryCode"));
                    station.setLongitude(obj.getString("longitude"));
                    station.setLatitude(obj.getString("latitude"));
                    station.setPriority(obj.getString("priority"));

                    String title_without_diacritics = Normalizer.normalize(obj.getString("title"), Normalizer.Form.NFD)
                            .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
                    station.setTitleAscii(title_without_diacritics);

                    realm.copyToRealmOrUpdate(station);

                } catch (Exception exc) {
                    Log.e("json_data", "no data");
                    Log.e("json_data", exc.toString());
                    break;
                }
            }

            realm.commitTransaction();


            DownloadStations.this.finish();
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close the Realm instance.
        realm.close();
    }
}
