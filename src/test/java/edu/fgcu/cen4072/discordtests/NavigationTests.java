package edu.fgcu.cen4072.discordtests;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * NavigationTests covers basic page navigation and route-level UI checks
 * for Discord's public-facing authentication pages.
 */
public class NavigationTests extends BaseTest {

    private static final String LOGIN_URL = ConfigReader.get("discord.loginUrl");

    @Test
    public void testLoginPageLoads() {
        driver.get(LOGIN_URL);
        Assert.assertTrue(driver.getCurrentUrl().contains("/login"));
    }

    @Test
    public void testLoginPageTitleIsPresent() {
        driver.get(LOGIN_URL);
        Assert.assertFalse(driver.getTitle().isBlank());
    }

    @Test
    public void testRegisterLinkNavigatesToSignup() {
        driver.get(LOGIN_URL);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[contains(text(), 'Register')]"))).click();
        wait.until(ExpectedConditions.urlContains("/register"));
        Assert.assertTrue(driver.getCurrentUrl().contains("/register"));
    }

    @Test
    public void testRegisterPageLogInButtonNavigatesToLogin() {
        driver.get("https://discord.com/register");
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[.//span[contains(text(), 'Already have an account? Log in')]]")))
                .click();
        wait.until(ExpectedConditions.urlContains("/login"));
        Assert.assertTrue(driver.getCurrentUrl().contains("/login"));
    }
    @Test
    public void testLoginFieldsArePresent() {
        driver.get(LOGIN_URL);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.name("email")));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.name("password")));
        Assert.assertTrue(driver.findElement(By.name("email")).isDisplayed());
        Assert.assertTrue(driver.findElement(By.name("password")).isDisplayed());
    }
}
