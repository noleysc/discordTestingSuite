package org.example;

import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.example.pages.dashboardPage;
import org.example.pages.loginPage;
import org.example.pages.userSettingsPage;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class UserSettingsTests extends baseTest {

    private final String loginIdentifier = "softwaretesting@tutamail.com";
    private final String loginPassword = "testPASS!@#";
    private dashboardPage dashboard;
    private final Random random = new Random();

    @BeforeClass
    public void loginAndPrepare() {
        logger.info("Logging in for UserSettingsTests...");
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

    @Test(priority = 1, description = "Mute the user from dashboard")
    @Severity(SeverityLevel.NORMAL)
    public void testMuteUser() {
        dashboard.toggleMute();
    }

    @Test(priority = 2, description = "Deafen the user from dashboard")
    @Severity(SeverityLevel.NORMAL)
    public void testDeafenUser() {
        dashboard.toggleDeafen();
    }

    @Test(priority = 3, description = "Modify User Display Name")
    @Severity(SeverityLevel.CRITICAL)
    public void testUpdateDisplayName() {
        userSettingsPage userSettings = dashboard.openUserSettings();
        String newDisplayName = "Name-" + (random.nextInt(8999) + 1000);
        
        userSettings.editProfile(newDisplayName, null, null);
        
        // Re-open to verify
        userSettings = dashboard.openUserSettings();
        assertThat(userSettings.getInputValue("Display Name"))
                .as("Display name should match updated value")
                .contains(newDisplayName);
        
        userSettings.closeUserSettings();
    }

    @Test(priority = 4, description = "Modify User Pronouns")
    @Severity(SeverityLevel.NORMAL)
    public void testUpdatePronouns() {
        userSettingsPage userSettings = dashboard.openUserSettings();
        String newPronouns = "they/them/" + (random.nextInt(99));
        
        userSettings.editProfile(null, newPronouns, null);
        
        // Re-open to verify
        userSettings = dashboard.openUserSettings();
        assertThat(userSettings.getInputValue("Pronouns"))
                .as("Pronouns should match updated value")
                .contains(newPronouns);
        
        userSettings.closeUserSettings();
    }

    @Test(priority = 5, description = "Modify User Avatar")
    @Severity(SeverityLevel.NORMAL)
    public void testUpdateAvatar() {
        logger.info("Waiting 30 seconds before avatar update to respect rate limits...");
        simulateThinking(30000, 31000);
        
        userSettingsPage userSettings = dashboard.openUserSettings();
        
        // Toggle avatar between black and white
        String projectRoot = System.getProperty("user.dir");
        String avatarPath = (random.nextBoolean()) ? projectRoot + "\\black.png" : projectRoot + "\\white.png";
        
        userSettings.uploadAvatar(avatarPath);
        userSettings.saveChanges();
        
        simulateThinking(1000, 2000);
        userSettings.closeUserSettings();
        
        // Assert we are back on dashboard
        dashboard.ensureHydrated();
    }
}