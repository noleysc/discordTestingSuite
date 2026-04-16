package edu.fgcu.cen4072.discordtests;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.time.Duration;

/**
 * BaseTest
 *
 * This class provides shared setup and teardown functionality
 * for all Selenium test classes in the project.
 *
 * All test classes should extend this class to inherit:
 * - WebDriver instance
 * - WebDriverWait instance
 * - Actions instance
 */
public class BaseTest {

    protected WebDriver driver;
    protected WebDriverWait wait;
    protected Actions actions;

    private static final boolean DEMO_MODE = false; // For demo video, flip to 'true'
    private static final long DEMO_PAUSE_MILLIS = 2000;

    @BeforeMethod
    public void setUp() {

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--incognito");
        options.addArguments("--disable-blink-features=AutomationControlled");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        actions = new Actions(driver);

        driver.manage().window().maximize();
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    protected void pause() {
        pause(DEMO_PAUSE_MILLIS);
    }

    protected void pause(long millis) {
        if (!DEMO_MODE) {
            return;
        }

        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Demo pause was interrupted", e);
        }
    }
}
