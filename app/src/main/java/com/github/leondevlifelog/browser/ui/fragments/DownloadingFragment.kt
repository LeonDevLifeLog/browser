package com.github.leondevlifelog.browser.ui.fragments


import android.os.Bundle
import android.support.annotation.IntDef
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.androidev.download.DownloadInfo
import com.androidev.download.DownloadJobListener
import com.androidev.download.DownloadManager
import com.androidev.download.DownloadTask
import com.github.leondevlifelog.browser.R

private const val KEY_TYPE = "KEY_TYPE"


class DownloadingFragment : Fragment() {
    private var type: Int? = null

    @IntDef(DownloadDone, Downloading)
    @Retention(AnnotationRetention.SOURCE)
    annotation class FragmentType


    private var manager: DownloadManager? = null
    private var tasks: MutableList<DownloadTask>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            type = it.getInt(KEY_TYPE)
        }
        manager = DownloadManager.getInstance()
        manager?.addDownloadJobListener(object : DownloadJobListener {
            override fun onStarted(info: DownloadInfo?) {
                tasks?.add(0, manager?.createTask(info, null)!!)
//                adapter.notifyItemInserted(0)
            }

            override fun onCreated(info: DownloadInfo?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onCompleted(finished: Boolean, info: DownloadInfo?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
        tasks = manager?.allTasks
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_downloading, container, false)
    }


    companion object {
        const val Downloading = 0
        const val DownloadDone = 1
        @JvmStatic
        fun newInstance(@FragmentType fragmentType: Int) =
                DownloadingFragment().apply {
                    arguments = Bundle().apply {
                        putInt(KEY_TYPE, fragmentType)
                    }
                }
    }
}
