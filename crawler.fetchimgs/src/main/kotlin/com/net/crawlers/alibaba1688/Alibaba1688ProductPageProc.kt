package com.net.crawlers.alibaba1688

import com.net.ktwebmagic.PageProc
import com.net.ktwebmagic.TargetLink
import com.net.ktwebmagic.common.ImageDownloadProc
import com.net.ktwebmagic.waitByXpath
import org.openqa.selenium.By
import org.openqa.selenium.remote.RemoteWebDriver

class Alibaba1688ProductPageProc(val category: String, val imgPath: String) : PageProc() {
    override fun process(driver: RemoteWebDriver) {
        driver.executeScript("window.scrollBy(0,10000)")
        // repeat push next A if it exists
/*        val nextA = driver.findElementsByXPath("//div[@id='dt-tab']/a[2]")
        if (nextA.size == 1) {
            while (!nextA[0].getAttribute("class").contains("next-g")) {
                Thread.sleep(500)
                nextA[0].sendKeys(Keys.ENTER)
            }
        }*/

        val UL = driver.findElementByXPath("//div[@class='tab-content-container']/ul")
        val productIMGs = UL.findElements(By.ByXPath("li/div/a/img"))
        var idx = 0
        for (img in productIMGs) {
            var url = img.getAttribute("src")
            if (url.contains("lazyload.png")) {
                url = img.getAttribute("data-lazy-src")
            }
            url = url.replace("60x60.", "")
            val surfix = url.replace(Regex(".*\\."), "")
            addTargetLink(TargetLink(url, ImageDownloadProc(url, "D:/imgs/$imgPath/${idx++}.$surfix")))
        }

        val P = driver.waitByXpath("//div[@id='desc-lazyload-container']/p[1]", 10)
        val summaryIMGs = P.findElements(By.tagName("img"))
        idx = 0
        for (img in summaryIMGs) {
            val url = img.getAttribute("src").replace("60x60.", "")
            val surfix = url.replace(Regex(".*\\."), "")
            addTargetLink(TargetLink(url, ImageDownloadProc(url, "D:/imgs/$imgPath/summary/${idx++}.$surfix")))
        }
    }


}