package com.github.leondevlifelog.browser.ui.fragments


import android.os.Bundle
import android.support.v14.preference.PreferenceFragment

import com.github.leondevlifelog.browser.R


class SettingsFragment : PreferenceFragment() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

        addPreferencesFromResource(R.xml.pref_headers)
    }
}
