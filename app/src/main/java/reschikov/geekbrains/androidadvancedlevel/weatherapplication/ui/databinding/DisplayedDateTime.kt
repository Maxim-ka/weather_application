package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.databinding

import android.widget.TextView
import androidx.databinding.BindingAdapter

interface DisplayedDateTime {

    @BindingAdapter("date")
    fun setDate(textView: TextView, dt: Long)

    @BindingAdapter("current_time")
    fun setCurrentTime(textView: TextView, dt: Long)

    @BindingAdapter("time")
    fun setTime(textView: TextView, dt: Long)
}