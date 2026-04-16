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
    public void prepareSocial() {
        logger.info("Starting SocialTests...");
        if (dashboard == null) {
            dashboard = new DashboardPage(getDriver());
        }
        dashboard.ensureHydrated();
    }

    @Test(priority = 1, description = "Change user online status to Idle")
    @Severity(SeverityLevel.NORMAL)
    public void testSetStatusIdle() {
        dashboard.changeStatus("Idle");
        simulateThinking(3000, 5000);
        assertThat(dashboard.getCurrentStatus().toLowerCase())
                .as("Status should be Idle")
                .contains("idle");
    }

    @Test(priority = 2, description = "Change user online status to Do Not Disturb")
    @Severity(SeverityLevel.NORMAL)
    public void testSetStatusDND() {
        dashboard.changeStatus("Do Not Disturb");
        simulateThinking(3000, 5000);
        assertThat(dashboard.getCurrentStatus().toLowerCase())
                .as("Status should be Do Not Disturb")
                .contains("disturb");
    }

    @Test(priority = 3, description = "Change user status to Invisible (Offline)")
    @Severity(SeverityLevel.NORMAL)
    public void testChangeStatusToInvisible() {
        dashboard.changeStatus("Invisible");
        simulateThinking(3000, 5000);
        assertThat(dashboard.getCurrentStatus().toLowerCase())
                .as("Status should be Invisible")
                .contains("invisible");
    }

    @Test(priority = 4, description = "Change user status back to Online")
    @Severity(SeverityLevel.NORMAL)
    public void testChangeStatusToOnline() {
        dashboard.changeStatus("Online");
        simulateThinking(3000, 5000);
        assertThat(dashboard.getCurrentStatus().toLowerCase())
                .as("Status should be Online")
                .contains("online");
    }

    @Test(priority = 5, description = "Navigate to Friends Tab")
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
