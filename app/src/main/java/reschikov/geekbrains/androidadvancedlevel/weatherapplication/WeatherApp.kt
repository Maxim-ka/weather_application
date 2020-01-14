package reschikov.geekbrains.androidadvancedlevel.weatherapplication

import android.app.Application
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.android.ext.koin.androidContext
import org.koin.android.logger.AndroidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.EmptyLogger
import org.koin.core.logger.Logger
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.di.appModule
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.di.dataBindingModule
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.di.viewModelModule
import timber.log.Timber

class WeatherApp : Application() {

    @ExperimentalCoroutinesApi
    override fun onCreate() {
        super.onCreate()

        startKoin {
            logger(setLogger())
            androidContext (this@WeatherApp)
            modules(listOf(appModule, viewModelModule, dataBindingModule))
        }
        Timber.plant(Timber.DebugTree())
    }

    private fun setLogger(): Logger{
        return if(BuildConfig.DEBUG) AndroidLogger() else EmptyLogger()
    }
}