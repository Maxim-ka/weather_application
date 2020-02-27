package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.base

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ReceiveChannel

@ExperimentalCoroutinesApi
abstract class BaseViewModel : ViewModel(){

    abstract var errorChannel : BroadcastChannel<Throwable?>?
    abstract var booleanChannel : BroadcastChannel<Boolean>?
    val isProgressVisible = ObservableBoolean()

    fun getBooleanChannel(): ReceiveChannel<Boolean>? = booleanChannel?.openSubscription()
    fun getErrorChannel(): ReceiveChannel<Throwable?>? = errorChannel?.openSubscription()

    protected suspend fun setError (e : Throwable?) {
        errorChannel?.send(e)
    }

    protected suspend fun hasCities (hasCities: Boolean) {
        booleanChannel?.send(hasCities)
    }

    abstract fun addStateOfCurrentPlace()

    override fun onCleared() {
        super.onCleared()
        errorChannel?.cancel()
        booleanChannel?.cancel()
        errorChannel = null
        booleanChannel = null
    }
}