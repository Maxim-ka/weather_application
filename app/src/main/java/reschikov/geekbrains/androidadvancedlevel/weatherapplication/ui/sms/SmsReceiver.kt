package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.sms

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.koin.core.KoinComponent
import org.koin.core.get
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.*
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.notifications.Notifiable

class SmsReceiver : BroadcastReceiver(), KoinComponent {

    private var notifiable : Notifiable? = null

    override fun onReceive(context: Context, intent: Intent) {
        intent.run {
            action?.let {
                notifiable = get<Notifiable>()
                var message: String? = null
                when (it) {
                    ACTION_SENT_SMS -> {
                        message = if (resultCode != Activity.RESULT_OK) context.getString(R.string.err_msg_not_sent) else context.getString(R.string.message_sent)
                    }
                    ACTION_DELIVERED_SMS -> {
                        message = if (resultCode != Activity.RESULT_OK) context.getString(R.string.err_msg_not_delivered) else context.getString(R.string.message_delivered)
                    }
                }
                message?.let {msg ->
                    notifiable?.makeNote(getStringExtra(KEY_RECIPIENT), msg, CHANNEL_ID_SMS)?.let {notification ->
                        notifiable?.show(notification,null) }
                    }
                notifiable = null
            }
        }
    }
}