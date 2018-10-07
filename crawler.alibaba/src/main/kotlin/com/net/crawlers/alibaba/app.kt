package com.net.crawlers.alibaba

import com.net.crawlers.alibaba.pageproc.ProductPageProc
import com.net.ktwebmagic.TargetLink
import com.net.ktwebmagic.WebMagicSche


fun main(args: Array<String>) {
    val sche = WebMagicSche
    sche.start(TargetLink("https://detail.1688.com/pic/571081632869.html", ProductPageProc), true)
}