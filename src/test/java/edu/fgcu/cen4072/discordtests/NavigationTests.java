package edu.fgcu.cen4072.discordtests;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.time.Duration;

/**
 * NavigationTests covers basic page navigation and route-level UI checks
 * for Discord's public-facing authentication pages.
 */
public class NavigationTests extends BaseTest {

    private static final String LOGIN_URL = "https://discord.com/login";

    @org.testng.annotations.BeforeMethod
    public void methodDelay() {
        logger.info("Waiting 5 seconds before navigation test...");
        simulateThinking(5000, 5500);
    }

    @Test
    public void testLoginPageLoads() {
        getDriver().get(LOGIN_URL);
        Assert.assertTrue(getDriver().getCurrentUrl().contains("/login"));
    }

    @Test
    public void testLoginPageTitleIsPresent() {
        getDriver().get(LOGIN_URL);
        Assert.assertFalse(getDriver().getTitle().isBlank());
    }

    @Test
    public void testRegisterLinkNavigatesToSignup() {
        getDriver().get(LOGIN_URL);
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[contains(text(), 'Register')]"))).click();
        wait.until(ExpectedConditions.urlContains("/register"));
        Assert.assertTrue(getDriver().getCurrentUrl().contains("/register"));
    }

    @Test
    public void testRegisterPageLogInButtonNavigatesToLogin() {
        getDriver().get("https://discord.com/register");
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[.//span[contains(text(), 'Already have an account? Log in')]]")))
                .click();
        wait.until(ExpectedConditions.urlContains("/login"));
        Assert.assertTrue(getDriver().getCurrentUrl().contains("/login"));
    }
    @Test
    public void testLoginFieldsArePresent() {
        getDriver().get(LOGIN_URL);
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.name("email")));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.name("password")));
        Assert.assertTrue(getDriver().findElement(By.name("email")).isDisplayed());
        Assert.assertTrue(getDriver().findElement(By.name("password")).isDisplayed());
    }
}
