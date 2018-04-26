package com.github.leondevlifelog.browser.ui

import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.LinearLayout
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
        var appbarLayout = layoutInflater.inflate(R.layout.toolbar, null)
        var linearLayout = findViewById<ViewGroup>(android.R.id.content).getChildAt(0) as LinearLayout
        linearLayout.addView(appbarLayout, 0)
        setSupportActionBar(appbarLayout.findViewById(R.id.toolbar))
        appbarLayout.findViewById<Toolbar>(R.id.toolbar).elevation = 0f
        appbarLayout.elevation = 0f
        supportActionBar?.elevation = 0f
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
