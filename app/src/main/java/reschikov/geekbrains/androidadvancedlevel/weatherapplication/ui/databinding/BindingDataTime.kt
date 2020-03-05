package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.databinding

import android.graphics.drawable.Drawable
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.BindingAdapter
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.NAN_INT
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.R
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.THREE_HOURS
import java.text.DateFormat
import java.util.*

private const val PADDING_DRAWABLE = 24

@BindingAdapter("date")
fun setDate(textView: TextView, dt: Long){
    textView.text = DateFormat.getDateInstance(DateFormat.FULL).format(Date(dt))
}

@BindingAdapter("current_time")
fun setCurrentTime(textView: AppCompatTextView, dt: Long){
    var warning: Drawable? = null
    if (System.currentTimeMillis() - dt >= THREE_HOURS){
        warning = getDrawable(textView.context, R.drawable.ic_warning)
        textView.text = textView.context.getString(R.string.att_out_date)
    } else {
        setTime(textView, dt)
    }
    textView.setCompoundDrawablesWithIntrinsicBounds(warning,null, null, null)
    textView.compoundDrawablePadding = warning?.let { PADDING_DRAWABLE } ?: NAN_INT
}

@BindingAdapter("time")
fun setTime(textView: TextView, dt: Long){
    textView.text = DateFormat.getTimeInstance(DateFormat.MEDIUM).format(Date(dt))
}

@BindingAdapter("date_time")
fun setDataTime(textView: TextView, dt: Long){
    textView.text = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.MEDIUM).format(Date(dt))
}