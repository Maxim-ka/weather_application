package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.databinding

import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.BindingAdapter

interface DisplayedDateTime {

    @BindingAdapter("date")
    fun setDate(textView: TextView, dt: Long)

    @BindingAdapter("current_time")
    fun setCurrentTime(textView: AppCompatTextView, dt: Long)

    @BindingAdapter("time")
    fun setTime(textView: TextView, dt: Long)

    @BindingAdapter("date_time")
    fun setDataTime(textView: TextView, dt: Long)
}