package org.example; // This must be the very first line [cite: 83]

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.annotations.Test;

/**
 * LoginTests covers core authentication-related UI flows on Discord's login page.
 */
public class LoginTests extends BaseTest {

    private static final String LOGIN_URL = ConfigReader.get("discord.loginUrl");
    private static final String EMAIL = ConfigReader.get("discord.email");
    private static final String PASSWORD = ConfigReader.get("discord.password");

    @Test
    public void testValidLogin() {
        driver.get(LOGIN_URL);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.name("email"))).sendKeys(EMAIL);
        driver.findElement(By.name("password")).sendKeys(PASSWORD);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@type='submit']"))).click();
    }

    @Test
    public void testInvalidPassword() {
        driver.get(LOGIN_URL);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.name("email"))).sendKeys(EMAIL);
        driver.findElement(By.name("password")).sendKeys("wrongPass");
        driver.findElement(By.xpath("//button[@type='submit']")).click();
    }

    @Test
    public void testEmptyFields() {
        driver.get(LOGIN_URL);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@type='submit']"))).click();
    }

    @Test
    public void testRegisterNavigation() {
        driver.get(LOGIN_URL);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[contains(text(), 'Register')]"))).click();
    }

    @Test
    public void testForgotPasswordLink() {
        driver.get(LOGIN_URL);
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[contains(translate(text(), 'FORGOT', 'forgot'), 'forgot')]")))
                .click();
    }
}