package com.net.crawlers.alibaba.pageproc

import com.net.ktwebmagic.IPageProc
import com.net.ktwebmagic.JsonBuilder
import com.net.ktwebmagic.PageProc
import org.openqa.selenium.remote.RemoteWebDriver

object ImageDownloadProcJsonBuilder : JsonBuilder<ImageDownloadProc>(ImageDownloadProc::class.java) {
    override fun jsonCons(): (String) -> IPageProc {
        return { ImageDownloadProc }
    }

}

object ImageDownloadProc : PageProc() {
    override fun process(driver: RemoteWebDriver) {
        println(driver.currentUrl)
    }
}