package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.push

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.koin.android.ext.android.get
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.CHANNEL_ID_PUSH
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.notifications.Notifiable

private const val MESSAGE_ID_FIREBASE = 1

class PushMessagingService : FirebaseMessagingService(){

    private var notifiable: Notifiable? = null

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        remoteMessage.notification?.let{msg ->
            if (notifiable == null) notifiable = get()
            notifiable?.let {
                it.show(it.makeNote(msg.title, msg.body, CHANNEL_ID_PUSH), MESSAGE_ID_FIREBASE)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        notifiable = null
    }
}