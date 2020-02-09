package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.settings

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.view.Menu
import androidx.core.content.edit
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.*
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.mainactivity.MainActivity
import java.text.DateFormat
import java.util.*

class FragmentSettings : PreferenceFragmentCompat(), OnSharedPreferenceChangeListener {

    private var textKeyWeather: EditTextPreference? = null
    private var textKeyOpenCage: EditTextPreference? = null
    private var viewBalanceRequests: Preference? = null
    private var viewResetDate: Preference? = null
    private var listPreference: ListPreference? = null
    private lateinit var spWeather: SharedPreferences
    private lateinit var spCage: SharedPreferences
    private lateinit var spRequest: SharedPreferences

    override fun onCreatePreferences(bundle: Bundle?, s: String?) {
        setPreferencesFromResource(R.xml.setting, s)

        textKeyWeather = findPreference(KEY_WEATHER)
        textKeyOpenCage = findPreference(KEY_OPEN_CAGE)
        viewBalanceRequests = findPreference(KEY_BALANCE_OF_REQUESTS)
        viewResetDate = findPreference(KEY_RESET_DATE)
        listPreference = findPreference(KEY_REQUEST_SELECTION)

        context?.let {
            spWeather = it.getSharedPreferences(PREFERENCE_OPEN_WEATHER, MODE_PRIVATE)
            spCage = it.getSharedPreferences(PREFERENCE_OPEN_CAGE, MODE_PRIVATE)
            spRequest = it.getSharedPreferences(PREFERENCE_REQUEST, MODE_PRIVATE)
            textKeyWeather?.summary = spWeather.getString(KEY_WEATHER, BuildConfig.OPEN_WEATHER_KEY)
            textKeyOpenCage?.summary = spCage.getString(KEY_OPEN_CAGE, BuildConfig.OPEN_CAGE_KEY)
            viewBalanceRequests?.summary = spCage.getInt(KEY_BALANCE_OF_REQUESTS, NUMBER_REQUESTS_PER_DAY).toString()
            viewResetDate?.summary = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.MEDIUM)
                .format(Date(spCage.getLong(KEY_RESET_DATE, System.currentTimeMillis() + NUMBER_MILLISECONDS_PER_DAY)))
            listPreference?.summary = spRequest.getString(KEY_REQUEST_SELECTION, resources.getStringArray(R.array.request).first())
        }
        setHasOptionsMenu(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.let { (it as MainActivity).supportActionBar?.setTitle(R.string.title_preference) }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.clear()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        when (key) {
            KEY_WEATHER -> textKeyWeather?.let{
                it.takeIf { it.text.isNotBlank() } ?.let {editText ->
                    spWeather.edit{ putString(KEY_WEATHER, editText.text) }
                    editText.summary = editText.text
                }
            }
            KEY_OPEN_CAGE -> textKeyOpenCage?.let{
                it.takeIf { it.text.isNotBlank() } ?.let {editText ->
                    spCage.edit{putString(KEY_OPEN_CAGE, editText.text)}
                    editText.summary = editText.text
                }
            }
            KEY_REQUEST_SELECTION -> listPreference?.let {
                spRequest.edit{putString(KEY_REQUEST_SELECTION, it.value)}
                it.summary = it.value
            }
        }
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }
}