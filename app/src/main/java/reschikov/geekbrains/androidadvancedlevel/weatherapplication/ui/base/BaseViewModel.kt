package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.base

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ReceiveChannel

@ExperimentalCoroutinesApi
abstract class BaseViewModel : ViewModel(){

    abstract var errorChannel : BroadcastChannel<Throwable?>
    val isProgressVisible = ObservableBoolean()

    fun getErrorChannel(): ReceiveChannel<Throwable?>? = errorChannel.openSubscription()

    protected suspend fun setError (e : Throwable?) {
        errorChannel.send(e)
    }

    abstract fun addStateOfCurrentPlace()

    override fun onCleared() {
        super.onCleared()
        errorChannel.cancel()
    }
}