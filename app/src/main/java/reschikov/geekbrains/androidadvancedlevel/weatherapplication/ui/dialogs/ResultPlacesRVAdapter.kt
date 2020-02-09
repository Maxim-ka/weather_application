package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.dialogs

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.R
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.databinding.ItemCityBinding
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.domain.Place
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.base.BaseRVAdapter
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.base.OnItemClickListener

open class ResultPlacesRVAdapter(private val onItemClickListener: OnItemClickListener<Place>) : BaseRVAdapter<Place>() {

    override var list: MutableList<Place> = mutableListOf()
        set(value){
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<Place> {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: ItemCityBinding = DataBindingUtil.inflate(layoutInflater, R.layout.item_city, parent, false)
        val parentWidth = parent.width
        val width = determineWidth(parent.context)
        binding.size = if (parentWidth != 0 && parentWidth < width) parentWidth else width
        return PlaceViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder<Place>, i: Int) {
        super.onBindViewHolder(viewHolder, i)
        viewHolder.itemView.setOnClickListener {onItemClickListener.onItemClick(list[i])}
    }

    class PlaceViewHolder(private val binding: ItemCityBinding) :
            BaseRVAdapter.ViewHolder<Place>(binding.root){

        override fun bind(item: Place) {
            binding.place = item
            binding.executePendingBindings()
        }
    }
}