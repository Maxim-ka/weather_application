package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.listplaces

import androidx.recyclerview.widget.DiffUtil
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.domain.Place
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.base.OnItemClickListener
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.dialogs.ResultPlacesRVAdapter
import java.lang.ref.WeakReference
import kotlin.properties.Delegates

class PlacesRVAdapter (private val weakRemotelyStored: WeakReference<RemotelyStored<Place>>,
                       weakOnItemClickListener: WeakReference<OnItemClickListener<Place>>) :
        ResultPlacesRVAdapter(weakOnItemClickListener),
        Removable {

    override var list: MutableList<Place> by Delegates.observable(mutableListOf()){
        _, oldValue, newValue ->
        run {
            val diffUtilCallback = ListPlacesDiffUtilCallback(oldValue, newValue)
            DiffUtil.calculateDiff(diffUtilCallback).dispatchUpdatesTo(this)
        }
    }

    override fun delete(position: Int) {
        weakRemotelyStored.get()?.remove(list[position])
    }
}

