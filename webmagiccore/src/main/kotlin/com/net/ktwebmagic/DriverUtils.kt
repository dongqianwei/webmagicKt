package com.net.ktwebmagic

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait


fun WebDriver.waitElementByXpath(xpath: String, timeout: Long): WebElement {
    val wait = WebDriverWait(this, timeout)
    val byXPath = By.xpath(xpath)
    return wait.until(ExpectedConditions.presenceOfElementLocated(byXPath))
}

fun WebDriver.waitElementsByXpath(xpath: String, timeout: Long): List<WebElement> {
    val wait = WebDriverWait(this, timeout)
    val byXPath = By.xpath(xpath)
    return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(byXPath))
}

fun WebDriver.moveToElement(element: WebElement) {
    val actions = Actions(this)
    actions.moveToElement(element)
    actions.perform()
}