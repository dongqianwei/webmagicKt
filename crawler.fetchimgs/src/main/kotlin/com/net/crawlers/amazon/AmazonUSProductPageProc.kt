package com.net.crawlers.amazon

import com.net.ktwebmagic.PageProc
import org.openqa.selenium.remote.RemoteWebDriver

class AmazonUSProductPageProc(val category: String, val imgPath: String) : PageProc() {
    override fun process(driver: RemoteWebDriver) {
        println("TODO for amazonUS img download")
    }

}