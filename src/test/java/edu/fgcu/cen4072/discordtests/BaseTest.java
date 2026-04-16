package edu.fgcu.cen4072.discordtests;

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
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import java.io.File;

public class BaseTest {
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();
    protected static final Logger logger = LoggerFactory.getLogger(BaseTest.class);

    public WebDriver getDriver() {
        return driverThreadLocal.get();
    }

    protected Keys getModifierKey() {
        return System.getProperty("os.name").toLowerCase().contains("mac") ? Keys.COMMAND : Keys.CONTROL;
    }

    @BeforeSuite
    public void suiteSetUp() {
        logger.info("Initializing Suite-Level Firefox WebDriver...");
        initDriver();
    }

    @AfterSuite
    public void suiteTearDown() {
        quitDriver();
    }

    private void initDriver() {
        // Add a short delay to cool down
        logger.warn("Cooldown delay: waiting 5 seconds before initializing driver...");
        try { Thread.sleep(5000); } catch (InterruptedException ignore) {}

        driverThreadLocal.set(initFirefox());
        getDriver().manage().window().maximize();
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
        
        // Use the explicit binary path provided by the user
        // Note: Selenium usually needs the .exe, but we'll try to point to the standard install if the link is just a shortcut
        String firefoxPath = "C:\\Program Files\\Mozilla Firefox\\firefox.exe";
        options.setBinary(firefoxPath);
        
        // Use a stable profile for persistent session
        String testProfilePath = System.getProperty("user.dir") + "\\AutomationProfile\\Firefox_Main_Session";
        File profileDir = new File(testProfilePath);
        if (!profileDir.exists()) profileDir.mkdirs();
        
        options.addArguments("-profile", testProfilePath);
        options.addArguments("-no-remote");
        
        // Disable all permission prompts
        options.addPreference("permissions.default.desktop-notification", 2);
        options.addPreference("permissions.default.camera", 2);
        options.addPreference("permissions.default.microphone", 2);
        options.addPreference("permissions.default.geo", 2);
        options.addPreference("privacy.popups.showBrowserMessage", false);
        options.addPreference("dom.webdriver.enabled", false);
        options.addPreference("useAutomationExtension", false);
        
        options.addPreference("general.useragent.override", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:124.0) Gecko/20100101 Firefox/124.0");
        
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

    protected void simulateThinking(int min, int max) {
        if (max <= min) {
            max = min + 100;
        }
        try {
            Thread.sleep(new java.util.Random().nextInt(max - min + 1) + min);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Attachment(value = "Page screenshot", type = "image/png")
    public byte[] saveScreenshot(WebDriver driver) {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }
}
