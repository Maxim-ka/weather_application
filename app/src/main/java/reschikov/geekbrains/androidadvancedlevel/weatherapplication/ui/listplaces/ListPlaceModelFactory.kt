package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.listplaces

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.channels.Channel
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.domain.Success
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.repository.Derivable

class ListPlaceModelFactory(private val derivable: Derivable) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(Derivable::class.java, Channel::class.java, Channel::class.java)
                .newInstance(derivable, Channel<Throwable>(), Channel<Success>())
    }
}