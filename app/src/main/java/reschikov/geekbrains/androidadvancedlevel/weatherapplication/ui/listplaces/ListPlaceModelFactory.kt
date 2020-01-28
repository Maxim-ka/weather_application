package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.listplaces

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.repository.Derivable

class ListPlaceModelFactory(private val derivable: Derivable) : ViewModelProvider.Factory {

    @ExperimentalCoroutinesApi
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(Derivable::class.java, BroadcastChannel::class.java, BroadcastChannel::class.java)
                .newInstance(derivable, BroadcastChannel<Throwable?>(Channel.CONFLATED), BroadcastChannel<Boolean>(Channel.CONFLATED))
    }
}