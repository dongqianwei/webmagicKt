package com.net.crawlers.alibaba.pageproc

import com.net.ktwebmagic.PageProc
import org.openqa.selenium.remote.RemoteWebDriver


object ImageDownloadProc : PageProc() {
    override fun process(driver: RemoteWebDriver) {
        println(driver.currentUrl)
    }
}