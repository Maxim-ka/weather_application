package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.notifications

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.R

private const val INITIAL_NUMBER = 2

class Notice(private val context: Context) : Notifiable{

    private var messageId = INITIAL_NUMBER
    private val notificationManager: NotificationManager by lazy {  context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager}

    override fun makeNote(title: String?, message: String?, channel: String): Notification {
        val stackBuilder = TaskStackBuilder.create(context)
        stackBuilder.addNextIntent(Intent())
        val resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        return NotificationCompat.Builder(context, channel)
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher_weather_app))
                .setSmallIcon(R.drawable.ic_notifications_active)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(resultPendingIntent)
                .build()
    }

    override fun show(notification: Notification, hisId: Int?){
        var id: Int = messageId
        hisId?.let { id = it } ?: run { messageId++ }
        notificationManager.notify(id, notification)
    }
}