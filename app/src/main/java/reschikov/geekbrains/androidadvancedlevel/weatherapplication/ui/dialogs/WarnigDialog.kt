package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.KEY_MESSAGE
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.KEY_TITLE
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.R
import timber.log.Timber

class WarningDialog : DialogFragment(){

    private var title: Int = 0
    private var message: Int = 0

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        savedInstanceState?.let { getArgumentsFromBundle(it)
        } ?: arguments?.let { getArgumentsFromBundle(it)  }
        Timber.i("onCreateDialog")
        return activity?.let {
            AlertDialog.Builder(it, R.style.Theme_MaterialComponents_Light_Dialog_Alert)
                .setTitle(title)
                .setIcon(R.drawable.ic_warning)
                .setMessage(message)
                .setPositiveButton(R.string.ok) {dialog, which ->
                    findNavController().popBackStack()}
                .create()
        } ?:return super.onCreateDialog(savedInstanceState)
    }

    private fun getArgumentsFromBundle(bundle: Bundle){
        bundle.apply {
            title = getInt(KEY_TITLE)
            message = getInt(KEY_MESSAGE)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.apply {
            putInt(KEY_TITLE, title)
            putInt(KEY_MESSAGE, message)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Timber.i("onDestroyView")
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.i("onDestroy")
    }
}