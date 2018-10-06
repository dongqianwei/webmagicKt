package com.net.ktwebmagic

import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClients
import org.apache.http.impl.client.LaxRedirectStrategy
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path


object Downloader {

    fun download(url: URL, dstFile: Path): Path {
        val httpclient = HttpClients.custom()
                .setRedirectStrategy(LaxRedirectStrategy()) // adds HTTP REDIRECT support to GET and POST methods
                .build()
        httpclient.use {
            val get = HttpGet(url.toURI()) // we're using GET but it could be via POST as well
            return httpclient.execute(get) {
                if (!Files.isDirectory(dstFile.parent)) {
                    Files.createDirectories(dstFile.parent)
                }
                Files.copy(it.entity.content, dstFile)
                dstFile
            }
        }
    }

}