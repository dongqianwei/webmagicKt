package com.net.ktwebmagic.dbservice

import com.google.gson.Gson
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

    val gson = Gson()

    init {
        Database.connect("jdbc:sqlite:sche.db", driver = "org.sqlite.JDBC")
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
        transaction {
            dbCreateMissingTable(TargetLinks)
            dbCreateMissingTable(Properties)
        }
    }

    fun dbInit() {
        // do nothing, just make sure init block is executed
    }

    fun dbCreateMissingTable(table: Table) {
        createMissingTablesAndColumns(table)
    }

    fun dbAddTargetLink(link: TargetLink): Int {
        return TargetLinks.insertAndGetId {
            it[url] = link.url
            it[clazz] = link.pageProc.javaClass.name
            it[json] = gson.toJson(link.pageProc)
        }.value
    }

    fun dbRemoveTargetLink(id: Int) {
        TargetLinks.deleteWhere { TargetLinks.id eq id }
    }


    fun dbAllTargetLinks(): List<TargetLinkInfo> {
        return TargetLinks.selectAll().map {
            TargetLinkInfo(it[TargetLinks.url], it[TargetLinks.id].value, it[TargetLinks.clazz], it[TargetLinks.json])
        }
    }

    fun dbDropTargetLinks() {
        TargetLinks.deleteAll()
    }

    private val IS_TASK_FINISHED = "is.task.finished"

    fun isTaskFinished(): Boolean {
        return transaction {
            val query = Properties.select { Properties.key eq IS_TASK_FINISHED }
            if (query.empty()) {
                false
            } else {
                query.first()[Properties.value] == "true"
            }
        }
    }

    fun dbSetTaskFinished(status: Boolean) {
        Properties.deleteWhere { Properties.key eq IS_TASK_FINISHED }
        Properties.insert {
            it[key] = IS_TASK_FINISHED
            it[value] = if (status) "true" else "false"
        }
    }
}
