package com.github.leondevlifelog.browser.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.github.leondevlifelog.browser.R
import com.github.leondevlifelog.browser.view.AddressBarView
import com.github.leondevlifelog.browser.view.MenuPopupWindow
import com.labo.kaji.relativepopupwindow.RelativePopupWindow
import kotlinx.android.synthetic.main.activity_test.*
import kotlinx.android.synthetic.main.bottpm_navigator_bar.*


class TestActivity : AppCompatActivity() {
    val TAG: String = "TestActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        addressBarView.onActionButtonClickListener = object : AddressBarView.OnActionButtonClickListener {
            override fun onSecurityBtnClick(v: View) {
            }

            override fun onNoneSecurityBtnClick(v: View) {
            }

            override fun onSearchBtnClick(v: View) {
            }

            override fun onInputDone(urlOrKeyWord: String) {
            }

            override fun onScanBtnClick(v: View) {
            }
        }
        var relativePopupWindow = MenuPopupWindow(this)
        actionMenu.setOnClickListener({ v ->
            if (!relativePopupWindow.isShowing)
                relativePopupWindow.showOnAnchor(v, RelativePopupWindow.VerticalPosition.ALIGN_BOTTOM,
                        RelativePopupWindow.HorizontalPosition.ALIGN_LEFT)
            else
                relativePopupWindow.dismiss()
        })
        actionTabs.setOnClickListener({ v ->
            if (!relativePopupWindow.isShowing)
                relativePopupWindow.showOnAnchor(v, RelativePopupWindow.VerticalPosition.ALIGN_BOTTOM,
                        RelativePopupWindow.HorizontalPosition.ALIGN_LEFT)
            else
                relativePopupWindow.dismiss()
        })
    }

}
