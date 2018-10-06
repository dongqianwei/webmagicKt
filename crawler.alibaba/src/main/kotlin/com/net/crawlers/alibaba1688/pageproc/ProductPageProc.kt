package com.net.crawlers.alibaba1688.pageproc

import com.net.ktwebmagic.PageProc
import com.net.ktwebmagic.TargetLink
import com.net.ktwebmagic.common.ImageDownloadProc
import org.apache.logging.log4j.LogManager
import org.openqa.selenium.By
import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait

object ProductPageProc : PageProc() {

    private val logger = LogManager.getLogger(this.javaClass)

    override fun process(driver: RemoteWebDriver) {
        val wait = WebDriverWait(driver, 10)
        val byXPath = By.xpath("//div[@class='tab-nav-container']/ul")
        val ul = wait.until(ExpectedConditions.presenceOfElementLocated(byXPath))
        val LIs = ul.findElements(By.xpath("li"))
        for (LI in LIs) {
            val url = LI.findElement(By.xpath("div/a/img")).getAttribute("src")
            addTargetLink(TargetLink(url, ImageDownloadProc(url)))
        }
    }
}