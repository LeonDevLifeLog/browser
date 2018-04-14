package com.github.leondevlifelog.browser.ui

import android.os.Bundle
import com.github.leondevlifelog.browser.R

class SettingsActivity : AppCompatPreferenceActivity() {
    override fun onBuildHeaders(target: MutableList<Header>?) {
        super.onBuildHeaders(target)
        loadHeadersFromResource(R.xml.pref_headers, target)
    }

    override fun isValidFragment(fragmentName: String?): Boolean {
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}
