package org.example;

import io.qameta.allure.Attachment;
import org.openqa.selenium.Keys;
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

    protected Keys getModifierKey() {
        return System.getProperty("os.name").toLowerCase().contains("mac") ? Keys.COMMAND : Keys.CONTROL;
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
        
        // Clean Room Strategy: Use a thread-unique isolated profile for automation
        String threadId = String.valueOf(Thread.currentThread().getId());
        String profilePath = System.getProperty("user.dir") + "\\AutomationProfile\\Firefox_Clean_" + threadId;
        File profileDir = new File(profilePath);
        if (!profileDir.exists()) profileDir.mkdirs();
        
        logger.info("Using dedicated automation profile for thread " + threadId + ": " + profilePath);
        options.addArguments("-profile", profilePath);
        options.addArguments("-no-remote");
        
        // Advanced Stealth & Fingerprint Protection
        String os = System.getProperty("os.name").toLowerCase();
        String userAgent = os.contains("mac") 
            ? "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:124.0) Gecko/20100101 Firefox/124.0"
            : "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:124.0) Gecko/20100101 Firefox/124.0";
        
        options.addPreference("general.useragent.override", userAgent);
        options.addPreference("dom.webdriver.enabled", false);
        options.addPreference("useAutomationExtension", false);
        options.addPreference("privacy.trackingprotection.enabled", false);
        
        return new FirefoxDriver(options);
    }

    private void copyProfileSelection(File source, File target) {
        if (!target.exists()) target.mkdirs();
        
        // Critical files for session & auth
        String[] criticalFiles = {"cookies.sqlite", "places.sqlite", "sessionstore.jsonlz4", "key4.db", "logins.json", "cert9.db"};
        
        for (String fileName : criticalFiles) {
            File srcFile = new File(source, fileName);
            File destFile = new File(target, fileName);
            if (srcFile.exists()) {
                try (java.io.FileInputStream in = new java.io.FileInputStream(srcFile);
                     java.io.FileOutputStream out = new java.io.FileOutputStream(destFile)) {
                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    while ((bytesRead = in.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }
                } catch (Exception e) {
                    logger.warn("Resilient copy failed for " + fileName + ", attempting standard copy: " + e.getMessage());
                    try {
                        java.nio.file.Files.copy(srcFile.toPath(), destFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                    } catch (Exception e2) {
                        logger.error("All copy attempts failed for " + fileName);
                    }
                }
            }
        }
    }

    @Attachment(value = "Page screenshot", type = "image/png")
    public byte[] saveScreenshot(WebDriver driver) {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }
}
