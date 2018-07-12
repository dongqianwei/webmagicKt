package com.net.ktwebmagic

import com.google.gson.Gson

interface IJsonBuilder {
    fun jsonCons(): (String) -> IPageProc

    fun className(): String

    fun toJson(t: IPageProc): String
}

abstract class JsonBuilder<T : IPageProc>(val clazz: Class<T>) : IJsonBuilder {

    val gson = Gson()

    override fun className() = clazz.name

    override fun toJson(t: IPageProc): String {
        return gson.toJson(null)
    }
}

object PageProcJsonBuilder {

    // 反序列化注册
    private val pageProcJsonBuilders = HashMap<String, IJsonBuilder>()

    fun registerJsonBuilder(jsonBuilder: IJsonBuilder) {
        pageProcJsonBuilders.put(jsonBuilder.className(), jsonBuilder)
    }

    fun fromJson(clazzName: String, json: String): IPageProc {
        return pageProcJsonBuilders[clazzName]!!.jsonCons().invoke(json)
    }

    fun toJson(pageProc: IPageProc): String {
        return pageProcJsonBuilders[pageProc.javaClass.name]!!.toJson(pageProc)
    }
}