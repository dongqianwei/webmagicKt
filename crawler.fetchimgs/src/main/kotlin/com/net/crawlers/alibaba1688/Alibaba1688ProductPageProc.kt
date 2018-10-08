package com.net.crawlers.alibaba1688

import com.net.crawlers.ProductImgInfo
import com.net.crawlers.db.ImgDB
import com.net.ktwebmagic.PageProc
import com.net.ktwebmagic.TargetLink
import com.net.ktwebmagic.common.ImageDownloadProc
import com.net.ktwebmagic.waitElementByXpath
import com.net.ktwebmagic.waitElementsByXpath
import org.jetbrains.exposed.sql.transactions.transaction
import org.openqa.selenium.By
import org.openqa.selenium.remote.RemoteWebDriver

class Alibaba1688ProductPageProc(val category: String, val imgPath: String) : PageProc() {
    override fun process(driver: RemoteWebDriver) {
        // get title
        val title = driver.findElementByXPath("//div[@id='mod-detail-title']/h1").text
        transaction {
            ImgDB.dbAddProduct(ProductImgInfo(category, title, driver.currentUrl, imgPath))
        }
        driver.executeScript("window.scrollBy(0,10000)")
        val targetLinks = ArrayList<TargetLink>()
        val UL = driver.waitElementByXpath("//div[@class='tab-content-container']/ul", 10)
        val productIMGs = UL.findElements(By.ByXPath("li/div/a/img"))
        var idx = 0
        for (img in productIMGs) {
            var url = img.getAttribute("src")
            if (url.contains("lazyload.png")) {
                url = img.getAttribute("data-lazy-src")
            }
            url = url.replace("60x60.", "")
            val surfix = url.replace(Regex(".*\\."), "")
            targetLinks.add(TargetLink(url, ImageDownloadProc(url, "D:/imgs/$imgPath/${idx++}.$surfix")))
        }

        idx = 0
        val Ps = driver.waitElementsByXpath("//div[@id='desc-lazyload-container']//p", 10)
        for (P in Ps) {
            val summaryIMGs = P.findElements(By.tagName("img"))
            for (img in summaryIMGs) {
                val url = img.getAttribute("src").replace("60x60.", "")
                val surfix = url.replace(Regex(".*\\."), "")
                targetLinks.add(TargetLink(url, ImageDownloadProc(url, "D:/imgs/$imgPath/summary/${idx++}.$surfix")))
            }
        }

        for (link in targetLinks) {
            addTargetLink(link)
        }
    }


}