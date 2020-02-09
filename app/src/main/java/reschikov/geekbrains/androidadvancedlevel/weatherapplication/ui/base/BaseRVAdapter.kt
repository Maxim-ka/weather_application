package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.base

import android.content.Context
import android.content.res.Configuration
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableArrayList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView

abstract class BaseRVAdapter<T> :
        RecyclerView.Adapter<BaseRVAdapter.ViewHolder<T>>(){

    companion object{
        @BindingAdapter("list")
        @JvmStatic
        fun <T> installDataIntoAdapter(recyclerView: RecyclerView, observableArrayList: ObservableArrayList<T>){
            (recyclerView.adapter as BaseRVAdapter<T>).list = observableArrayList.toMutableList()
        }

        @BindingAdapter("orientation")
        @JvmStatic
        fun determineOrientation(recyclerView: RecyclerView, config: Int){
            (recyclerView.layoutManager as LinearLayoutManager).apply {
                orientation = if (config == Configuration.ORIENTATION_LANDSCAPE){
                    LinearLayoutManager.HORIZONTAL
                } else {
                    LinearLayoutManager.VERTICAL
                }
            }
        }

        @BindingAdapter("size")
        @JvmStatic
        fun setWidth(cardView: MaterialCardView, size: Int){
            cardView.layoutParams = LinearLayout.LayoutParams(size, ViewGroup.LayoutParams.WRAP_CONTENT)
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
}

