package sk.meetz.zlty_odchytavac.gcm;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

//src: https://www.simplifiedcoding.net/android-push-notification-using-gcm-tutorial/
public class GCMTokenRefreshListenerService extends InstanceIDListenerService {

    //If the token is changed registering the device again
    @Override
    public void onTokenRefresh() {
        Intent intent = new Intent(this, GCMRegistrationIntentService.class);
        startService(intent);
    }
}
