package com.net.ktwebmagic

import com.google.gson.Gson
import com.net.ktwebmagic.dbservice.TargetLinkInfo
import com.net.ktwebmagic.dbservice.WebMagicDBService
import org.apache.logging.log4j.LogManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxOptions
import org.openqa.selenium.remote.RemoteWebDriver
import java.util.*

object WebMagicSche {

    var driver: RemoteWebDriver? = null
    val targetLinkQueue = LinkedList<TargetLink>()

    val gson = Gson()

    val logger = LogManager.getLogger(this.javaClass)

    init {
        System.setProperty("webdriver.gecko.driver", WebMagicConfig.geckoDriverPath)
        System.setProperty(FirefoxDriver.SystemProperty.DRIVER_USE_MARIONETTE, "true")
        System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE, "selenium.log")
        val options = FirefoxOptions()
        options.setHeadless(WebMagicConfig.isBrowserHeadless)
        if (!WebMagicConfig.isBrowserLoadImage) options.addPreference("permissions.default.image", 2)
        if (!WebMagicConfig.isBrowserLoadStyleSheet) options.addPreference("permissions.default.stylesheet", 2)
        this.driver = FirefoxDriver(options)
        //this.driver!!.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS)
        WebMagicDBService.dbInit()
    }

    fun restart(linksInfo: List<TargetLinkInfo>) {
        for (info in linksInfo) {
            val pageProc = gson.fromJson(info.json, Class.forName(info.clazz))
            val targetLink = TargetLink(info.url, pageProc as IPageProc, info.id)
            targetLinkQueue.add(targetLink)
        }
        doTasks()
    }

    fun start(vararg startLinks: TargetLink, forceRestart: Boolean = false) {
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

        for (link in startLinks) {
            addTargetLink(link)
        }
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
                        if (tryTimes > 0) {
                            Thread.sleep(5000)
                        }
                        if (link.pageProc.needBrowser) {
                            driver!!.get(link.url)
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
}