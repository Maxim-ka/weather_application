package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.databinding

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso

private const val URL = "http://openweathermap.org/img/wn/"
private const val FILE = "@2x.png"

@BindingAdapter("src")
fun loadImage(image: ImageView, icon: String?){
    icon?.let { Picasso.get().load("$URL$it$FILE").into(image)}
}
