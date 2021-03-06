package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.weather.forecast

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableBoolean
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.R
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.database.model.ForecastTable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.databinding.ItemForecastdayBinding
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.base.BaseRVAdapter
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.base.OnItemClickListener
import java.lang.ref.WeakReference

class ForecastRVAdapter(private val weakOnItemClickListener: WeakReference<OnItemClickListener<ForecastTable>>)
    : BaseRVAdapter<ForecastTable>() {

    val isShownSelection = ObservableBoolean()
    override var list: MutableList<ForecastTable> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(viewGroup.context)
        val binding: ItemForecastdayBinding = DataBindingUtil.inflate(layoutInflater,
            R.layout.item_forecastday, viewGroup, false)
        binding.size = determineWidth(viewGroup.context)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: BaseRVAdapter.ViewHolder<ForecastTable>, i: Int) {
        super.onBindViewHolder(viewHolder, i)
        val holder = viewHolder as ViewHolder
        holder.binding.isDisplaySelecting = isShownSelection
        holder.binding.executePendingBindings()
    }

    override fun onViewAttachedToWindow(holder: BaseRVAdapter.ViewHolder<ForecastTable>) {
        super.onViewAttachedToWindow(holder)
        holder.itemView.setOnClickListener{
            if (isShownSelection.get())  {
                weakOnItemClickListener.get()?.onItemClick(list[holder.adapterPosition])
            }
        }
    }
    override fun onViewDetachedFromWindow(holder: BaseRVAdapter.ViewHolder<ForecastTable>) {
        super.onViewDetachedFromWindow(holder)
        holder.itemView.setOnClickListener(null)
    }

    class ViewHolder(var binding: ItemForecastdayBinding) :
           BaseRVAdapter.ViewHolder<ForecastTable>(binding.root){

       override fun bind(item: ForecastTable) {
           binding.forecast = item
       }
   }
}
