package edu.fgcu.cen4072.discordtests;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * NavigationTests
 *
 * Tests navigation within Discord after login.
 * Assumes user is already authenticated via AuthenticatedBaseTest.
 */
public class NavigationTests extends AuthenticatedBaseTest {

    @Test (priority=1)
    public void testHomePageLoadsAfterLogin() {
        pause(4000);
        wait.until(ExpectedConditions.urlContains("/channels/@me"));
        Assert.assertTrue(driver.getCurrentUrl().contains("/channels/@me"));
    }

    @Test (priority=2)
    public void testShopNavigation() {
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[text()='Shop']")));
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[text()='Shop']"))).click();
        pause(4000);
        wait.until(ExpectedConditions.urlContains("/shop"));
        Assert.assertTrue(driver.getCurrentUrl().contains("/shop"));
    }

    @Test (priority=3)
    public void testQuestsNavigation() {
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[text()='Quests']")));
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[text()='Quests']"))).click();
        pause(4000);
        wait.until(ExpectedConditions.urlContains("/quest"));
        Assert.assertTrue(driver.getCurrentUrl().contains("/quest"));
    }

    @Test
    public void testDiscoveryApplicationsNavigation() {
        driver.get("https://discord.com/discovery/applications");
        pause(4000);
        wait.until(ExpectedConditions.urlContains("/discovery/applications"));
        Assert.assertTrue(driver.getCurrentUrl().contains("/discovery/applications"));
    }

    @Test
    public void testDiscoveryServersNavigation() {
        driver.get("https://discord.com/discovery/servers");
        pause(4000);
        wait.until(ExpectedConditions.urlContains("/discovery/servers"));
        Assert.assertTrue(driver.getCurrentUrl().contains("/discovery/servers"));
    }
}
