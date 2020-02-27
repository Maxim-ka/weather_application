package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.base

import android.content.Context
import android.content.res.Configuration
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

abstract class BaseRVAdapter<T> :
        RecyclerView.Adapter<BaseRVAdapter.ViewHolder<T>>() {

    abstract var list: MutableList<T>

    override fun onBindViewHolder(viewHolder: ViewHolder<T>, i: Int) {
        viewHolder.bind(list[i])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    protected fun determineWidth(context: Context) : Int {
        val dm = context.resources.displayMetrics
        val width = dm.widthPixels
        val height = dm.heightPixels
        return if (context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE){
            if (width < height) width else height
        } else {
            ViewGroup.LayoutParams.MATCH_PARENT
        }
    }

    abstract class ViewHolder<T>(view: View) :
            RecyclerView.ViewHolder(view){

        abstract fun bind(item: T)
    }
}

