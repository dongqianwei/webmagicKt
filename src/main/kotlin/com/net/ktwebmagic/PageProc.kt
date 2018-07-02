package com.net.ktwebmagic

import com.google.gson.Gson
import org.openqa.selenium.remote.RemoteWebDriver

data class TargetLink(val url: String, val pageProc: IPageProc, var id: Int = 0)

interface IPageProc {
    fun process(driver: RemoteWebDriver)
    // parameter to json
    fun toJson(): String
}

interface IJsonBuilder<T: IPageProc> {
    fun jsonCons(): (String) -> T

    fun className(): String
}

abstract class PageProc: IPageProc {

    val gson = Gson()

    fun addTargetLink(link: TargetLink) {
        WebMagicSche.addTargetLink(link)
    }
}