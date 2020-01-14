package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.base

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.domain.Success

abstract class BaseViewModel : ViewModel(){

    abstract val errorChannel : Channel<Throwable>
    abstract val successChannel : Channel<Success>
    val isProgressVisible = ObservableBoolean()

    fun getSuccessChannel(): ReceiveChannel<Success> = successChannel
    fun getErrorChannel(): ReceiveChannel<Throwable> = errorChannel

    protected suspend fun setError (e : Throwable) {
        errorChannel.send(e)
    }

    protected suspend fun setSuccess (success: Success) {
        successChannel.send(success)
    }
}