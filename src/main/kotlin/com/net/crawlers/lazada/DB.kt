package com.net.crawlers.lazada

import com.net.ktwebmagic.db.WebMagicDBService
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object ProductInfos : Table() {
    val category1: Column<String> = text("category1")
    val category2: Column<String> = text("category2")
    val category3: Column<String> = text("category3")
    val title: Column<String> = text("title")
    val ratingNum: Column<Int> = integer("ratingNum")
    val QANum: Column<Int> = integer("QANum")
    val price: Column<String> = text("price")
    val SKU: Column<String> = text("SKU")
    val score: Column<String> = text("score")
    val url: Column<String> = text("url")
}

object LazadaDB {
    fun tableInit() {
        WebMagicDBService.createMissingTable(ProductInfos)
    }

    fun dbAddProduct(product: ProductInfo) {
        transaction {
            ProductInfos.insert {
                it[category1] = product.cate1
                it[category2] = product.cate2
                it[category3] = product.cate3
                it[title] = product.title
                it[ratingNum] = product.ratingNum
                it[QANum] = product.QANum
                it[price] = product.price
                it[SKU] = product.SKU
                it[score] = product.score
                it[url] = product.url
            }
        }
    }

    fun remoteAll() {
        transaction {
            ProductInfos.deleteAll()
        }
    }
}

