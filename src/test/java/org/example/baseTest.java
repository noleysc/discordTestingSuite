package org.example;

import io.qameta.allure.Attachment;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import java.io.File;

public class baseTest {
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();
    protected static final Logger logger = LoggerFactory.getLogger(baseTest.class);

    public WebDriver getDriver() {
        return driverThreadLocal.get();
    }

    // Default: Reset driver for every method unless overridden
    protected boolean useClassLevelDriver() {
        return false;
    }

    @BeforeClass
    public void suiteSetUp() {
        if (useClassLevelDriver()) {
            logger.info("Initializing Class-Level Firefox WebDriver...");
            initDriver();
        }
    }

    @BeforeMethod
    public void methodSetUp() {
        if (!useClassLevelDriver()) {
            logger.info("Initializing Method-Level Firefox WebDriver...");
            initDriver();
        }
    }

    private void initDriver() {
        // Add a short delay to cool down
        logger.warn("Cooldown delay: waiting 5 seconds before initializing driver...");
        try { Thread.sleep(5000); } catch (InterruptedException ignore) {}

        driverThreadLocal.set(initFirefox());
        getDriver().manage().window().maximize();
    }

    @AfterMethod
    public void methodTearDown() {
        if (!useClassLevelDriver()) {
            quitDriver();
        }
    }

    @AfterClass
    public void suiteTearDown() {
        if (useClassLevelDriver()) {
            quitDriver();
        }
    }

    private void quitDriver() {
        if (getDriver() != null) {
            logger.info("Quitting WebDriver...");
            getDriver().quit();
            driverThreadLocal.remove();
        }
    }

    protected WebDriver initFirefox() {
        FirefoxOptions options = new FirefoxOptions();
        
        // Use a unique profile subdirectory to avoid lock issues
        String baseProfilePath = System.getProperty("user.dir") + "/AutomationProfile/Firefox";
        String uniqueProfilePath = baseProfilePath + "_" + System.currentTimeMillis();
        File profileDir = new File(uniqueProfilePath);
        if (!profileDir.exists()) profileDir.mkdirs();
        
        options.addArguments("-profile", uniqueProfilePath);
        
        // Hide webdriver usage and set User Agent directly on options
        options.addPreference("dom.webdriver.enabled", false);
        options.addPreference("useAutomationExtension", false);
        options.addPreference("general.useragent.override", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:124.0) Gecko/20100101 Firefox/124.0");
        
        // Suppress permission prompts
        options.addPreference("permissions.default.desktop-notification", 2);
        options.addPreference("permissions.default.microphone", 2);
        options.addPreference("permissions.default.camera", 2);
        options.addPreference("permissions.default.geo", 2);
        options.addPreference("dom.webnotifications.enabled", false);
        options.addPreference("media.navigator.permission.disabled", true);
        options.addPreference("media.navigator.streams.fake", true);
        
        return new FirefoxDriver(options);
    }

    @Attachment(value = "Page screenshot", type = "image/png")
    public byte[] saveScreenshot(WebDriver driver) {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }
}
