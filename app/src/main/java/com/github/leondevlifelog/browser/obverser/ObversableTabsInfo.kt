package com.github.leondevlifelog.browser.obverser

import com.github.leondevlifelog.browser.bean.TabInfo
import java.util.*

class ObversableTabsInfo : Observable {

    var tabs: MutableList<TabInfo> = MutableList(1, { TabInfo() })
    var selectedTab: TabInfo? = tabs[0]
        set(value) {
            field = value
            setChanged()
            notifyObservers(selectedTab)
        }


    constructor() : super()

    fun add(x: TabInfo): Unit {
        if (tabs.size > 9)
            return
        tabs.add(x)
        if (tabs.size == 1) {
            selectedTab = x
        }
        setChanged()
        notifyObservers(tabs.size)
    }

    fun remove(x: TabInfo) {
        tabs.remove(x)
        if (tabs.size == 0) {
            var element = TabInfo()
            selectedTab = element
            tabs.add(element)
        }
        setChanged()
        notifyObservers(tabs.size)
    }

    fun size(): Int {
        return tabs.size
    }

    fun get(postion: Int): TabInfo {
        return tabs.get(postion)
    }

    fun clear(): Unit {
        tabs.clear()
        tabs.add(TabInfo())
        selectedTab = tabs[0]
        setChanged()
        notifyObservers(tabs.size)
    }

}