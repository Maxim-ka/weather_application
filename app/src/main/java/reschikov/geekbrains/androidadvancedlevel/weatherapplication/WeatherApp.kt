package reschikov.geekbrains.androidadvancedlevel.weatherapplication

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.android.ext.koin.androidContext
import org.koin.android.logger.AndroidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.EmptyLogger
import org.koin.core.logger.Logger
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.di.appModule
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.di.dataBindingModule
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.di.notificationModule
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.di.viewModelModule
import timber.log.Timber

class WeatherApp : Application() {

    @ExperimentalCoroutinesApi
    override fun onCreate() {
        super.onCreate()
        initChannelsNotifications()
        startKoin {
            logger(setLogger())
            androidContext (this@WeatherApp)
            modules(listOf(appModule, viewModelModule, dataBindingModule, notificationModule))
        }
        Timber.plant(Timber.DebugTree())
    }

    private fun initChannelsNotifications(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelPush = NotificationChannel(CHANNEL_ID_PUSH, "SimpleNotice",
                    NotificationManager.IMPORTANCE_DEFAULT)
            channelPush.description = "Notice Push"
            val channelSMS = NotificationChannel(CHANNEL_ID_SMS, "SMS sending and receiving notification",
                    NotificationManager.IMPORTANCE_DEFAULT)
            channelSMS.description = "Notice SMS"
            val notificationManager = baseContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channelPush)
            notificationManager.createNotificationChannel(channelSMS)
        }
    }

    private fun setLogger(): Logger{
        return if(BuildConfig.DEBUG) AndroidLogger() else EmptyLogger()
    }
}