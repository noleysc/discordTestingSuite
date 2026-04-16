package org.example;

import io.qameta.allure.*;
import org.example.pages.dashboardPage;
import org.example.pages.loginPage;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("Social")
@Feature("User Presence and Social Navigation")
public class SocialTests extends baseTest {
    private final String loginIdentifier = "softwaretesting@tutamail.com";
    private final String loginPassword = "testPASS!@#";
    private dashboardPage dashboard;

    @BeforeClass
    public void loginAndPrepare() {
        logger.info("Starting SocialTests...");
        if (getDriver().getCurrentUrl().contains("channels/@me")) {
            logger.info("Already on dashboard, skipping login navigation.");
        } else {
            getDriver().get("https://discord.com/login");
        }

        loginPage login = new loginPage(getDriver());
        login.login(loginIdentifier, loginPassword);
        dashboard = new dashboardPage(getDriver());
        dashboard.ensureHydrated();
    }

    @Test(priority = 1, description = "Change user status to Idle")
    @Severity(SeverityLevel.NORMAL)
    public void testChangeStatusToIdle() {
        dashboard.changeStatus("Idle");
        simulateThinking(3000, 5000);
        assertThat(dashboard.getCurrentStatus().toLowerCase())
                .as("Status should be Idle")
                .contains("idle");
    }

    @Test(priority = 2, description = "Change user status to Do Not Disturb")
    @Severity(SeverityLevel.NORMAL)
    public void testChangeStatusToDND() {
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

    @Test(priority = 5, description = "Navigate to Friends list")
    @Severity(SeverityLevel.NORMAL)
    public void testNavigateToFriends() {
        dashboard.openFriendsTab();
        
        boolean onFriends = (Boolean) ((org.openqa.selenium.JavascriptExecutor) getDriver()).executeScript(
            "return document.body.innerText.toLowerCase().includes('add friend') || " +
            "       document.body.innerText.toLowerCase().includes('online');"
        );
        
        assertThat(onFriends)
                .as("Should be on the Friends tab")
                .isTrue();
    }
}
