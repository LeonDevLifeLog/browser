package com.github.leondevlifelog.browser.database

interface WebData {
    fun getWebTitle(): String
    fun getWebUrl(): String
    fun getWebDataType(): WebType
}