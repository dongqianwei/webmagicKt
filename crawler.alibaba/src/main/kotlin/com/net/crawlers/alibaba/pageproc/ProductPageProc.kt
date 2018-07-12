package com.net.crawlers.alibaba.pageproc

import com.net.ktwebmagic.IPageProc
import com.net.ktwebmagic.JsonBuilder
import com.net.ktwebmagic.PageProc
import com.net.ktwebmagic.TargetLink
import org.openqa.selenium.By
import org.openqa.selenium.remote.RemoteWebDriver

object ProductPageProcJsonBuilder : JsonBuilder<ProductPageProc>(ProductPageProc::class.java) {
    override fun jsonCons(): (String) -> IPageProc {
        return { ProductPageProc }
    }
}

object ProductPageProc : PageProc() {
    override fun process(driver: RemoteWebDriver) {
        val div = driver.findElementByXPath("//div[@class='tab-nav-container']")
        val imgs = div.findElements(By.xpath("//img"))
        for (img in imgs) {
            addTargetLink(TargetLink(img.getAttribute("src"), ImageDownloadProc))
        }
    }
}