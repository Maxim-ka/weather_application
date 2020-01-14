package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.button.*
import kotlinx.android.synthetic.main.city_input_dialog.*
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.KEY_CODE
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.KEY_PLACE
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.R

class PlaceNameInputDialog : DialogFragment() {

    private val navController : NavController by lazy { findNavController() }
    private lateinit var place: String
    private lateinit var code: String

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setTitle(R.string.you_must_enter_place)
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.city_input_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        exit.setText(R.string.get_data)
        exit.setOnClickListener{
            takeIf { checkNamePlaceField() && checkCodeCountryField() &&
                    navController.currentDestination?.id == R.id.placeNameInputDialog
            }?.run {
                navController.navigate(R.id.action_placeNameInputDialog_to_choicePlaceDialog, Bundle().apply {
                    putString(KEY_PLACE, place)
                    putString(KEY_CODE, code)
                })
            }
        }
    }

    private fun checkNamePlaceField(): Boolean{
        if (checkErrorField(tiet_name_place, til_name_place)){
            return false
        }
        place = tiet_name_place.text.toString()
        return true
    }

    private fun checkCodeCountryField(): Boolean{
        if (checkErrorField(tiet_code_country, til_code_country)){
            return false
        }
        code = tiet_code_country.text.toString()
        if (code.length != 2){
            til_code_country.error = "The code does NOT consist of TWO letters"
            return false
        }
        return true
    }

    private fun checkErrorField(tiet: TextInputEditText, til: TextInputLayout): Boolean{
        tiet.text?.let {
            val sequence = it.trim{ char -> char == ' '}
            if (sequence.isEmpty()) {
                til.error = "empty fieldCurrentState"
                return true
            }
            if (!sequence.matches(getString(R.string.not_number).toRegex())){
                til.error = "contains numbers"
                return true
            }
            til.error = null
            return false
        }
        til.error = "The fieldCurrentState is not filled"
        return true
    }
}
