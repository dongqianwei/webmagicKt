package com.net.crawlers.lazada

import com.google.gson.Gson
import com.net.ktwebmagic.JsonBuilder
import com.net.ktwebmagic.PageProc
import com.net.ktwebmagic.TargetLink
import org.openqa.selenium.By
import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait

object MainPageProcJsonBuilder : JsonBuilder<MainPageProc>(MainPageProc::class.java) {

    override fun jsonCons(): (String) -> MainPageProc {
        return {
            val gson = Gson()
            val startCategory = gson.fromJson(it, String::class.java)
            MainPageProc(startCategory)
        }
    }
}

class MainPageProc(val startCategory: String): PageProc() {

    override fun toJson(): String {
        return gson.toJson(startCategory)
    }

    override fun process(driver: RemoteWebDriver) {
        val wait = WebDriverWait(driver, 10)
        val byXPath = By.ByXPath("//ul[@class='lzd-site-menu-root']")
        val ul = wait.until(ExpectedConditions.presenceOfElementLocated(byXPath))
        val level1Titles = ul.findElements(By.ByXPath("li//span"))
        val subULs = ul.findElements(By.ByXPath("ul"))
        // iterator level2 titles
        var idx = 0
        for (subUL in subULs) {
            val level1Title = level1Titles[idx].text
            idx ++
            if (level1Title != startCategory) {
                continue
            }
            val level2LIs = subUL.findElements(By.ByXPath("li"))
            for (level2LI in level2LIs) {
                val level2Title = level2LI.findElement(By.xpath("a/span")).text
                val only2Levels = level2LI.findElements(By.xpath("ul")).isEmpty()
                if (only2Levels) {
                    val url = level2LI.findElement(By.xpath("a")).getAttribute("href")
                    addTargetLink(TargetLink(url, CategoryPageProc(listOf(level1Title, level2Title, ""))))
                }
                else {
                    val level2UL = level2LI.findElement(By.xpath("ul"))
                    val level3LIs = level2UL.findElements(By.xpath("li"))
                    for (level3LI in level3LIs) {
                        val level3Title = level3LI.findElement(By.xpath("a/span")).text
                        val url = level3LI.findElement(By.xpath("a")).getAttribute("href")
                        addTargetLink(TargetLink(url, CategoryPageProc(listOf(level1Title, level2Title, level3Title))))
                    }
                }
            }
            break
        }
    }
}