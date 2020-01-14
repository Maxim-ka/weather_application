package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.listplaces

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.launch
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.domain.Place
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.domain.Success
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.repository.Derivable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.base.BaseListViewModel
import timber.log.Timber


class ListPlaceViewModel(private var derivable: Derivable?,
                         override val errorChannel: Channel<Throwable>,
                         override val successChannel: Channel<Success>) :
        BaseListViewModel<Place.Result>(),
        AddablePlace{

    private var foundPlace: String? = null
    private var foundCode: String? = null
    private var results: List<Place.Result> = listOf()

    init {
        loadSavePlaces()
        Timber.i("init %d", System.identityHashCode(this))
    }

    private fun loadSavePlaces() {
        viewModelScope.launch(Dispatchers.IO) {
            isProgressVisible.set(true)
            derivable?.let {derivable ->
                derivable.getListCities().run {
                    list?.let {
                        if (it.isEmpty()) setSuccess(Success.LastPlace(null))
                        else setObservableList(it)
                    } ?: error?.let {
                        setError(it)
                    }
                }
            }
            isProgressVisible.set(false)
        }
    }

    @ExperimentalCoroutinesApi
    override fun findPlaces(place: String, code: String): ReceiveChannel<List<Place.Result>> {
        return viewModelScope.produce(Dispatchers.IO) {
            takeIf {results.isEmpty() && !foundPlace.equals(place) && !foundCode.equals(code) }?.run {
                derivable?.let {derivable ->
                    derivable.determineLocationCoordinates(place, code).run {
                        list?.let {
                            send(it)
                            results = it
                        } ?: error?.let {
                            setError(it)
                        }
                    }
                }
            } ?: send(results)
            Timber.i("results $results")
        }
    }

    override fun addPlace(lat: Double, lon: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                isProgressVisible.set(true)
                derivable?.let {derivable ->
                    derivable.addSelectedPlace(lat, lon).run {
                        toDistribute(list , error)
                    }
                }
            } finally {
                finish()
            }
        }
    }

    fun addCurrentPlace(){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                isProgressVisible.set(true)
                derivable?.let {derivable ->
                    derivable.addCurrentPlace().run {
                        toDistribute(list, error)
                    }
                }
            } finally {
                finish()
            }
        }
    }

    fun remove(item: Place.Result) {
        viewModelScope.launch (Dispatchers.IO) {
            try {
                isProgressVisible.set(true)
                derivable?.let {derivable ->
                    derivable.deletePlace(item.lat, item.lon).run {
                        toDistribute(list, error)
                    }
                }
            } finally {
                finish()
            }
        }
    }

    private suspend fun toDistribute(list: List<Place.Result>?, error: Throwable?){
        list?.let {
            setObservableList(it)
        }
        if (observableList.isEmpty()) setSuccess(Success.LastPlace(null))
        error?.let {
            setError(it)
        }
    }

    private fun finish(){
        isProgressVisible.set(false)
    }

    override fun onCleared() {
        super.onCleared()
        derivable = null
        Timber.i("!!!onCleared %d", System.identityHashCode(this))
    }
}