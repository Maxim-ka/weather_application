package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.databinding

import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso

private const val URL = "https://openweathermap.org/img/wn/"
private const val FILE = "@2x.png"

@BindingAdapter("src")
fun loadImage(image: AppCompatImageView, icon: String?){
    icon?.let { Picasso.get().load("$URL$it$FILE").into(image)}
}
