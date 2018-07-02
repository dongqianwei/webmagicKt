package com.net.crawlers.lazada

import com.net.ktwebmagic.WebMagicSche
import com.net.ktwebmagic.TargetLink

fun main(args: Array<String>) {
    LazadaDB.tableInit()
    val sche = WebMagicSche
    sche.registerJsonCons(MainPageProcJsonBuilder)
    sche.registerJsonCons(CategoryPageProcJsonBuilder)
    sche.registerJsonCons(ProductPageProcJsonBuilder)
    sche.start(TargetLink("https://www.lazada.com.my/", MainPageProc(if(args.size > 0) args[0] else "Electronic Devices")))
}