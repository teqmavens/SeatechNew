package teq.development.seatech.Chat;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.Random;

import teq.development.seatech.App;
import teq.development.seatech.Dashboard.DashBoardActivity;
import teq.development.seatech.Dashboard.Notifications;
import teq.development.seatech.LoginActivity;
import teq.development.seatech.R;
import teq.development.seatech.Utils.AlarmReceiver;
import teq.development.seatech.Utils.AppConstants;
import teq.development.seatech.Utils.HandyObject;

/**
 * Created by vibrantappz on 11/30/2016.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public static boolean fromnotii;
    //public static final int notifyID = 9001;

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
                remoteMessage.getData().get("sender_name"), remoteMessage.getData().get("urgent"), remoteMessage.getData().get("id"), remoteMessage.getData().get("sender_id"));
    }


    private void sendNotification(String messageBody, String jobid, String type, String sendername, String urgent, String msgid, String senderid) {
        Intent resultIntent;
        if (type.equalsIgnoreCase("chat")) {
            if (urgent.equalsIgnoreCase("1")) {
                setAlarm();
            }
            sendDeliverStatus(msgid, senderid);
            resultIntent = new Intent(this, ChatActivity.class);
        } else {
            resultIntent = new Intent(this, Notifications.class);
        }
        resultIntent.putExtra("type", type);
        resultIntent.putExtra("jobid", jobid);
        resultIntent.setAction(Long.toString(System.currentTimeMillis()));
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0,
                resultIntent, 0);

        NotificationCompat.Builder mNotifyBuilder;
        NotificationManager mNotificationManager;
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String messageBodyn = "";
        if (sendername != null && !sendername.isEmpty()) {
            messageBodyn = "Message From " +sendername+" #"+jobid;
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
        // mNotificationManager.notify(notifyID, mNotifyBuilder.build());
        mNotificationManager.notify(new Random().nextInt(), mNotifyBuilder.build());
        //https://developer.android.com/training/notify-user/navigation
        // https://developer.android.com/reference/android/app/PendingIntent
    }

    private void setAlarm() {
        Intent myIntent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, myIntent, 0);
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(this.ALARM_SERVICE);

        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime(),
                    5 * 60 * 1000,
                    pendingIntent);
        } else {
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime(),
                    5 * 60 * 1000,
                    pendingIntent);
        }

//http://android-er.blogspot.com/2012/05/cancel-alarm-with-matching.html
    }

    private void sendDeliverStatus(String msgid, String senderid) {
        try {
            Socket mSocket = IO.socket("http://132.148.241.93:3000");
            mSocket.connect();
            JSONObject jobj = new JSONObject();
            jobj.put("customId", Integer.parseInt(HandyObject.getPrams(this, AppConstants.LOGINTEQPARENT_ID)));
            jobj.put("device_id", HandyObject.getPrams(this, AppConstants.DEVICE_TOKEN));
            mSocket.emit("storeClientInfo", jobj);


            Log.e("DELEIVER FROM NOTIIII", "notiiiii");
            JSONObject jobj_delv = new JSONObject();
            jobj_delv.put("id", Integer.parseInt(msgid));
            jobj_delv.put("sender_id", senderid);
            JSONArray jarry_delv = new JSONArray();
            jarry_delv.put(jobj_delv);
            // DashBoardActivity.mSocket.emit("delivered", jarry_delv);
            mSocket.emit("delivered1", jarry_delv);


        } catch (Exception e) {
        }
    }
}
