package com.net.ktwebmagic

import org.openqa.selenium.remote.RemoteWebDriver

data class TargetLink(val url: String, val pageProc: IPageProc, var id: Int = 0)

interface IPageProc {
    fun process(driver: RemoteWebDriver)

    var needBrowser: Boolean
}


abstract class PageProc: IPageProc {

    fun addTargetLink(link: TargetLink) {
        WebMagicSche.addTargetLink(link)
    }

    final override var needBrowser: Boolean = true
}