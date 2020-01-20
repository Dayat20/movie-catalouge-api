package com.example.moviecatalougeapi.ui.setting


import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.example.moviecatalougeapi.R
import com.example.moviecatalougeapi.util.notification.AlarmReceiver
import java.text.SimpleDateFormat
import java.util.*


class PreferenceFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var LANGUAGE: String
    private lateinit var RELEASE: String
    private lateinit var REMINDER: String

    private lateinit var languagePreference: Preference
    private lateinit var releasePreference: SwitchPreference
    private lateinit var reminderPreference: SwitchPreference

    private lateinit var alarmReceiver: AlarmReceiver

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)

        alarmReceiver = AlarmReceiver()

        init()
        setSummary()
    }

    private fun init() {
        LANGUAGE = resources.getString(R.string.key_language)
        RELEASE = resources.getString(R.string.key_release)
        REMINDER = resources.getString(R.string.key_reminder)

        languagePreference = findPreference<Preference>(LANGUAGE) as Preference
        releasePreference = findPreference<SwitchPreference>(RELEASE) as SwitchPreference
        reminderPreference = findPreference<SwitchPreference>(REMINDER) as SwitchPreference
    }


    private fun setSummary() {
        languagePreference.summary = Locale.getDefault().displayLanguage

        languagePreference.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            true
        }

        val sh = preferenceManager.sharedPreferences
        releasePreference.isChecked = sh.getBoolean(RELEASE, false)
        reminderPreference.isChecked = sh.getBoolean(REMINDER, false)
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(p0: SharedPreferences, p1: String) {
        if (p1 == RELEASE) {
            releasePreference.isChecked = p0.getBoolean(RELEASE, false)
            if (p0.getBoolean(RELEASE, false)) {
                alarmReceiver.setRepeatingAlarm(this.requireContext(), AlarmReceiver.TYPE_RELEASE, "08:00")
            }else {
                alarmReceiver.cancelAlarm(this.requireContext(), AlarmReceiver.TYPE_RELEASE)
            }
        }

        if (p1 == REMINDER) {
            reminderPreference.isChecked = p0.getBoolean(REMINDER, false)
            if (p0.getBoolean(REMINDER, false)) {
                alarmReceiver.setRepeatingAlarm(this.requireContext(), AlarmReceiver.TYPE_REMINDER, "07:00")
            }else {
                alarmReceiver.cancelAlarm(this.requireContext(), AlarmReceiver.TYPE_REMINDER)
            }
        }
    }
}
