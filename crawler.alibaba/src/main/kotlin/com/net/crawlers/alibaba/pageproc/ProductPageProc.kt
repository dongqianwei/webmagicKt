package com.net.crawlers.alibaba.pageproc

import com.net.ktwebmagic.PageProc
import com.net.ktwebmagic.TargetLink
import com.net.ktwebmagic.common.ImageDownloadProc
import com.net.ktwebmagic.waitByXpath
import org.apache.logging.log4j.LogManager
import org.openqa.selenium.By
import org.openqa.selenium.remote.RemoteWebDriver
import java.util.*

object ProductPageProc : PageProc() {

    private val logger = LogManager.getLogger(this.javaClass)

    override fun process(driver: RemoteWebDriver) {
        val ul = driver.waitByXpath("//div[@class='tab-nav-container']/ul", 10)
        val LIs = ul.findElements(By.xpath("li"))
        for (LI in LIs) {
            val url = LI.findElement(By.xpath("div/a/img")).getAttribute("src")
            addTargetLink(TargetLink(url, ImageDownloadProc(url, "D:/" + UUID.randomUUID() + ".jpg")))
        }
    }
}