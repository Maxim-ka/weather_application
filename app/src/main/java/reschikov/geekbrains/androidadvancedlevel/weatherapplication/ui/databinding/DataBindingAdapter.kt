package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.databinding

import androidx.databinding.DataBindingComponent

class DataBindingAdapter : DataBindingComponent{

    override fun getDisplayedImage(): DisplayedImage = BindingDataImage()

    override fun getDisplayedDateTime(): DisplayedDateTime = BindingDataTime()
}