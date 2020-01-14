package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.weather.forecast

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.R
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.database.model.ForecastTable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.databinding.ItemForecastdayBinding
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.base.BaseRVAdapter
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.databinding.DataBindingAdapter

class ForecastRVAdapter(private val dataBindingAdapter: DataBindingAdapter) : BaseRVAdapter<ForecastTable>() {

    override var list: MutableList<ForecastTable> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(viewGroup.context)
        val binding: ItemForecastdayBinding = DataBindingUtil.inflate(layoutInflater, R.layout.item_forecastday, viewGroup, false, dataBindingAdapter)
        return ViewHolder(binding)
    }

   class ViewHolder(private val binding: ItemForecastdayBinding) :
           BaseRVAdapter.ViewHolder<ForecastTable>(binding.root){

       override fun bind(item: ForecastTable) {
           binding.forecast = item
           binding.executePendingBindings()
       }
   }
}
