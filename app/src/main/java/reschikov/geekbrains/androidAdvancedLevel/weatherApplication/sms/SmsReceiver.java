package reschikov.geekbrains.androidadvancedlevel.weatherapplication.sms;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.widget.Toast;

import reschikov.geekbrains.androidadvancedlevel.weatherapplication.R;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.Rules;

public class SmsReceiver extends BroadcastReceiver {

    private int messageId;

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent != null && intent.getAction() != null ){
            String message;
            switch (intent.getAction()) {
                case Rules.ACTION_SENT_SMS:
                    if (getResultCode() != Activity.RESULT_OK) message = context.getString(R.string.message_not_sent);
                    else message = context.getString(R.string.message_sent);
                    makeNote(context, intent.getStringExtra(Rules.KEY_RECIPIENT), message);
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                    break;
                case Rules.ACTION_DELIVERED_SMS:
                    if (getResultCode() != Activity.RESULT_OK) message = context.getString(R.string.message_not_delivered);
                    else message = context.getString(R.string.message_delivered);
                    makeNote(context, intent.getStringExtra(Rules.KEY_RECIPIENT), message);
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    private void makeNote (Context context, String addressFrom, String message) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "2")
            .setSmallIcon(R.drawable.ic_stat_notifications)
            .setContentTitle(context.getString(R.string.format_one_string, addressFrom))
            .setContentText(message)
            .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher_weather_app))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true);

        Intent resultIntent = new Intent();

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(resultPendingIntent);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(messageId++, builder.build());
    }
}
