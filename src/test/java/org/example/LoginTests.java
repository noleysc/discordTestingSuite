package org.example; // This must be the very first line [cite: 83]

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.*;
import org.testng.annotations.*;
import java.time.Duration;

public class LoginTests { // All your code MUST be inside these main brackets
    WebDriver driver;
    WebDriverWait wait;

    @BeforeMethod
    public void setup() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--incognito");
        options.addArguments("--disable-blink-features=AutomationControlled");

        driver = new ChromeDriver(options); // Uses Selenium WebDriver [cite: 72]
        wait = new WebDriverWait(driver, Duration.ofSeconds(10)); // Addresses dynamic elements [cite: 10, 49]
        driver.get("https://discord.com/login");
    }

    @Test
    public void testValidLogin() {
        wait.until(ExpectedConditions.presenceOfElementLocated(By.name("email"))).sendKeys("nstilwell863@gmail.com");
        driver.findElement(By.name("password")).sendKeys("testPassword!@#");
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@type='submit']"))).click();
    }

    @Test
    public void testInvalidPassword() {
        wait.until(ExpectedConditions.presenceOfElementLocated(By.name("email"))).sendKeys("nstilwell863@gmail.com");
        driver.findElement(By.name("password")).sendKeys("wrongPass");
        driver.findElement(By.xpath("//button[@type='submit']")).click();
    }

    @Test
    public void testEmptyFields() {
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@type='submit']"))).click();
    }

    @Test
    public void testRegisterNavigation() {
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[contains(text(), 'Register')]"))).click();
    }

    @Test
    public void testForgotPasswordLink() {
        // Broad search to handle automation constraints [cite: 10]
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[contains(translate(text(), 'FORGOT', 'forgot'), 'forgot')]"))).click();
    }

    @AfterMethod
    public void teardown() {
        if (driver != null) {
            driver.quit(); // Cleanly closes execution [cite: 30, 64]
        }
    }
} // Final bracket closes the class