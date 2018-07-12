package com.net.ktwebmagic

import com.net.ktwebmagic.dbservice.TargetLinkInfo
import com.net.ktwebmagic.dbservice.WebMagicDBService
import org.apache.logging.log4j.LogManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxOptions
import org.openqa.selenium.remote.RemoteWebDriver
import java.util.*
import java.util.concurrent.TimeUnit

object WebMagicSche {

    var driver: RemoteWebDriver? = null
    val targetLinkQueue = LinkedList<TargetLink>()

    val logger = LogManager.getLogger(this.javaClass)

    init {
        System.setProperty("webdriver.gecko.driver", "D:/geckodriver.exe")
        System.setProperty(FirefoxDriver.SystemProperty.DRIVER_USE_MARIONETTE, "true")
        System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE, "selenium.log")
        val options = FirefoxOptions()
        /*options.setHeadless(true)
        options.addPreference("permissions.default.image", 2)
        options.addPreference("permissions.default.stylesheet", 2)*/
        this.driver = FirefoxDriver(options)
        this.driver!!.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS)
        WebMagicDBService.dbInit()
    }

    fun restart(linksInfo: List<TargetLinkInfo>) {
        for (info in linksInfo) {
            val pageProc = PageProcJsonBuilder.fromJson(info.clazz, info.json)
            val targetLink = TargetLink(info.url, pageProc, info.id)
            targetLinkQueue.add(targetLink)
        }
        doTasks()
    }

    fun start(startLink: TargetLink, forceRestart: Boolean = false) {
        if (forceRestart) {
            logger.info("FORCE RESTART, data may be duplicated..")
            // 清空
            transaction {
                WebMagicDBService.dbDropTargetLinks()
                WebMagicDBService.dbSetTaskFinished(false)
            }
        }

        // check if last task is already finished
        if (WebMagicDBService.isTaskFinished()) {
            logger.info("Task already finished, if you want a new task, please remove the DB file first...")
            return
        }

        logger.info("web magic START...")
        //check if need continue
        val linksInfo = transaction {
            WebMagicDBService.dbAllTargetLinks()
        }
        if (!linksInfo.isEmpty()) {
            logger.info("last task is not finished, restart task now...")
            restart(linksInfo)
            return
        }

        addTargetLink(startLink)
        doTasks()
        logger.info("web magic END...")
    }

    private fun doTasks() {
        try {
            while (!targetLinkQueue.isEmpty()) {
                val link = targetLinkQueue.pop()
                var tryTimes = 0
                while (true) {
                    try {
                        logger.info("get url: ${link.url}")
                        driver!!.get(link.url)
                        if (tryTimes > 0) {
                            Thread.sleep(5000)
                        }
                        link.pageProc.process(driver!!)
                        break
                    } catch (ex: Exception) {
                        if (tryTimes == 0) {
                            logger.warn("failed to process, try again..")
                            tryTimes++
                            continue
                        }

                        throw ex
                    }
                }
                transaction {
                    WebMagicDBService.dbRemoveTargetLink(link.id)
                    // 队列为空，设置任务状态为完成
                    if (targetLinkQueue.isEmpty()) {
                        WebMagicDBService.dbSetTaskFinished(true)
                    }
                }
            }
        } catch (ex: Exception) {
            logger.error("url: ${driver!!.currentUrl}", ex)
        } finally {
            driver!!.close()
        }
    }


    fun addTargetLink(link: TargetLink) {
        targetLinkQueue.add(link)
        // 数据库持久化
        val id = transaction {
            WebMagicDBService.dbAddTargetLink(link)
        }
        link.id = id
    }

    fun registerJsonBuilder(builder: IJsonBuilder) {
        PageProcJsonBuilder.registerJsonBuilder(builder)
    }
}