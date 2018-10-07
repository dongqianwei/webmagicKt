package com.net.crawlers.lazada

import com.net.ktwebmagic.PageProc
import org.apache.logging.log4j.LogManager
import org.openqa.selenium.By
import org.openqa.selenium.remote.RemoteWebDriver

private val logger = LogManager.getLogger(LazadaProductPageProc::class.java)

data class ProductInfo(val cate1: String,val cate2: String,val cate3: String, val title: String, val ratingNum: Int, val QANum: Int, val price: String, val SKU: String, val score: String, val url: String)


class LazadaProductPageProc(val categorys: List<String>) : PageProc() {
    override fun process(driver: RemoteWebDriver) {
        // test if product unavailable
        val errorInfoDIVs = driver.findElementsByXPath("//div[@class='error-info']")
        if (errorInfoDIVs.size > 0) {
            val errorText = errorInfoDIVs[0].findElement(By.xpath("h3")).text
            logger.warn("${driver.currentUrl}: ERROR INFO: $errorText")
            return
        }
        val title = driver.findElementByXPath("//h1[@class='pdp-product-title']").text
        val ratingAndAQ_As = driver.findElementsByXPath("//div[@class='pdp-review-summary']//a")
        var ratingNum = 0
        var QANum = 0
        if (ratingAndAQ_As.size > 0) {
            val ratingText = ratingAndAQ_As[0].text
            if (ratingText != "No Ratings") {
                ratingNum = ratingText.split(" ")[0].toInt()
            }
        }
        if (ratingAndAQ_As.size > 1) {
            QANum = ratingAndAQ_As[1].text.split(" ")[0].toInt()
        }

        var SKU = ""
        val price = driver.findElementByXPath("//div[@class='pdp-product-price']/span").text.substring(2)
        val specKeyLIs = driver.findElementsByXPath("//ul[@class='specification-keys']/li")
        for (specKeyLI in specKeyLIs) {
            val key = specKeyLI.findElement(By.xpath("span")).text
            if (key == "SKU") {
                SKU = specKeyLI.findElement(By.tagName("div")).text
            }
        }
        val score = driver.findElementByXPath("//div[@class='score']/span").text
        val productInfo = ProductInfo(categorys[0], categorys[1], categorys[2],
                title, ratingNum, QANum, price, SKU, score, driver.currentUrl)
        println("product summary: $productInfo")
        LazadaDB.dbAddProduct(productInfo)
    }

}