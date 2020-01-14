package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.databinding

import android.graphics.drawable.Drawable
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.R
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.THREE_HOURS
import java.text.DateFormat
import java.util.*

private const val PADDING_DRAWABLE = 24

class BindingDataTime : DisplayedDateTime {

    @BindingAdapter("date")
    override fun setDate(textView: TextView, dt: Long){
        textView.text = DateFormat.getDateInstance(DateFormat.FULL).format(Date(dt))
    }

    @BindingAdapter("current_time")
    override fun setCurrentTime(textView: TextView, dt: Long){
        var warning: Drawable? = null
        if (System.currentTimeMillis() - dt >= THREE_HOURS){
            warning = ContextCompat.getDrawable(textView.context, R.drawable.ic_warning)
            textView.text = textView.context.getString(R.string.out_date)
        } else {
            setTime(textView, dt)
        }
        textView.setCompoundDrawablesWithIntrinsicBounds(warning,null, null, null)
        textView.compoundDrawablePadding = warning?.let { PADDING_DRAWABLE } ?: 0
    }

    @BindingAdapter("time")
    override fun setTime(textView: TextView, dt: Long){
        textView.text = DateFormat.getTimeInstance(DateFormat.MEDIUM).format(Date(dt))
    }
}