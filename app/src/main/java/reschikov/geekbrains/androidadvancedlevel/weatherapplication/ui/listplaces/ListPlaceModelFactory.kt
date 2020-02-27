package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.listplaces

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.repository.Derivable

class ListPlaceModelFactory(private var derivable: Derivable?) : ViewModelProvider.Factory {

    @ExperimentalCoroutinesApi
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val repository = derivable
        derivable = null
        return modelClass.getConstructor(BroadcastChannel::class.java, BroadcastChannel::class.java,
                Derivable::class.java)
            .newInstance(BroadcastChannel<Throwable?>(Channel.CONFLATED),
                BroadcastChannel<Boolean>(Channel.CONFLATED), repository)
    }
}