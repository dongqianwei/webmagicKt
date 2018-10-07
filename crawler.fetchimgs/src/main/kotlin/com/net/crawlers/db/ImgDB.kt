package com.net.crawlers.db

import com.net.crawlers.ProductImgInfo
import com.net.ktwebmagic.dbservice.WebMagicDBService
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

object ProductInfoTable : Table() {
    val category: Column<String> = text("category")
    val title: Column<String> = text("title")
    val url: Column<String> = text("url")
    val imgLocation: Column<String> = text("imgLocation")
    //val price: Column<String> = text("price")
    //val description: Column<String> = text("description")
    //val color: Column<String> = text("color")
    //val size: Column<String> = text("size")
}

object ImgDB {
    fun tableInit() {
        WebMagicDBService.dbInit()
        transaction {
            WebMagicDBService.dbCreateMissingTable(ProductInfoTable)
        }
    }

    fun dbAddProduct(product: ProductImgInfo) {
        transaction {
            ProductInfoTable.insert {
                it[category] = product.category
                it[title] = product.title
                it[imgLocation] = product.imgLocation
                it[url] = product.url
            }
        }
    }

    fun remoteAll() {
        transaction {
            ProductInfoTable.deleteAll()
        }
    }
}
