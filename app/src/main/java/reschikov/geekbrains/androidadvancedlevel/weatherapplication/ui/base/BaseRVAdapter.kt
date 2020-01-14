package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.base

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableArrayList
import androidx.recyclerview.widget.RecyclerView

abstract class BaseRVAdapter<T> :
        RecyclerView.Adapter<BaseRVAdapter.ViewHolder<T>>(){

    companion object{
        @BindingAdapter("list")
        @JvmStatic
        fun <T> installDataIntoAdapter(recyclerView: RecyclerView, observableArrayList: ObservableArrayList<T>){
            (recyclerView.adapter as BaseRVAdapter<T>).list = observableArrayList.toMutableList()
        }
    }

    abstract var list: MutableList<T>

    override fun onBindViewHolder(viewHolder: ViewHolder<T>, i: Int) {
        viewHolder.bind(list[i])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    abstract class ViewHolder<T>(view: View) :
            RecyclerView.ViewHolder(view){

        abstract fun bind(item: T)
    }
}

