package cl.cooperativa.presidenciales2018;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by innova6 on 21-08-2017.
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    private static final String TAG = "MessagingService";
    private static final String KEY_EXTRA = "extra_candidato";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d(TAG,"From: "+remoteMessage.getFrom());

        ModelNotifications modelNotifications = new ModelNotifications();
        modelNotifications.setId(remoteMessage.getFrom());
        modelNotifications.setTitle(remoteMessage.getNotification().getTitle());
        modelNotifications.setDescription(remoteMessage.getNotification().getBody());
        modelNotifications.setExtra(remoteMessage.getData().get(KEY_EXTRA));


        showNotification(modelNotifications);
    }

    private void showNotification(ModelNotifications modelNotifications){

        Intent intent = new Intent(this,PerfilFragment.class);
        intent.putExtra(KEY_EXTRA,modelNotifications.getExtra());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSonUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationbuilder= new NotificationCompat.Builder(this)
               .setSmallIcon(R.drawable.ic_send)
               .setContentTitle(modelNotifications.getTitle())
               .setContentText(modelNotifications.getDescription())
               .setAutoCancel(true)
               .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
               .setSound(defaultSonUri)
               .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0,notificationbuilder.build());
    }
}
