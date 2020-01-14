package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.weather

import androidx.databinding.ObservableField
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.database.model.CurrentTable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.database.model.ForecastTable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.domain.DataWeather
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.domain.Success
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.repository.Derivable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.base.BaseListViewModel
import timber.log.Timber

class WeatherViewModel(private var derivable: Derivable?,
                       override val errorChannel: Channel<Throwable>,
                       override val successChannel: Channel<Success>) :
        BaseListViewModel<ForecastTable>() {

    val fieldCurrentState = ObservableField<CurrentTable>()

    init {
        Timber.i("init")
    }

    fun getStatePlace(lat: Double, lon: Double){
        viewModelScope.launch(Dispatchers.IO) {
            isProgressVisible.set(true)
            try {
                derivable?.let { getWeather(it.getDataWeather(lat, lon)) }
            } catch (e: Throwable) {
                setError(e)
            } finally {
                isProgressVisible.set(false)
            }
        }
    }

    fun getStateLastPlace(){
        viewModelScope.launch(Dispatchers.IO){
            Timber.i("getStateLastPlace")
            isProgressVisible.set(true)
            try {
                derivable?.getStateLastPlace()?.let {
                    getWeather(it)
                } ?: setSuccess(Success.LastPlace(null))
            } catch (e: Throwable) {
                setError(e)
            } finally {
                isProgressVisible.set(false)
            }
        }
    }

    fun getStateCurrentPlace(){
        viewModelScope.launch(Dispatchers.IO) {
            isProgressVisible.set(true)
            try {
                derivable?.let { getWeather(it.getStateCurrentPlace()) }
            } catch (e: Throwable) {
                setError(e)
            } finally {
                isProgressVisible.set(false)
            }
        }
    }

    private suspend fun getWeather(data: DataWeather){
        data.takeIf { it is DataWeather.Error }?.let {
            setError((it as DataWeather.Error).error)
        } ?: setWeather(data)
    }

    private fun setWeather(data: DataWeather){
        with(data as DataWeather.Data){
            fieldCurrentState.set(currentTable)
            setObservableList(forecasts)
        }
    }

    override fun onCleared() {
        super.onCleared()
        derivable = null
        Timber.i("onCleared()")
    }
}