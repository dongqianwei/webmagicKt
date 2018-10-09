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
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit

object WebMagicSche {

    var driver: RemoteWebDriver? = null
    private val browserTargetLinkQueue = LinkedList<TargetLink>()
    private val nonBrowserTargetLinkQueue = LinkedBlockingQueue<TargetLink>()

    private val executor = Executors.newFixedThreadPool(5)

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
            if (targetLink.pageProc.needBrowser) {
                browserTargetLinkQueue.add(targetLink)
            } else {
                nonBrowserTargetLinkQueue.add(targetLink)
            }
        }
        schedulerNonBrowserTasks()
        schedulerBrowserTasks()
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
        schedulerNonBrowserTasks()
        schedulerBrowserTasks()
        executor.awaitTermination(10, TimeUnit.DAYS)
        logger.info("web magic END...")
    }

    private fun schedulerNonBrowserTasks() {
        executor.execute {
            while (true) {
                // 执行nonBrowserTargetLinkQueue中的任务
                val targetLink = nonBrowserTargetLinkQueue.poll(5, TimeUnit.SECONDS)
                if (targetLink != null) {
                    executor.execute {
                        targetLink.pageProc.process()
                        removeFinishedTask(targetLink)
                    }
                } else {
                    if (isBrowserTasksAllFinished && nonBrowserTargetLinkQueue.isEmpty()) {
                        logger.info("Non Browser Task Scheduler finished...")
                        executor.shutdown()
                        break
                    }
                }
            }
        }
    }

    @Volatile
    private var isBrowserTasksAllFinished = false

    private fun schedulerBrowserTasks() {
        try {
            while (!browserTargetLinkQueue.isEmpty()) {
                val link = browserTargetLinkQueue.pop()
                var tryTimes = 0
                while (true) {
                    try {
                        logger.info("get url: ${link.url}")
                        if (tryTimes > 0) {
                            Thread.sleep(5000)
                        }
                        driver!!.get(link.url)
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

                removeFinishedTask(link)
            }

            isBrowserTasksAllFinished = true;

            logger.info("Browser Task Scheduler finished...")
        } catch (ex: Exception) {
            logger.error("url: ${driver!!.currentUrl}", ex)
        } finally {
            driver!!.close()
        }
    }

    private fun removeFinishedTask(link: TargetLink) {
        transaction {
            WebMagicDBService.dbRemoveTargetLink(link.id)
            // 队列为空，设置任务状态为完成
            if (browserTargetLinkQueue.isEmpty() && nonBrowserTargetLinkQueue.isEmpty()) {
                WebMagicDBService.dbSetTaskFinished(true)
            }
        }
    }


    fun addTargetLink(link: TargetLink) {
        // 数据库持久化
        val id = transaction {
            WebMagicDBService.dbAddTargetLink(link)
        }
        link.id = id

        if (link.pageProc.needBrowser) {
            browserTargetLinkQueue.add(link)
        } else {
            nonBrowserTargetLinkQueue.add(link)
        }
    }
}