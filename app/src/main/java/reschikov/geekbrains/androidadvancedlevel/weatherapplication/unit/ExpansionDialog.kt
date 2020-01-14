package reschikov.geekbrains.androidadvancedlevel.weatherapplication.unit

import android.Manifest
import android.graphics.Color
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.material.snackbar.Snackbar
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.REQUEST_MY_PERMISSIONS_ACCESS_LOCATION
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

fun FragmentActivity.showAlertDialog(title: Int, message: Int, hasAction: Boolean){
    also {
        AlertDialog.Builder(it, R.style.Theme_MaterialComponents_Light_Dialog_Alert)
            .setTitle(title)
            .setIcon(R.drawable.ic_warning)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(R.string.ok){dialog, which ->
                if (hasAction) requestPermissions(it)
                dialog.dismiss()
            }
            .create()
            .show()
    }
}

private fun requestPermissions(activity: FragmentActivity) {
    ActivityCompat.requestPermissions(activity,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION),
            REQUEST_MY_PERMISSIONS_ACCESS_LOCATION)
}