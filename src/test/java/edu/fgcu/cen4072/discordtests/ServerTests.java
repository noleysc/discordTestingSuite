package edu.fgcu.cen4072.discordtests;

import io.qameta.allure.*;
import edu.fgcu.cen4072.discordtests.pages.DashboardPage;
import edu.fgcu.cen4072.discordtests.pages.ServerSettingsPage;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("Server Management")
@Feature("Server Lifecycle")
public class ServerTests extends BaseTest {
    private String serverName;
    private final String pfpPath = null; // System.getProperty("user.dir") + "/logo.png";
    private DashboardPage dashboard;
    private final Random random = new Random();


    @BeforeClass
    public void prepareServer() {
        serverName = "Nexus Lab " + (random.nextInt(899) + 100);
        logger.info("Starting ServerTests with server name: {}", serverName);
        dashboard = new DashboardPage(getDriver());
        dashboard.ensureHydrated(); // relies on earlier LoginTests in the suite order
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

    @Test(priority = 4, dependsOnMethods = "testCreateVoiceChannel", description = "Create a role and enable Administrator permission")
    @Severity(SeverityLevel.CRITICAL)
    public void testConfigureServerRoles() {
        ServerSettingsPage settings = dashboard.openServerSettings(serverName);
        settings.createRole("Lead Architect");
        settings.enableAdministratorPermission();
    }

    @Test(priority = 5, dependsOnMethods = "testConfigureServerRoles", description = "Delete the server to clean up")
    @Severity(SeverityLevel.NORMAL)
    public void testDeleteServer() {
        ServerSettingsPage settings = dashboard.openServerSettings(serverName);
        settings.deleteServer(serverName);

        assertThat(dashboard.isServerCreated(serverName))
                .as("Server with name " + serverName + " should be deleted")
                .isFalse();
    }
}
