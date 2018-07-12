package com.net.ktwebmagic.demo

import com.google.gson.Gson
import com.net.ktwebmagic.*
import org.openqa.selenium.By
import org.openqa.selenium.remote.RemoteWebDriver

object DemoPageProcJsonBuilder : JsonBuilder<DemoPageProc>(DemoPageProc::class.java) {
    override fun jsonCons(): (String) -> DemoPageProc {
        return { DemoPageProc }
    }

    override fun toJson(t: IPageProc): String {
        return Gson().toJson(null)
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

}

fun main(args: Array<String>) {
    val sche = WebMagicSche
    sche.registerJsonBuilder(DemoPageProcJsonBuilder)
    sche.start(TargetLink("https://news.ycombinator.com/", DemoPageProc), true)
}