package edu.fgcu.cen4072.discordtests; // This must be the very first line [cite: 83]

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * LoginTests validates authentication-related behaviors:
 * required fields, invalid inputs, and successful login flow.
 */
public class LoginTests extends BaseTest {

    private static final String LOGIN_URL = ConfigReader.get("discord.loginUrl");
    private static final String EMAIL = ConfigReader.get("discord.email");
    private static final String PASSWORD = ConfigReader.get("discord.password");

    @Test(priority = 1)
    public void testLoginPageLoads() {
        driver.get(LOGIN_URL);
        pause(4000);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("email")));

        Assert.assertTrue(driver.getCurrentUrl().contains("/login"));
    }

    @Test(priority = 2)
    public void testEmptyFields() {
        driver.get(LOGIN_URL);
        pause();

        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@type='submit']"))).click();
        pause();

        // Check which element is currently focused
        String activeElementTag = driver.switchTo().activeElement().getAttribute("name");
        Assert.assertEquals(activeElementTag, "email");
    }

    @Test(priority = 3)
    public void testEmailOnly() {
        driver.get(LOGIN_URL);
        pause();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.name("email"))).sendKeys(EMAIL);
        pause();

        driver.findElement(By.xpath("//button[@type='submit']")).click();
        pause();

        // Check which element is currently focused
        String activeElement = driver.switchTo().activeElement().getAttribute("name");
        Assert.assertEquals(activeElement, "password");
    }

    @Test(priority = 4)
    public void testPasswordOnly() {
        driver.get(LOGIN_URL);
        pause();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.name("password")))
                .sendKeys("somePassword");
        pause();

        driver.findElement(By.xpath("//button[@type='submit']")).click();
        pause();

        // Check which element is currently focused
        String activeElement = driver.switchTo().activeElement().getAttribute("name");
        Assert.assertEquals(activeElement, "email");
    }

    @Test(priority = 5)
    public void testValidEmailAndInvalidPassword() {
        driver.get(LOGIN_URL);
        pause();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.name("email"))).sendKeys(EMAIL);
        pause(5000);    // leave this time alone

        driver.findElement(By.name("password")).sendKeys("wrongPass");
        pause();

        driver.findElement(By.xpath("//button[@type='submit']")).click();
        pause();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(text(), 'Login or password is invalid')]")));

        Assert.assertTrue(driver.getPageSource().contains("Login or password is invalid"));
    }

    @Test(priority = 6)
    public void testRegisterNavigation() {
        driver.get(LOGIN_URL);
        pause(4000);

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[contains(text(), 'Register')]"))).click();
        pause(3000);

        wait.until(ExpectedConditions.urlContains("/register"));
        Assert.assertTrue(driver.getCurrentUrl().contains("/register"));
    }

    @Test(priority = 7)
    public void testValidLogin() {
        driver.get(LOGIN_URL);
        pause();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.name("email"))).sendKeys(EMAIL);
        pause();

        driver.findElement(By.name("password")).sendKeys(PASSWORD);
        pause();

        driver.findElement(By.xpath("//button[@type='submit']")).click();
        pause(5000);

        wait.until(ExpectedConditions.not(
                ExpectedConditions.urlContains("/login")
        ));
        Assert.assertFalse(driver.getCurrentUrl().contains("/login"));
    }
}