package edu.fgcu.cen4072.discordtests;

import io.qameta.allure.*;
import edu.fgcu.cen4072.discordtests.pages.DashboardPage;
import edu.fgcu.cen4072.discordtests.pages.LoginPage;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("Social")
@Feature("Status and Friends")
public class SocialTests extends BaseTest {
    private final String loginIdentifier = "softwaretesting@tutamail.com";
    private final String loginPassword = "testPASS!@#";
    private DashboardPage dashboard;

    @BeforeClass
    public void loginAndPrepare() {
        logger.info("Starting SocialTests...");
        if (getDriver().getCurrentUrl().contains("channels/@me")) {
            logger.info("Already on dashboard, skipping login navigation.");
        } else {
            getDriver().get("https://discord.com/login");
        }

        LoginPage login = new LoginPage(getDriver());
        login.login(loginIdentifier, loginPassword);
        dashboard = new DashboardPage(getDriver());
        dashboard.ensureHydrated();
    }

    @Test(priority = 1, description = "Change user online status to Idle")
    @Severity(SeverityLevel.NORMAL)
    public void testSetStatusIdle() {
        dashboard.changeStatus("Idle");
        assertThat(dashboard.getCurrentStatus())
                .as("Status should be updated to Idle")
                .containsIgnoringCase("Idle");
    }

    @Test(priority = 2, description = "Change user online status to Do Not Disturb")
    @Severity(SeverityLevel.NORMAL)
    public void testSetStatusDND() {
        dashboard.changeStatus("Do Not Disturb");
        assertThat(dashboard.getCurrentStatus())
                .as("Status should be updated to Do Not Disturb")
                .containsIgnoringCase("Disturb");
    }

    @Test(priority = 3, description = "Navigate to Friends Tab")
    @Severity(SeverityLevel.MINOR)
    public void testNavigateToFriends() {
        dashboard.openFriendsTab();
        
        // Verify by checking for "Add Friend" button or "Online" tab presence
        boolean onFriends = (boolean) ((org.openqa.selenium.JavascriptExecutor) getDriver()).executeScript(
            "return document.body.innerText.toLowerCase().includes('add friend') || " +
            "       document.body.innerText.toLowerCase().includes('online');"
        );
        
        assertThat(onFriends)
                .as("Should be on the Friends tab")
                .isTrue();
    }
}
