package org.example;

import io.qameta.allure.*;
import org.example.pages.dashboardPage;
import org.example.pages.loginPage;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("Messaging")
@Feature("Chat Functionality")
public class MessagingTests extends baseTest {
    private final String loginIdentifier = "softwaretesting@tutamail.com";
    private final String loginPassword = "testPASS!@#";
    private String serverName = "Nexus Lab"; // Using a known stable server
    private String channelName;
    private dashboardPage dashboard;
    private final Random random = new Random();

    @BeforeClass
    public void loginOnly() {
        if (getDriver().getCurrentUrl().contains("channels/@me")) {
            logger.info("Already on dashboard, skipping login navigation.");
        } else {
            getDriver().get("https://discord.com/login");
        }
        
        loginPage login = new loginPage(getDriver());
        login.login(loginIdentifier, loginPassword);
        dashboard = new dashboardPage(getDriver());
        dashboard.ensureHydrated();
        
        channelName = "test-chat-" + (random.nextInt(899) + 100);
        logger.info("MessagingTests prepared with channel: {}", channelName);
    }

    @Test(priority = 1, description = "Prepare: Select server and create test channel")
    @Severity(SeverityLevel.NORMAL)
    public void testPrepareMessaging() {
        dashboard.selectServer(serverName);
        dashboard.openCreateChannelModal();
        dashboard.createChannel(channelName, false);
        dashboard.selectChannel(channelName);
    }

    @Test(priority = 2, dependsOnMethods = "testPrepareMessaging", description = "Send a text message")
    @Severity(SeverityLevel.BLOCKER)
    public void testSendMessage() {
        String message = "Automated Test Message: " + random.nextInt(1000);
        dashboard.sendMessage(message);
        
        assertThat(dashboard.getLastMessageText())
                .as("Last message should match the sent text")
                .isEqualTo(message);
    }

    @Test(priority = 3, dependsOnMethods = "testSendMessage", description = "Edit the last sent message")
    @Severity(SeverityLevel.NORMAL)
    public void testEditMessage() {
        String newMessage = "Edited Automated Message: " + random.nextInt(1000);
        dashboard.editLastMessage(newMessage);
        
        simulateThinking(2000, 3000); // Wait for edit to propagate
        
        assertThat(dashboard.getLastMessageText())
                .as("Last message should match the edited text")
                .isEqualTo(newMessage);
    }

    @Test(priority = 4, dependsOnMethods = "testEditMessage", description = "Delete the last sent message")
    @Severity(SeverityLevel.NORMAL)
    public void testDeleteMessage() {
        String textBeforeDelete = dashboard.getLastMessageText();
        dashboard.deleteLastMessage();
        
        simulateThinking(2000, 3000);
        
        String textAfterDelete = dashboard.getLastMessageText();
        assertThat(textAfterDelete)
                .as("The message should no longer be the last message")
                .isNotEqualTo(textBeforeDelete);
    }
}
