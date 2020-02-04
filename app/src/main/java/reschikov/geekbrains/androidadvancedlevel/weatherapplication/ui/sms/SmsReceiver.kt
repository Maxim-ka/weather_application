package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.sms

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.koin.core.KoinComponent
import org.koin.core.inject
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.*
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.notifications.Notifiable

class SmsReceiver : BroadcastReceiver(), KoinComponent {

    private val notifiable: Notifiable by inject()

    override fun onReceive(context: Context, intent: Intent) {
        intent.run {
            action?.let {
                var message: String? = null
                when (it) {
                    ACTION_SENT_SMS -> {
                        message = if (resultCode != Activity.RESULT_OK) context.getString(R.string.message_not_sent) else context.getString(R.string.message_sent)
                    }
                    ACTION_DELIVERED_SMS -> {
                        message = if (resultCode != Activity.RESULT_OK) context.getString(R.string.message_not_delivered) else context.getString(R.string.message_delivered)
                    }
                }
                message?.let {msg -> notifiable.show(notifiable.makeNote(getStringExtra(KEY_RECIPIENT), msg, CHANNEL_ID_SMS),null) }
            }
        }
    }
}