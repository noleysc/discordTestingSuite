package edu.fgcu.cen4072.discordtests;

import io.qameta.allure.*;
import edu.fgcu.cen4072.discordtests.pages.DashboardPage;
import edu.fgcu.cen4072.discordtests.pages.LoginPage;
import edu.fgcu.cen4072.discordtests.pages.ServerSettingsPage;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("Server Management")
@Feature("Server Lifecycle")
public class ServerTests extends BaseTest {
    private final String loginIdentifier = "softwaretesting@tutamail.com";
    private final String loginPassword = "testPASS!@#";
    private String serverName;
    private final String pfpPath = null;
    private DashboardPage dashboard;
    private final Random random = new Random();

    @BeforeClass
    public void loginAndPrepare() {
        serverName = "Nexus Lab " + (random.nextInt(899) + 100);
        logger.info("Starting ServerTests with server name: {}", serverName);

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

    @Test(priority = 1, description = "Create a new Discord server")
    @Severity(SeverityLevel.BLOCKER)
    public void testCreateServer() {
        dashboard.openAddServerModal();
        dashboard.createServer(serverName, pfpPath);

        assertThat(dashboard.isServerCreated(serverName))
                .as("Server with name " + serverName + " should be created and visible")
                .isTrue();
    }

    @Test(priority = 2, dependsOnMethods = "testCreateServer", description = "Create a text channel in the new server")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateTextChannel() {
        String channelName = "logs-" + (random.nextInt(8999) + 1000);
        dashboard.openCreateChannelModal();
        dashboard.createChannel(channelName, false);
    }

    @Test(priority = 3, dependsOnMethods = "testCreateTextChannel", description = "Create a voice channel in the new server")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateVoiceChannel() {
        String channelName = "voice-" + (random.nextInt(8999) + 1000);
        dashboard.openCreateChannelModal();
        dashboard.createChannel(channelName, true);
    }

    @Test(priority = 4, dependsOnMethods = "testCreateVoiceChannel", description = "Create a new server role")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateRole() {
        ServerSettingsPage settings = dashboard.openServerSettings(serverName);
        settings.createRole("Lead Architect");
    }

    @Test(priority = 5, dependsOnMethods = "testCreateRole", description = "Enable Administrator permission for a role")
    @Severity(SeverityLevel.CRITICAL)
    public void testEnableAdminPermission() {
        ServerSettingsPage settings = dashboard.openServerSettings(serverName);
        settings.enableAdministratorPermission();
    }

    @Test(priority = 6, dependsOnMethods = "testEnableAdminPermission", description = "Delete the server to clean up")
    @Severity(SeverityLevel.NORMAL)
    public void testDeleteServer() {
        ServerSettingsPage settings = dashboard.openServerSettings(serverName);
        settings.deleteServer(serverName);

        assertThat(dashboard.isServerCreated(serverName))
                .as("Server with name " + serverName + " should be deleted")
                .isFalse();
    }
}
