package com.net.ktwebmagic.demo

import com.net.ktwebmagic.PageProc
import com.net.ktwebmagic.TargetLink
import com.net.ktwebmagic.WebMagicSche
import org.openqa.selenium.By
import org.openqa.selenium.remote.RemoteWebDriver


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
    sche.start(TargetLink("https://news.ycombinator.com/", DemoPageProc), forceRestart = true)
}