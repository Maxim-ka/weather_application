package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.listplaces

import androidx.recyclerview.widget.DiffUtil
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.domain.Place

class ListPlacesDiffUtilCallback(private val oldList: List<Place>, private val newList: List<Place>) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].lat == newList[newItemPosition].lat &&
               oldList[oldItemPosition].lon == newList[newItemPosition].lon
    }

    override fun getOldListSize():Int {
        return oldList.size
    }

    override fun getNewListSize(): Int{
        return newList.size
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].name == newList[newItemPosition].name
    }
}