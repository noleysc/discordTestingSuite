package org.example; // This must be the very first line [cite: 83]

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.annotations.Test;

/**
 * LoginTests covers core authentication-related UI flows on Discord's login page.
 */
public class LoginTests extends BaseTest {

    @Test
    public void testValidLogin() {
        driver.get("https://discord.com/login");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.name("email")))
                .sendKeys("discord.testing.fgcu@gmail.com");
        driver.findElement(By.name("password")).sendKeys("DiscordTest123!");
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@type='submit']"))).click();
    }

    @Test
    public void testInvalidPassword() {
        driver.get("https://discord.com/login");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.name("email")))
                .sendKeys("discord.testing.fgcu@gmail.com");
        driver.findElement(By.name("password")).sendKeys("wrongPass");
        driver.findElement(By.xpath("//button[@type='submit']")).click();
    }

    @Test
    public void testEmptyFields() {
        driver.get("https://discord.com/login");
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@type='submit']"))).click();
    }

    @Test
    public void testRegisterNavigation() {
        driver.get("https://discord.com/login");
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[contains(text(), 'Register')]"))).click();
    }

    @Test
    public void testForgotPasswordLink() {
        driver.get("https://discord.com/login");
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[contains(translate(text(), 'FORGOT', 'forgot'), 'forgot')]"))).click();
    }
}