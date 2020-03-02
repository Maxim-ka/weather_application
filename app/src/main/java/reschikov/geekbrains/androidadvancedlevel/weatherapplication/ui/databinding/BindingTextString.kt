package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.databinding

import android.text.SpannableString
import android.text.Spanned
import android.text.style.RelativeSizeSpan
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.BindingAdapter
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.SPACE

private const val SCALE = 0.8f

@BindingAdapter("string")
fun setIntoText(textView : AppCompatTextView, string: String){
    val spanString = SpannableString(string)
    spanString.setSpan(RelativeSizeSpan(SCALE), string.lastIndexOf(SPACE), string.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    textView.text = spanString
}