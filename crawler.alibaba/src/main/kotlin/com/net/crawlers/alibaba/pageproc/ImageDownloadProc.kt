package com.net.crawlers.alibaba.pageproc

import com.net.ktwebmagic.Downloader
import com.net.ktwebmagic.PageProc
import org.apache.logging.log4j.LogManager
import org.openqa.selenium.remote.RemoteWebDriver
import java.net.URL
import java.nio.file.Paths


class ImageDownloadProc(val url: String) : PageProc() {

    init {
        needBrowser = false
    }

    @Transient
    private val logger = LogManager.getLogger(this.javaClass)

    override fun process(driver: RemoteWebDriver) {
        logger.info("download img: {}", url)
        Downloader.download(URL(url), Paths.get("D:/imgs").resolve(URL(url).file))
    }
}