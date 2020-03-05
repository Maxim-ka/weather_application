package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.*
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.request.command.GetByName
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.request.command.GetByPostCode
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.listplaces.ListPlaceViewModel

private const val ONLY_LETTER = "[^`~!@#\$%&*()_=+|/{};:'\",<.>?№\\d]+"
private const val ONLY_DIGIT = "\\d+"
private const val NOT_NUMBER = "\\D+"
private const val NUMBER_CHARS_IN_CODE = 2

@ExperimentalCoroutinesApi
class PlaceNameInputDialog : DialogFragment() {

    private val navController : NavController by lazy { findNavController() }
    private val model : ListPlaceViewModel by navGraphViewModels(R.id.nav_places)
    private val defaultRequest : String by lazy { resources.getStringArray(R.array.request).first() }
    private lateinit var request : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context?.let {ctx ->
            val sp = ctx.getSharedPreferences(PREFERENCE_REQUEST, Context.MODE_PRIVATE)
            sp.getString(KEY_REQUEST_SELECTION, defaultRequest)?.let {
                request = it
            }
        } ?: run { request = defaultRequest }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        savedInstanceState?.let { request = it.getString(KEY_REQUEST_SELECTION, defaultRequest) }
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.city_input_dialog, container, false)
        view.exit.setText(R.string.but_get_data)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setModeRequest()
        dialog?.setTitle(getString(R.string.title_prediction_location))
        exit.requestFocus()
    }

    private fun setModeRequest(){
        when(request){
            getString(R.string.fld_place_name) -> {
                dialog?.let {
                    it.actv_dialog_title.setText(R.string.title_specify_place_name)
                    it.setTilNamePlace(getString(R.string.city_name), getString(R.string.city_name))
                    it.exit.setOnClickListener { requestByName() }
                }
            }
            getString(R.string.fld_postcode) -> {
                dialog?.let{
                    it.actv_dialog_title.setText(R.string.title_specify_place_zip_code)
                    it.setTilNamePlace(getString(R.string.zip_code), getString(R.string.zip_code_example))
                    it.exit.setOnClickListener { requestByPostCode() }
                }
            }
            getString(R.string.fld_opencage) ->{
                dialog?.let {
                    it.actv_dialog_title.setText(R.string.title_indicate_in_ascending_order)
                    it.setTilNamePlace(getString(R.string.street_district_city_region_country),
                            getString(R.string.street_district_city_region_country))
                    it.exit.setOnClickListener { requestOpenCage() }
                }
            }
        }
    }

    private fun Dialog.setTilNamePlace(hint: String, helper: String){
        til_name_place.hint = hint
        til_name_place.helperText = helper
    }

    private fun requestByName(){
        val name = getNamePlace()
        val code = getCodeCountry()
        if (checkNamePlace(name) && checkCodeCountryField(code)){
            model.addPlace(GetByName("$name,$code"))
        }
    }

    private fun requestByPostCode(){
        val zip = getNamePlace()
        val code = getCodeCountry()
        if (checkZipCodePlace(zip) && checkCodeCountryField(code)){
            model.addPlace(GetByPostCode("$zip,$code"))
        }
    }

    private fun requestOpenCage(){
        val place = getNamePlace()
        val code = getCodeCountry()
        if (checkPlaceOpenCage(place) && checkCodeCountryField(code) &&
            navController.currentDestination?.id == R.id.placeNameInputDialog) {
            navController.navigate(R.id.action_placeNameInputDialog_to_choicePlaceDialog,
                Bundle().apply {
                    putString(KEY_PLACE, place)
                    putString(KEY_CODE, code)
                }
            )
        }
    }

    private fun getNamePlace() : String = tiet_name_place.text.toString().trim{ char -> char == SPACE}

    private fun getCodeCountry() : String = tiet_code_country.text.toString().trim{ char -> char == SPACE}

    private fun checkNamePlace(string: String): Boolean{
        return isFieldFilled(string, til_name_place) &&
               isWord(string, til_name_place)
    }

    private fun isWord(string: String, til: TextInputLayout): Boolean{
        if (string.matches(ONLY_LETTER.toRegex())){
            return showError(null, til)
        }
        return showError(getString(R.string.err_title), til)
    }

    private fun checkZipCodePlace(string: String): Boolean{
        return isFieldFilled(string, til_name_place) &&
               isNumber(string, til_name_place)
    }

    private fun isNumber(string: String, til: TextInputLayout): Boolean{
        if (string.matches(ONLY_DIGIT.toRegex())){
            return showError(null, til)
        }
        return showError(getString(R.string.err_not_number), til)
    }

    private fun checkPlaceOpenCage(string: String): Boolean{
        return isFieldFilled(string, til_name_place) &&
            doesNotContainNumbers(string, til_name_place)
    }

    private fun checkCodeCountryField(string: String): Boolean{
        return isFieldFilled(string, til_code_country) &&
           doesNotContainNumbers(string, til_code_country) &&
           isRequiredLength(string, til_code_country)
    }

    private fun isRequiredLength(string: String, til: TextInputLayout): Boolean {
        if (string.length != NUMBER_CHARS_IN_CODE){
            return showError(getString(R.string.err_consist_letters), til)
        }
        return showError(null, til)
    }

    private fun doesNotContainNumbers(string: String, til: TextInputLayout): Boolean{
        if (!string.matches(NOT_NUMBER.toRegex())){
            return showError(getString(R.string.err_contains_numbers), til)
        }
        return showError(null, til)
    }

    private fun isFieldFilled(string: String, til: TextInputLayout): Boolean{
        if (string.isEmpty() || string == SPACE.toString()) {
            return showError(getString(R.string.err_empty_field), til)
        }
        return showError(null, til)
    }

    private fun showError(msg: String?, til: TextInputLayout) : Boolean{
        til.error = msg
        return msg == null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_REQUEST_SELECTION, request)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        exit.setOnClickListener(null)
    }
}
