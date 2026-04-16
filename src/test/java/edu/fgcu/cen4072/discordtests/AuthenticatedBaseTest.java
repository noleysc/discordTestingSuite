package edu.fgcu.cen4072.discordtests;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;

import java.time.Duration;

/**
 * AuthenticatedBaseTest
 *
 * Shared base class for Discord test classes that require a logged-in session.
 *
 * Lifecycle:
 * - Open browser once before the class
 * - Log in once before the class
 * - Reset to Discord home after each test method
 * - Close browser once after the class
 */
public class AuthenticatedBaseTest {

    protected WebDriver driver;
    protected WebDriverWait wait;
    protected Actions actions;

    protected static final String LOGIN_URL = ConfigReader.get("discord.loginUrl");
    protected static final String EMAIL = ConfigReader.get("discord.email");
    protected static final String PASSWORD = ConfigReader.get("discord.password");
    protected static final String HOME_URL = "https://discord.com/channels/@me";

    private static final boolean DEMO_MODE = true; // flip to true for presentation/demo
    private static final long DEMO_PAUSE_MILLIS = 2000;

    @BeforeClass
    public void setUpClass() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--incognito");
        options.addArguments("--disable-blink-features=AutomationControlled");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        actions = new Actions(driver);

        driver.manage().window().maximize();

        loginToDiscord();
    }

    @AfterMethod
    public void resetToHomePage() {
        if (driver == null) {
            return;
        }

        driver.get(HOME_URL);
        wait.until(ExpectedConditions.urlContains("/channels/@me"));
        pause(5000);
    }

    @AfterClass
    public void tearDownClass() {
        if (driver != null) {
            driver.quit();
        }
    }

    protected void loginToDiscord() {
        driver.get(LOGIN_URL);
        pause(3000);

        wait.until(ExpectedConditions.presenceOfElementLocated(By.name("email"))).sendKeys(EMAIL);
        pause();

        driver.findElement(By.name("password")).sendKeys(PASSWORD);
        pause();

        driver.findElement(By.xpath("//button[@type='submit']")).click();
        pause(5000);

        wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("/login")));

        wait.until(ExpectedConditions.urlContains("/channels"));
        pause();
    }

    protected void goHome() {
        driver.get(HOME_URL);
        wait.until(ExpectedConditions.urlContains("/channels/@me"));
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
