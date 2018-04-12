package com.github.leondevlifelog.browser.html.homepage

import android.app.Application
import com.anthonycr.mezzanine.MezzanineGenerator
import com.github.leondevlifelog.browser.R
import org.jsoup.Jsoup

/**
 * A builder for the home page.
 */
internal class HomePageBuilder(private val app: Application) {
    fun buildPage(): String {
        val html = MezzanineGenerator.HomePageReader().provideHtml()

        val document = Jsoup.parse(html).apply {
            title(app.getString(R.string.app_name))
            outputSettings().charset("utf-8")
        }


        val body = document.body()

        body.getElementById("image_url").attr("src", "")

        document.getElementsByTag("script").firstOrNull()?.let {
            val newJavaScript = it.html().replace("\${BASE_URL}", "").replace("&", "\\u0026")
            it.html(newJavaScript)
        }

        return document.outerHtml()
    }
}