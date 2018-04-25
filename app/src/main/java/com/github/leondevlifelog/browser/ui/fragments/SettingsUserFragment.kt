package com.github.leondevlifelog.browser.ui.fragments


import android.os.Bundle
import android.preference.PreferenceFragment
import com.github.leondevlifelog.browser.R

class SettingsUserFragment : PreferenceFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.pref_user)
    }

}
