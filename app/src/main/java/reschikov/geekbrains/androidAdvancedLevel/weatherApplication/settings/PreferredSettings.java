package reschikov.geekbrains.androidadvancedlevel.weatherapplication.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.preference.SwitchPreference;
import androidx.preference.CheckBoxPreference;
import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceFragmentCompat;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.R;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.Rules;

import static androidx.preference.PreferenceManager.getDefaultSharedPreferences;

public class PreferredSettings extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    private CheckBoxPreference checkBoxLang;
    private EditTextPreference textKeyApiUx;
    private SwitchPreference switchGeoCoder;
    private EditTextPreference textKeyOpenCage;

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        setPreferencesFromResource(R.xml.setting, s);
        checkBoxLang = (CheckBoxPreference) findPreference(Rules.KEY_LANG);
        textKeyApiUx = (EditTextPreference) findPreference(Rules.KEY_APIUX);
        switchGeoCoder = (SwitchPreference) findPreference(Rules.KEY_ON_GEOCODER);
        textKeyOpenCage = (EditTextPreference) findPreference(Rules.KEY_OPEN_CAGE);
        if (getContext() != null){
            SharedPreferences sp = getDefaultSharedPreferences(getContext());
            textKeyApiUx.setSummary(sp.getString(Rules.KEY_APIUX, getString(R.string.key_apiux)));
            textKeyOpenCage.setSummary(sp.getString(Rules.KEY_OPEN_CAGE, getString(R.string.key_opencage)));
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        switch (key){
            case Rules.KEY_LANG:
                editor.putBoolean(Rules.KEY_LANG, checkBoxLang.isChecked());
                break;
            case Rules.KEY_APIUX:
                editor.putString(Rules.KEY_APIUX, textKeyApiUx.getText());
                textKeyApiUx.setSummary(textKeyApiUx.getText());
                break;
            case Rules.KEY_ON_GEOCODER:
                editor.putBoolean(Rules.KEY_ON_GEOCODER, switchGeoCoder.isChecked());
                break;
            case Rules.KEY_OPEN_CAGE:
                editor.putString(Rules.KEY_OPEN_CAGE, textKeyOpenCage.getText());
                textKeyOpenCage.setSummary(textKeyOpenCage.getText());
                break;
        }
        editor.apply();
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}
