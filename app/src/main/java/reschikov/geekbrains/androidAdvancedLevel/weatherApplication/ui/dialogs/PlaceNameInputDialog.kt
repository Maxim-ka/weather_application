package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.button.*
import kotlinx.android.synthetic.main.button.view.*
import kotlinx.android.synthetic.main.city_input_dialog.*
import kotlinx.android.synthetic.main.city_input_dialog.view.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.android.ext.android.get
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.*
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.listplaces.ListPlaceModelFactory
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.listplaces.ListPlaceViewModel
import timber.log.Timber

@ExperimentalCoroutinesApi
class PlaceNameInputDialog : DialogFragment() {

    private val navController : NavController by lazy { findNavController() }
    private val model: ListPlaceViewModel by navGraphViewModels(R.id.nav_places){get<ListPlaceModelFactory>()}
    private lateinit var request: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sp = context!!.getSharedPreferences(PREFERENCE_REQUEST, Context.MODE_PRIVATE)
        request = sp.getString(KEY_REQUEST_SELECTION, resources.getStringArray(R.array.request).first())!!
        Timber.i("onCreate ${context != null}")
        Timber.i("onCreate request $request")
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        savedInstanceState?.let { request = it.getString(KEY_REQUEST_SELECTION, "Place name") }
        Timber.i("onCreateDialog")
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.city_input_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setModeRequest(view)
        exit.setText(R.string.get_data)
        Timber.i("onViewCreated $request")
    }

    private fun setModeRequest(view: View){
        when(request){
            "Place name" -> {
                dialog?.setTitle(R.string.specify_place_name)
                view.til_name_place.hint = getString(R.string.city_name)
                view.til_name_place.helperText = getString(R.string.city_name)
                view.exit.setOnClickListener { requestByName() }
            }
            "Postcode" -> {
                dialog?.setTitle(R.string.specify_place_zip_code)
                view.til_name_place.hint = getString(R.string.zip_code)
                view.til_name_place.helperText = getString(R.string.zip_code_example)
                view.exit.setOnClickListener { requestByPostCode() }
            }
            "OpenCage" ->{
                dialog?.setTitle(R.string.indicate_in_ascending_order)
                view.til_name_place.hint = getString(R.string.street_district_city_region_country)
                view.til_name_place.helperText = getString(R.string.street_district_city_region_country)
                view.exit.setOnClickListener { requestOpenCage() }
            }
        }
    }

    private fun requestByName(){
        takeIf { checkNamePlace() && checkCodeCountryField()}?.run {
            model.addPlaceByName("${tiet_name_place.text.toString()},${tiet_code_country.text.toString()}")
        }
    }

    private fun requestByPostCode(){
        takeIf { checkZipCodePlace() && checkCodeCountryField()}?.run {
            model.addPlaceByZipCode("${tiet_name_place.text.toString()},${tiet_code_country.text.toString()}")
        }
    }

    private fun requestOpenCage(){
        takeIf { checkPlaceOpenCage() && checkCodeCountryField() &&
            navController.currentDestination?.id == R.id.placeNameInputDialog
        }?.run {
            navController.navigate(R.id.action_placeNameInputDialog_to_choicePlaceDialog, Bundle().apply {
                putString(KEY_PLACE, tiet_name_place.text.toString())
                putString(KEY_CODE, tiet_code_country.text.toString())
            })
        }
    }

    private fun checkNamePlace(): Boolean{
        return isFieldFilled(tiet_name_place.text, til_name_place) &&
               isWord(tiet_name_place.text.toString(), til_name_place)
    }

    private fun isWord(string: String, til: TextInputLayout): Boolean{
        if (string.matches("[^`~!@#\$%&*()_=+|/{};:'\",<.>?№\\d]+".toRegex())){
            til.error = null
            return true
        }
        til.error = "Title contains extra characters"
        return false
    }

    private fun checkZipCodePlace(): Boolean{
        return isFieldFilled(tiet_name_place.text, til_name_place) &&
               isNumber(tiet_name_place.text.toString(), til_name_place)
    }

    private fun isNumber(string: String, til: TextInputLayout): Boolean{
        if (string.matches("\\d+".toRegex())){
            til.error = null
            return true
        }
        til.error = "not a number"
        return false
    }

    private fun checkPlaceOpenCage(): Boolean{
        return isFieldFilled(tiet_name_place.text, til_name_place) &&
            doesNotContainNumbers(tiet_name_place.text.toString(), til_name_place)
    }

    private fun checkCodeCountryField(): Boolean{
        return isFieldFilled(tiet_code_country.text, til_code_country) &&
           doesNotContainNumbers(tiet_code_country.text.toString(), til_code_country) &&
           isRequiredLength(tiet_code_country.text.toString(), til_code_country)
    }

    private fun isRequiredLength(string: String, til: TextInputLayout): Boolean {
        if (string.length != 2){
            til.error = "The code does NOT consist of TWO letters"
            return false
        }
        til.error = null
        return true
    }

    private fun doesNotContainNumbers(string: String, til: TextInputLayout): Boolean{
        if (!string.matches(getString(R.string.not_number).toRegex())){
            til.error = "contains numbers"
            return false
        }
        til.error = null
        return true
    }

    private fun isFieldFilled(editable: Editable?, til: TextInputLayout): Boolean{
        editable?.let {
            val sequence = it.trim{ char -> char == ' '}
            if (sequence.isEmpty()) {
                til.error = "empty fieldCurrentState"
                return false
            }
            til.error = null
            return true
        }
        til.error = "The fieldCurrentState is not filled"
        return false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_REQUEST_SELECTION, request)
    }
}
