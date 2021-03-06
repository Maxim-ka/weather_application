package reschikov.geekbrains.androidadvancedlevel.weatherapplication

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.multidex.MultiDexApplication
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.android.ext.koin.androidContext
import org.koin.android.logger.AndroidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.EmptyLogger
import org.koin.core.logger.Logger
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.di.*
import timber.log.Timber

class WeatherApp : MultiDexApplication() {

    @ExperimentalCoroutinesApi
    override fun onCreate() {
        super.onCreate()
        initChannelsNotifications()
        startKoin {
            logger(setLogger())
            androidContext (this@WeatherApp)
            modules(listOf(appModule, weatherModule, locationModule, geoModule,
                viewModelModule, notificationModule))
        }
        Timber.plant(Timber.DebugTree())
    }

    private fun initChannelsNotifications(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelPush = NotificationChannel(CHANNEL_ID_PUSH, getString(R.string.notif_name_simple_notice),
                    NotificationManager.IMPORTANCE_DEFAULT)
            channelPush.description = getString(R.string.notif_push)
            val channelSMS = NotificationChannel(CHANNEL_ID_SMS, getString(R.string.notif_name_sms),
                    NotificationManager.IMPORTANCE_DEFAULT)
            channelSMS.description = getString(R.string.notif_sms)
            val notificationManager = baseContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channelPush)
            notificationManager.createNotificationChannel(channelSMS)
        }
    }

    private fun setLogger(): Logger{
        return if(BuildConfig.DEBUG) AndroidLogger() else EmptyLogger()
    }
}