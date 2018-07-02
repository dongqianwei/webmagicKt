package com.net.ktwebmagic

import com.net.ktwebmagic.dbservice.TargetLinkInfo
import com.net.ktwebmagic.dbservice.WebMagicDBService
import org.apache.logging.log4j.LogManager
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxOptions
import org.openqa.selenium.remote.RemoteWebDriver
import java.util.*
import kotlin.collections.HashMap

object WebMagicSche {

    var driver: RemoteWebDriver? = null
    val targetLinkQueue = LinkedList<TargetLink>()
    // 反序列化注册
    val pageProcCons = HashMap<String, (String) -> IPageProc>()

    val logger = LogManager.getLogger(this.javaClass)

    init {
        System.setProperty("webdriver.gecko.driver", "D:/geckodriver.exe")
        System.setProperty(FirefoxDriver.SystemProperty.DRIVER_USE_MARIONETTE,"true")
        System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE, "selenium.log")
        val options = FirefoxOptions()
        options.setHeadless(true)
        options.addPreference("permissions.default.image", 2)
        options.addPreference("permissions.default.stylesheet", 2)
        this.driver = FirefoxDriver(options)
    }

    fun restart(linksInfo: List<TargetLinkInfo>) {
        for (info in linksInfo) {
            val pageProc = pageProcCons.get(info.clazz)!!.invoke(info.json)
            val targetLink = TargetLink(info.url, pageProc, info.id)
            targetLinkQueue.add(targetLink)
        }
        doTasks()
    }

    fun start(vararg links: TargetLink, needRestart: Boolean = true) {
        logger.info("web magic START...")
        //test if need restart
        if (needRestart) {
            val linksInfo = WebMagicDBService.dbAllTargetLinks()
            if (!linksInfo.isEmpty()) {
                logger.info("last task is not finished, restart task now...")
                restart(linksInfo)
                return
            }
        }
        // 清空
        WebMagicDBService.dbDropTargetLinks()

        for (lk in links) {
            addTargetLink(lk)
        }
        doTasks()
        logger.info("web magic END...")
    }

    private fun doTasks() {
        try {
            while (!targetLinkQueue.isEmpty()) {
                val link = targetLinkQueue.pop()
                driver!!.get(link.url)
                var tryTimes = 0
                while (true) {
                    try {
                        if (tryTimes > 0) {
                            Thread.sleep(5000)
                        }
                        link.pageProc.process(driver!!)
                        break
                    }
                    catch (ex: Exception) {
                        if (tryTimes == 0) {
                            logger.warn("failed to process, try again..")
                            tryTimes ++
                            continue
                        }

                        throw ex
                    }
                }
                WebMagicDBService.dbRemoveTargetLink(link.id)
            }
        }
        catch(ex: Exception) {
            logger.error("url: ${driver!!.currentUrl}", ex)
        }
        finally {
            driver!!.close()
        }
    }

    fun <T: IPageProc>registerJsonCons(jsonBuilder: IJsonBuilder<T>) {
        pageProcCons.put(jsonBuilder.className(), jsonBuilder.jsonCons())
    }

    fun addTargetLink(link: TargetLink) {
        targetLinkQueue.add(link)
        // 数据库持久化
        val id = WebMagicDBService.dbAddTargetLink(link)
        link.id = id
    }
}