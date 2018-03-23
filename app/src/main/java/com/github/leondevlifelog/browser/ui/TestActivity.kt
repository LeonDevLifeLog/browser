package com.github.leondevlifelog.browser.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.github.leondevlifelog.browser.R
import com.github.leondevlifelog.browser.view.AddressBarView
import com.qmuiteam.qmui.widget.popup.QMUIPopup
import kotlinx.android.synthetic.main.activity_test.*


class TestActivity : AppCompatActivity() {
    var mNormalPopup: QMUIPopup? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        initNormalPopupIfNeed()
        addressBarView.onActionButtonClickListener = object : AddressBarView.OnActionButtonClickListener {
            override fun onSecurityBtnClick(v: View) {
                mNormalPopup?.setAnimStyle(QMUIPopup.ANIM_GROW_FROM_LEFT)
                mNormalPopup?.setPreferredDirection(QMUIPopup.DIRECTION_BOTTOM)
                mNormalPopup?.setPopupLeftRightMinMargin(0)
                mNormalPopup?.show(v)
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
    }

    private fun initNormalPopupIfNeed() {
        if (mNormalPopup == null) {
            mNormalPopup = QMUIPopup(this, QMUIPopup.DIRECTION_NONE)
            mNormalPopup?.setContentView(R.layout.tip_layout)
        }
    }
}
