package com.net.ktwebmagic

import org.apache.log4j.LogManager
import java.nio.file.Files
import java.nio.file.Paths

object WebMagicConfig {

    private val logger = LogManager.getLogger(this.javaClass)

    var isBrowserHeadless = true

    var isBrowserLoadImage = false

    var isBrowserLoadStyleSheet = false

    var geckoDriverPath = "C:/geckodriver.exe"

    init {
        val configPath = Paths.get("config.properties")
        if (Files.exists(configPath)) {
            logger.info("config file exists, loading config...")
            val lines = Files.readAllLines(configPath).filter { it.isNotBlank() }
            for (line in lines) {
                val kv = line.split(Regex("\\s*=\\s*"))
                if (kv.size == 2) {
                    when (kv[0]) {
                        "browser.headless" -> {
                            isBrowserHeadless = kv[1].toLowerCase().equals("true")
                        }
                        "browser.load.images" -> {
                            isBrowserLoadImage = kv[1].toLowerCase().equals("true")
                        }
                        "browser.load.style" -> {
                            isBrowserLoadStyleSheet = kv[1].toLowerCase().equals("true")
                        }
                        "gecko.driver.path" -> {
                            geckoDriverPath = kv[1].removeSurrounding("\"")
                        }
                        else -> {
                            logger.warn("unknown config: {} ${kv[0]}")
                        }
                    }
                }
            }
        }
    }

}