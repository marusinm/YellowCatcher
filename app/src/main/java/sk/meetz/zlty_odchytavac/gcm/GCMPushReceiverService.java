package sk.meetz.zlty_odchytavac.gcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GcmListenerService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;

import sk.meetz.zlty_odchytavac.MainActivity;
import sk.meetz.zlty_odchytavac.R;


//src: https://www.simplifiedcoding.net/android-push-notification-using-gcm-tutorial/

//Class is extending GcmListenerService
public class GCMPushReceiverService extends GcmListenerService {

    //This method will be called on every new message received
    @Override
    public void onMessageReceived(String from, Bundle data) {
        //Getting the message from the bundle
        String message = data.getString("message");
        String id = data.getString("route_id","0");
        System.out.println("from: "+from+" id: "+id);

        sendNotification(message, id);
    }

    //This method is generating a notification and displaying the notification
    private void sendNotification(String message, String route_id) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("route_id",route_id);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        int requestCode = 0;
        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestCode, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder noBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.noti)
                .setContentTitle(getApplicationContext().getString(R.string.app_name))
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(sound)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, noBuilder.build()); //0 = ID of notification
    }
}

