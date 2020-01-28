package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.listplaces

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.launch
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.domain.Place
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.repository.Derivable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.base.BaseListViewModel

@ExperimentalCoroutinesApi
class ListPlaceViewModel(private var derivable: Derivable?,
                         override val errorChannel: BroadcastChannel<Throwable?>,
                         override val booleanChannel: BroadcastChannel<Boolean>) :
        BaseListViewModel<Place>(),
        AddablePlace{

    private var foundPlace: String? = null
    private var foundCode: String? = null
    private var results: List<Place> = listOf()

    init {
        loadSavePlaces()
    }

    private fun loadSavePlaces() {
        viewModelScope.launch(Dispatchers.IO) {
            isProgressVisible.set(true)
            derivable?.let {derivable ->
                derivable.getListCities().run {
                   toDistribute(first, second)
                }
            }
            isProgressVisible.set(false)
        }
    }

    override fun addPlaceByName(name: String){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                isProgressVisible.set(true)
                derivable?.let {derivable ->
                    derivable.addPlaceByName(name).run {
                        toDistribute(first, second)
                    }
                }
            } finally {
                finish()
            }
        }
    }

    override fun addPlaceByZipCode(postCode: String){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                isProgressVisible.set(true)
                derivable?.let {derivable ->
                    derivable.addPlaceByZipCode(postCode).run {
                        toDistribute(first, second)
                    }
                }
            } finally {
                finish()
            }
        }
    }

    @ExperimentalCoroutinesApi
    override fun findPlaces(place: String, code: String): ReceiveChannel<List<Place>?> {
        return viewModelScope.produce(Dispatchers.IO) {
            takeIf {results.isEmpty() && !foundPlace.equals(place) && !foundCode.equals(code) }?.run {
                derivable?.let {derivable ->
                    derivable.determineLocationCoordinates(place, code).run {
                        first?.let {
                            send(it)
                            results = it
                        } ?: second?.let {
                            send(null)
                            setError(it)
                        }
                    }
                }
            } ?: send(results)
        }
    }

    override fun addPlaceByCoordinates(lat: Double, lon: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                isProgressVisible.set(true)
                derivable?.let {derivable ->
                    derivable.addSelectedPlace(lat, lon).run {
                        toDistribute(first, second)
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
                        toDistribute(first, second)
                    }
                }
            } finally {
                finish()
            }
        }
    }

    fun remove(item: Place) {
        viewModelScope.launch (Dispatchers.IO) {
            try {
                isProgressVisible.set(true)
                derivable?.let {derivable ->
                    derivable.deletePlace(item.lat, item.lon).run {
                        toDistribute(first, second)
                    }
                }
            } finally {
                finish()
            }
        }
    }

    private suspend fun toDistribute(list: List<Place>?, error: Throwable?){
        list?.let { setObservableList(it) }
        hasCities(observableList.isNotEmpty())
        setError(error)
    }

    private fun finish(){
        isProgressVisible.set(false)
    }

    override fun onCleared() {
        super.onCleared()
        derivable = null
    }
}