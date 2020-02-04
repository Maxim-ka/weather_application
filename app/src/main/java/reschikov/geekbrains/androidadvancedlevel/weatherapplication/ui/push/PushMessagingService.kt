package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.push

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.koin.core.KoinComponent
import org.koin.core.inject
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.CHANNEL_ID_PUSH
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.notifications.Notifiable

private const val MESSAGE_ID_FIREBASE = 1

class PushMessagingService : FirebaseMessagingService(), KoinComponent {

    private val notifiable: Notifiable by inject()

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        remoteMessage.notification?.let{
            notifiable.show(notifiable.makeNote(it.title, it.body, CHANNEL_ID_PUSH), MESSAGE_ID_FIREBASE)
        }
    }
}