package com.github.leondevlifelog.browser.ui.fragments


import android.os.Bundle
import android.preference.PreferenceFragment
import android.support.v4.app.NavUtils
import android.view.MenuItem
import com.github.leondevlifelog.browser.R

class UserSettingsFragment : PreferenceFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.pref_data_sync)
    }

}
