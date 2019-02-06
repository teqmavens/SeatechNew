package teq.development.seatech.Utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.WakefulBroadcastReceiver;

import java.util.Random;
import java.util.logging.Handler;

import teq.development.seatech.Dashboard.DashBoardActivity;
import teq.development.seatech.R;

public class AlarmReceiver extends WakefulBroadcastReceiver {

    Ringtone ringtone;
    Context context;
    public static final String TAG = "job_";
    public static final int notifyID = 101;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        ringtone = RingtoneManager.getRingtone(context, alarmUri);


        ringtone.play();
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, DashBoardActivity.class), 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(TAG, "Urgent", NotificationManager.IMPORTANCE_LOW);
            channel.setDescription("Urgent Message from Seatech");
            context.getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }

        Notification notification = new NotificationCompat.Builder(context, TAG)
                .setContentTitle("Urgent Message")
                .setContentText("You have some urgent message regarding job.")
                .setAutoCancel(true)
                .setChannelId(TAG)
                .setSound(null)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.notiicon)
                .setShowWhen(true)
                .setColor(Color.GREEN)
                .setLocalOnly(true)
                .build();

        NotificationManagerCompat.from(context).notify(notifyID, notification);

        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ringtone.stop();
            }
        }, 8000);
    }
}
