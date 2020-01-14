package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.databinding

import android.widget.ImageView
import androidx.databinding.BindingAdapter

interface DisplayedImage {

    @BindingAdapter("src")
    fun loadImage(image: ImageView, icon: String?)
}