package com.net.ktwebmagic.common

import com.net.ktwebmagic.Downloader
import com.net.ktwebmagic.PageProc
import org.apache.logging.log4j.LogManager
import java.net.URL
import java.nio.file.Paths


class ImageDownloadProc(val url: String, val path: String) : PageProc() {

    init {
        needBrowser = false
    }

    @Transient
    private val logger = LogManager.getLogger(this.javaClass)

    override fun process() {
        logger.info("downloading img: {}", url)
        Downloader.download(URL(url), Paths.get(path))
    }
}