package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.base

import androidx.databinding.ObservableArrayList
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
abstract class BaseListViewModel<T> : BaseViewModel()  {

    val observableList = ObservableArrayList<T>()

    protected fun setObservableList(items: List<T>){
        observableList.setList(items)
    }

    private fun <V> MutableList<V>.setList(items: List<V>){
        with(this){
            takeIf { isNotEmpty()} ?.clear()
            takeIf { items.isNotEmpty() } ?.addAll(items)
        }
    }
}