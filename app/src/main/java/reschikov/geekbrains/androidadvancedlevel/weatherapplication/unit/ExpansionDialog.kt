package reschikov.geekbrains.androidadvancedlevel.weatherapplication.unit

import android.graphics.Color
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.R

fun showMessage(view: View, message: String, color: Int){
    Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE).apply {
        setBackgroundTint(Color.WHITE)
        setTextColor(color)
        setAction(R.string.ok) {
            dismiss()
        }
    }
    .show()
}

fun Fragment.showAlertDialog(title: Int, message: String){
    activity?.let {
        AlertDialog.Builder(it, R.style.Theme_MaterialComponents_Light_Dialog_Alert)
            .setTitle(title)
            .setIcon(R.drawable.ic_warning)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(R.string.ok){dialog, which ->
                dialog.dismiss()
            }
            .create()
            .show()
    }
}