package com.net.crawlers.demo

import com.google.gson.Gson
import com.net.ktwebmagic.JsonBuilder
import com.net.ktwebmagic.PageProc
import com.net.ktwebmagic.TargetLink
import com.net.ktwebmagic.WebMagicSche
import org.openqa.selenium.By
import org.openqa.selenium.remote.RemoteWebDriver

object DemoPageProcJsonBuilder : JsonBuilder<DemoPageProc>(DemoPageProc::class.java) {
    override fun jsonCons(): (String) -> DemoPageProc {
        return { DemoPageProc }
    }
}

object DemoPageProc : PageProc() {
    override fun process(driver: RemoteWebDriver) {
        val storyTable = driver.findElementByXPath("//table[@id='hnmain']/tbody/tr[3]//table")
        val titlesA = storyTable.findElements(By.xpath("//a[@class='storylink']"))
        for (a in titlesA) {
            println(a.text)
        }
    }

    override fun toJson(): String {
        return Gson().toJson(null)
    }

}

fun main(args: Array<String>) {
    val sche = WebMagicSche
    sche.registerJsonCons(DemoPageProcJsonBuilder)
    sche.start(TargetLink("https://news.ycombinator.com/", DemoPageProc), true)
}