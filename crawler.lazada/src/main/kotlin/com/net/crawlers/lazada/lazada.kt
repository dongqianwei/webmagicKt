package com.net.crawlers.lazada

import com.net.ktwebmagic.TargetLink
import com.net.ktwebmagic.WebMagicSche

fun main(args: Array<String>) {
    LazadaDB.tableInit()
    val sche = WebMagicSche
    sche.registerJsonBuilder(MainPageProcJsonBuilder)
    sche.registerJsonBuilder(CategoryPageProcJsonBuilder)
    sche.registerJsonBuilder(ProductPageProcJsonBuilder)
    sche.start(TargetLink("https://www.lazada.com.my/", MainPageProc(if(args.size > 0) args[0] else "Electronic Devices")))
}