package reschikov.geekbrains.androidadvancedlevel.weatherapplication.di

import androidx.room.Room
import kotlinx.coroutines.channels.Channel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.Mapping
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.Repository
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.Storable
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
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.weather.WeatherViewModel
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.unit.TemperatureColor

private const val NAME_DATABASE = "weather.db"

val appModule = module {
    single { Room.databaseBuilder(get(), AppDatabase::class.java, NAME_DATABASE)
                .fallbackToDestructiveMigration()
                .build() }
    single<Storable> { DataBaseProvider(get()) }
    single<Derivable> { Repository(Mapping(TemperatureColor(get())), get(),
            WeatherServiceProvider(androidContext()),
            GeoCoordinatesProvider(androidContext()),
            LocateCoordinatesProvider(androidContext(),
                GoogleCoordinateDeterminant(androidContext()),
                AndroidCoordinateDeterminant(androidContext()))
            )
    }
}

val viewModelModule = module {
    viewModel { WeatherViewModel(get(), Channel(), Channel()) }
    single { ListPlaceModelFactory(get()) }
}

val dataBindingModule = module {
    single { DataBindingAdapter() }
}