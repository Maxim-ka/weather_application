package reschikov.geekbrains.androidadvancedlevel.weatherapplication.di

import androidx.room.Room
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.SCOPE_GEO
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.SCOPE_LOCAL
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.SCOPE_WEATHER
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.*
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.database.AppDatabase
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.database.DataBaseProvider
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.GeoCoordinatesProvider
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.LocateCoordinatesProvider
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.WeatherServiceProvider
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.coordinatedeterminants.AndroidCoordinateDeterminant
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.coordinatedeterminants.GoogleCoordinateDeterminant
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.repository.Derivable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.databinding.DataBindingAdapter
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.listplaces.ListPlaceModelFactory
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.notifications.Notice
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.notifications.Notifiable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.sms.SenderViewModel
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.weather.WeatherViewModel
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.unit.TemperatureColor
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.unit.WindDirection

private const val NAME_DATABASE = "weather.db"

val appModule = module {
    single { Room.databaseBuilder(get(), AppDatabase::class.java, NAME_DATABASE)
                .fallbackToDestructiveMigration()
                .build() }
    single<Storable> { DataBaseProvider(get()) }
    single<Derivable> {Repository(get(), Mapping(TemperatureColor(get()), WindDirection(get())))}
}

val weatherModule  = module {
    scope(named(SCOPE_WEATHER)) {
        scoped<RequestedWeather> { WeatherServiceProvider(androidContext()) }
    }
}

val geoModule  = module {
    scope(named(SCOPE_GEO)) {
        factory <Geocoded> { GeoCoordinatesProvider(androidContext()) }
    }
}

val locationModule  = module {
    scope(named(SCOPE_LOCAL)) {
        factory<IssuedCoordinates> { LocateCoordinatesProvider(androidContext(),
            GoogleCoordinateDeterminant(androidContext()),
            AndroidCoordinateDeterminant(androidContext()))}
    }
}

@ExperimentalCoroutinesApi
val viewModelModule = module {
    viewModel { WeatherViewModel(get(), BroadcastChannel(Channel.CONFLATED), BroadcastChannel(Channel.CONFLATED)) }
    single { ListPlaceModelFactory(get()) }
    viewModel { SenderViewModel() }
}

val dataBindingModule = module {
    single { DataBindingAdapter() }
}

val notificationModule = module {
    single<Notifiable> { Notice(get())}
}