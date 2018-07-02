package com.net.crawlers.lazada

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.net.ktwebmagic.IJsonBuilder
import com.net.ktwebmagic.IPageProc
import com.net.ktwebmagic.PageProc
import com.net.ktwebmagic.TargetLink
import org.apache.logging.log4j.LogManager
import org.openqa.selenium.By
import org.openqa.selenium.remote.RemoteWebDriver

private val logger = LogManager.getLogger(CategoryPageProc::class.java)

object CategoryPageProcJsonBuilder: IJsonBuilder<CategoryPageProc> {
    override fun jsonCons(): (String) -> CategoryPageProc {
        return {
            val gson = Gson()
            val listType = object : TypeToken<List<String>>() {}.type
            val categories = gson.fromJson<List<String>>(it, listType)
            CategoryPageProc(categories)
        }
    }

    override fun className() = CategoryPageProc::class.java.name

}

class CategoryPageProc(val categories: List<String>): PageProc() {

    override fun toJson(): String {
        return gson.toJson(categories)
    }

    override fun process(driver: RemoteWebDriver) {
        logger.info("process categories: $categories")
        val productDIVs = driver.findElementByXPath("//div[@data-qa-locator='general-products']")
                .findElements(By.xpath("div"))
        for (productDIV in productDIVs.subList(0, 10)) {
            val productURL = productDIV.findElement(By.xpath("div//a")).getAttribute("href")
            addTargetLink(TargetLink(productURL, ProductPageProc(categories)))
        }
    }
}