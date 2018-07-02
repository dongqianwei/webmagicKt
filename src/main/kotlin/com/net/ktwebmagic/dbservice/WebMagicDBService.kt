package com.net.ktwebmagic.dbservice

import com.net.ktwebmagic.TargetLink
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SchemaUtils.createMissingTablesAndColumns
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection


object TargetLinks : IntIdTable() {
    val url: Column<String> = text("url")
    val clazz: Column<String> = text("clazz")
    val json: Column<String> = text("json")
}

object Properties : Table() {
    val key: Column<String> = text("key").primaryKey()
    val value: Column<String> = text("value")
}

data class TargetLinkInfo(val url: String, val id: Int, val clazz: String, val json: String)


object WebMagicDBService {

    init {
        Database.connect("jdbc:sqlite:sche.dbservice", driver = "org.sqlite.JDBC")
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
        createMissingTable(TargetLinks)
        createMissingTable(Properties)
    }

    fun createMissingTable(table: Table) {
        transaction {
            createMissingTablesAndColumns(table)
        }
    }

    fun dbAddTargetLink(link: TargetLink): Int {
        return transaction {
            TargetLinks.insertAndGetId {
                it[url] = link.url
                it[clazz] = link.pageProc.javaClass.name
                it[json] = link.pageProc.toJson()
            }.value
        }
    }

    fun dbRemoveTargetLink(id: Int) {
        transaction {
            TargetLinks.deleteWhere { TargetLinks.id eq id }
        }
    }


    fun dbAllTargetLinks(): List<TargetLinkInfo> {
        return transaction {
            TargetLinks.selectAll().map {
                TargetLinkInfo(it[TargetLinks.url], it[TargetLinks.id].value, it[TargetLinks.clazz], it[TargetLinks.json])
            }
        }
    }

    fun dbDropTargetLinks() {
        transaction {
            TargetLinks.deleteAll()
        }
    }
}