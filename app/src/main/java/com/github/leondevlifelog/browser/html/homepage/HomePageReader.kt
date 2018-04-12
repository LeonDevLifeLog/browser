package com.github.leondevlifelog.browser.html.homepage

import com.anthonycr.mezzanine.FileStream

/**
 * The store for the homepage HTML.
 */
@FileStream("app/src/main/assets/homepage.html")
interface HomePageReader {

    fun provideHtml(): String

}