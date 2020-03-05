package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.databinding

import android.view.ViewGroup
import androidx.databinding.BindingAdapter
import com.google.android.material.card.MaterialCardView

@BindingAdapter("size")
fun setWidth(cardView: MaterialCardView, size: Int){
    cardView.layoutParams = ViewGroup.LayoutParams(size, ViewGroup.LayoutParams.WRAP_CONTENT)
}