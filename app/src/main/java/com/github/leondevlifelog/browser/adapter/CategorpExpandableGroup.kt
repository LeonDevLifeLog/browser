package com.github.leondevlifelog.browser.adapter

import com.github.leondevlifelog.browser.database.entities.BookMark
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup

class CategorpExpandableGroup(title: String?, items: MutableList<BookMark>?) : ExpandableGroup<BookMark>(title, items) {
}