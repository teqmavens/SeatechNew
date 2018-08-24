package teq.development.seatech.Chat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import teq.development.seatech.Dashboard.DashBoardActivity;
import teq.development.seatech.Dashboard.Notifications;
import teq.development.seatech.LoginActivity;
import teq.development.seatech.R;

/**
 * Created by vibrantappz on 11/30/2016.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public static boolean fromnotii;
    public static final int notifyID = 9001;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.e("FCMService", "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d("FCMService", "Message data payload: " + remoteMessage.getData());
        }
        // Check if message contains a notification payload.
        //   if (remoteMessage.getNotification() != null) {
        //  Log.d("FCMService", "Message Notification Body: " + remoteMessage.getNotification().getBody());
        Log.e("MESSAGE", "Message Notification Body: " + remoteMessage.getData().get("message"));
        //  Log.e("BID", "Message Notification Body: " + remoteMessage.getData().get("bid"));
        Intent intent = new Intent("load_message");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        // if (!ChatViewFragment.chatting) {
        sendNotification(remoteMessage.getData().get("message"), remoteMessage.getData().get("job_id"), remoteMessage.getData().get("type"),
                remoteMessage.getData().get("sender_name"));
        // }

        //  }
    }


    private void sendNotification(String messageBody, String jobid, String type, String sendername) {
        Intent resultIntent;
        if (type.equalsIgnoreCase("chat")) {
            resultIntent = new Intent(this, ChatActivity.class);
        } else {
            resultIntent = new Intent(this, Notifications.class);
        }
        resultIntent.putExtra("type", type);
        resultIntent.putExtra("jobid", jobid);
        //    resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0,
                resultIntent, PendingIntent.FLAG_ONE_SHOT);
       /* String CHANNEL_ID = "101";// The id of the channel.
        CharSequence name = getString(R.string.channel_name);// The user-visible name of the channel.
        int importance = NotificationManager.IMPORTANCE_HIGH;*/

        NotificationCompat.Builder mNotifyBuilder;
        NotificationManager mNotificationManager;
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mNotificationManager.createNotificationChannel(mChannel);
        }*/
        String messageBodyn = "";
        if (sendername != null && !sendername.isEmpty()) {
            // if (sendername.equalsIgnoreCase("")) {
            messageBodyn = "Message From " + sendername;
        } else {
            messageBodyn = "Seatech";
        }
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.notiicon);
        mNotifyBuilder = new NotificationCompat.Builder(this);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
           /* mNotifyBuilder.setSmallIcon(R.drawable.icon_transperent);
            mNotifyBuilder.setColor(getResources().getColor(R.color.notification_color));*/
            mNotifyBuilder.setSmallIcon(R.drawable.notiicon)
                    .setLargeIcon(largeIcon)
                    .setColor(getResources().getColor(R.color.appbasiccolor))
                    .setContentText(messageBody)
                    .setContentTitle(messageBodyn);
        } else {
            mNotifyBuilder.setSmallIcon(R.drawable.notiicon);
        }

                /*.setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(largeIcon)
                .setContentText(messageBodyn)
                .setContentTitle("Seatech");*/
        // Set pending intent
        mNotifyBuilder.setContentIntent(resultPendingIntent);

        // Set Vibrate, Sound and Light
        int defaults = 0;
        defaults = defaults | Notification.DEFAULT_LIGHTS;
        defaults = defaults | Notification.DEFAULT_VIBRATE;
        defaults = defaults | Notification.DEFAULT_SOUND;

        mNotifyBuilder.setDefaults(defaults);
        // Set the content for Notification
        // mNotifyBuilder.setContentText(msg);
        // Set autocancel
        mNotifyBuilder.setAutoCancel(true);
        // Post a notification
        mNotificationManager.notify(notifyID, mNotifyBuilder.build());
    }
}
