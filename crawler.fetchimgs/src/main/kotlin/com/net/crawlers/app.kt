package com.net.crawlers

import au.com.bytecode.opencsv.CSVReader
import com.net.crawlers.alibaba1688.ProductPageProc1688
import com.net.crawlers.lazada.ProductPageProcLazada
import com.net.ktwebmagic.PageProc
import com.net.ktwebmagic.TargetLink
import com.net.ktwebmagic.WebMagicSche
import java.io.FileReader
import java.util.*

fun main(args: Array<String>) {
    val csvReader = CSVReader(FileReader("input.csv"))
    val inputList = csvReader.readAll()
    val targetLinks = ArrayList<TargetLink>()
    for (input in inputList) {
        val category = input[0]
        val url = input[1]
        var pageProc: PageProc = if (url.contains("1688")) {
            ProductPageProc1688(category)
        } else if (url.contains("lazada")) {
            ProductPageProcLazada(category)
        } else {
            throw IllegalArgumentException("unknown link type: ${url}")
        }

        targetLinks.add(TargetLink(url, pageProc))
    }

    WebMagicSche.start(*targetLinks.toTypedArray(), forceRestart = true)
}