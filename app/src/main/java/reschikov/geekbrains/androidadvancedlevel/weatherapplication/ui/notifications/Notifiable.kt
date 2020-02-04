package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.notifications

import android.app.Notification

interface Notifiable {

    fun makeNote(title: String?, message: String?, channel: String): Notification
    fun show(notification: Notification, hisId: Int?)
}