package com.github.leondevlifelog.browser.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.github.leondevlifelog.browser.R
import com.github.leondevlifelog.browser.ui.fragments.DownloadingFragment
import kotlinx.android.synthetic.main.activity_down_load.*

class DownLoadActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_down_load)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "下载管理"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        vpDownload.adapter = object : FragmentPagerAdapter(supportFragmentManager) {
            var titles = arrayOf("下载中", "已下载")
            var fragment = arrayOf(DownloadingFragment.newInstance(DownloadingFragment.Downloading),
                    DownloadingFragment.newInstance(DownloadingFragment.DownloadDone))

            override fun getItem(position: Int): Fragment {
                return fragment[position]
            }

            override fun getCount(): Int {
                return titles.size
            }

            override fun getPageTitle(position: Int): CharSequence? {
                return titles[position]
            }
        }
        downloadTabs.setupWithViewPager(vpDownload)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
