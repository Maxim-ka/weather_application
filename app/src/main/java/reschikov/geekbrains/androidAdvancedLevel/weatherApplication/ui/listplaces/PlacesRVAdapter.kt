package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.listplaces

import androidx.recyclerview.widget.DiffUtil
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.domain.Place
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.base.OnItemClickListener
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.dialogs.ResultPlacesRVAdapter
import kotlin.properties.Delegates

class PlacesRVAdapter (private val remotelyStored: RemotelyStored<Place.Result>,
                       onItemClickListener: OnItemClickListener<Place.Result>) :
        ResultPlacesRVAdapter(onItemClickListener),
        Removable {

    override var list: MutableList<Place.Result> by Delegates.observable(mutableListOf()){
        _, oldValue, newValue ->
        run {
            val diffUtilCallback = ListPlacesDiffUtilCallback(oldValue, newValue)
            DiffUtil.calculateDiff(diffUtilCallback).dispatchUpdatesTo(this)
        }
    }

    override fun delete(position: Int) {
        remotelyStored.remove(list[position])
    }
}

