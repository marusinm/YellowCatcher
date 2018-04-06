package sk.meetz.zlty_odchytavac.gcm;


import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import sk.meetz.zlty_odchytavac.R;
import sk.meetz.zlty_odchytavac.ZltyOdchytavac;

//src: https://www.simplifiedcoding.net/android-push-notification-using-gcm-tutorial/
public class GCMRegistrationIntentService extends IntentService {
    //Constants for success and errors
    public static final String REGISTRATION_SUCCESS = "RegistrationSuccess";
    public static final String REGISTRATION_ERROR = "RegistrationError";
    public static final String SAVED_TOKEN = "saved_token";

    //Class constructor
    public GCMRegistrationIntentService() {
        super("");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        //Registering gcm to the device
        registerGCM();
    }

    private void registerGCM() {
        //Registration complete intent initially null
        Intent registrationComplete = null;

        //Register token is also null
        //we will get the token on successfull registration
        String token = null;
        try {
            //Creating an instanceid
            InstanceID instanceID = InstanceID.getInstance(getApplicationContext());

            //Getting the token from the instance id
            token = instanceID.getToken(getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            //Displaying the token in the log so that we can copy it to send push notification
            //You can also extend the app by storing the token in to your server
            Log.w("GCMRegIntentService", "token:" + token);

            //on registration complete creating intent with success
            registrationComplete = new Intent(REGISTRATION_SUCCESS);

            //Putting the token to the intent
            registrationComplete.putExtra("token", token);

            ZltyOdchytavac.GCM_TOKEN = token;

            //save token to sharedPreferences
            SharedPreferences settings = getSharedPreferences(SAVED_TOKEN, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("token", token);

            // Commit the edits!
            editor.commit();

        } catch (Exception e) {
            //If any error occurred
            Log.w("GCMRegIntentService", "Registration error");
            registrationComplete = new Intent(REGISTRATION_ERROR);
        }

        //Sending the broadcast that registration is completed
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }
}