package com.net.crawlers

import au.com.bytecode.opencsv.CSVReader
import com.net.crawlers.alibaba1688.Alibaba1688ProductPageProc
import com.net.crawlers.amazon.AmazonUSProductPageProc
import com.net.crawlers.db.ImgDB
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
        val imgPath = UUID.randomUUID().toString()
        var pageProc: PageProc = if (url.contains("1688")) {
            Alibaba1688ProductPageProc(category, imgPath)
        } else if (url.contains("www.amazon.com")) {
            AmazonUSProductPageProc(category, imgPath)
        } else {
            throw IllegalArgumentException("unknown link type: ${url}")
        }

        targetLinks.add(TargetLink(url, pageProc))
    }

    ImgDB.tableInit()
    WebMagicSche.start(*targetLinks.toTypedArray(), forceRestart = true)
}