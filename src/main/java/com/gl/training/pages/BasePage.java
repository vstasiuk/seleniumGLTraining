package com.gl.training.pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.LoadableComponent;
import org.openqa.selenium.support.ui.Wait;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public abstract class BasePage<T extends BasePage<T>> extends LoadableComponent<T> {

    public static final int TIME_WAIT_SECONDS = 10;
    private static final int SCRIPT_TIME_OUT_WAIT_SECONDS = 120;
    private static final int PAGE_LOAD_TIME_WAIT_SECONDS = 600;

    private Wait visibilityWait;
    private Wait invisibilityWait;

    protected final Logger log = LogManager.getLogger(this);
    private final WebDriver wd;

    private static Properties props = new Properties();

    public BasePage(WebDriver wd) {
        this.wd = wd;
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream webStream = loader.getResourceAsStream("env.properties");
        try {
            props.load(webStream);
            webStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        visibilityWait = new FluentWait<WebDriver>(getWebDriverCurrent())
                .withTimeout(TIME_WAIT_SECONDS * 20, TimeUnit.SECONDS)
                .pollingEvery(1, TimeUnit.SECONDS)
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class);

        invisibilityWait
                = new FluentWait<WebDriver>(getWebDriverCurrent())
                .withTimeout(TIME_WAIT_SECONDS * 20, TimeUnit.SECONDS)
                .pollingEvery(10, TimeUnit.MILLISECONDS)
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class);

        PageFactory.initElements(wd, this);
    }

    protected static Properties getProps() {
        return props;
    }

    public static String getPageURL() {
        return BasePage.props.getProperty("application.url");
    }

//    protected abstract void checkUniqueElements() throws Error;

    protected WebDriver getWebDriverCurrent() {
        return wd;
    }

    @Override
    protected void load() {
        log.info("Loading page: {}", getPageURL());
        wd.get(getPageURL());
    }

    @Override
    protected void isLoaded() throws Error {
        Assert.assertThat("Wrong page URL", wd.getCurrentUrl(), Matchers.equalToIgnoringCase(getPageURL()));
//        checkUniqueElements();
    }

    protected void sendKeys(final WebElement webElement, String text) {
        waitForClickable(webElement);
        webElement.clear();
        webElement.sendKeys(text);
    }

    protected WebElement waitForVisibility(WebElement webElement) {
        try {
            visibilityWait.until(ExpectedConditions.visibilityOf(webElement));
        } catch (NoSuchElementException nse) {
            System.out.println("Try to wait little more (wait for visibility)");
            nse.printStackTrace();
            return null;
        }
        return webElement;
    }

    protected void waitForInvisibility(final By locator) {
        try {
            invisibilityWait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
        } catch (NoSuchElementException e) {
            System.out.println("Try to wait little more (wait for invisibility)");
            e.printStackTrace();
        }
    }

    protected WebElement waitForClickable(WebElement webElement) {
        waitForVisibility(webElement);
        try {
            visibilityWait.until(ExpectedConditions.elementToBeClickable(webElement));
        } catch (NoSuchElementException nse) {
            System.out.println("Try to wait little more (wait for clickable)");
            nse.printStackTrace();
        }
        return webElement;
    }

    protected boolean click(WebElement webElement) {
        boolean result = false;
        int attempts = 0;
        while (attempts < 3) {
            try {
//                scrollToElement(waitForVisibility(webElement));
                waitForClickable(webElement).click();
                result = true;
                break;
            } catch (StaleElementReferenceException e) {
            }
            attempts++;
        }
        return result;
    }

    protected String getImageNameFromAbsolutePath(File fileToParse) {
        int indexOfSlash;
        if (fileToParse.getAbsolutePath().contains("/"))
            indexOfSlash = fileToParse.getAbsolutePath().lastIndexOf('/');
        else indexOfSlash = fileToParse.getAbsolutePath().lastIndexOf('\\');
        int indexOfDot = fileToParse.getAbsolutePath().lastIndexOf('.');
        return fileToParse.getAbsolutePath().substring(indexOfSlash + 1, indexOfDot);
    }

    protected boolean isElementPresent(final WebElement we) {
        try {
            return we.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    protected void selectCustomDropDown(WebElement buttonSelect, List<WebElement> optionsLinks, String textToSelect) {
        waitForClickable(buttonSelect).click();
        for (WebElement optionLink : optionsLinks) {
            if (waitForClickable(optionLink).getText().equalsIgnoreCase(textToSelect)) {
                optionLink.click();
                break;
            }
        }
    }

    public RemoteWebDriver switchToNewlyOpenedTab() {
        RemoteWebDriver webDriver;
        ArrayList<String> currentTabs = new ArrayList<>(getWebDriverCurrent().getWindowHandles());
        webDriver = (RemoteWebDriver) getWebDriverCurrent().switchTo().window(currentTabs.get(getWebDriverCurrent().getWindowHandles().size() - 1));
        webDriver.manage().window().maximize();
        return webDriver;
    }

    protected void moveMouseCursorToWebElement(WebElement webElement) {
//        scrollToElement(webElement);
        waitForClickable(webElement);
        Actions action = new Actions(getWebDriverCurrent());
        action.moveToElement(webElement).perform();
    }


}
