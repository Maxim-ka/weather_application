package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.push

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.CHANNEL_ID_PUSH
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.R

private const val MESSAGE_ID_FIREBASE = 1

class PushMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        remoteMessage.notification?.let{
            makeNote(it.title, it.body)
        }
    }

    private fun makeNote(title: String?, message: String?) {
        val notificationManager = baseContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val builder = NotificationCompat.Builder(baseContext, CHANNEL_ID_PUSH)
                .setSmallIcon(R.drawable.ic_notifications_active)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        notificationManager.notify(MESSAGE_ID_FIREBASE, builder.build())
    }
}