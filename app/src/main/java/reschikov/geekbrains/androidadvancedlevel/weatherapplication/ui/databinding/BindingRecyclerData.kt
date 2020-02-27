package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.databinding

import android.content.res.Configuration
import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableArrayList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.base.BaseRVAdapter

@BindingAdapter("list")
fun <T> installDataIntoAdapter(recyclerView: RecyclerView, observableArrayList: ObservableArrayList<T>?){
    recyclerView.adapter?.let {
        observableArrayList?.let {observableList ->  (it as BaseRVAdapter<T>).list = observableList.toMutableList() }
    }
}

@BindingAdapter("orientation")
fun determineOrientation(recyclerView: RecyclerView, config: Int){
    (recyclerView.layoutManager as LinearLayoutManager).apply {
        orientation = if (config == Configuration.ORIENTATION_LANDSCAPE){
            LinearLayoutManager.HORIZONTAL
        } else {
            LinearLayoutManager.VERTICAL
        }
    }
}
