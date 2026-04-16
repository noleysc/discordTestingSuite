package edu.fgcu.cen4072.discordtests;

import io.qameta.allure.*;
import edu.fgcu.cen4072.discordtests.pages.DashboardPage;
import edu.fgcu.cen4072.discordtests.pages.LoginPage;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("Messaging")
@Feature("Direct Messaging")
public class MessagingTests extends BaseTest {
    private final String loginIdentifier = "softwaretesting@tutamail.com";
    private final String loginPassword = "testPASS!@#";
    private DashboardPage dashboard;
    private String channelName;
    private final Random random = new Random();

    @BeforeClass
    public void prepareMessaging() {
        if (dashboard == null) {
            dashboard = new DashboardPage(getDriver());
        }
        
        try {
            if (!dashboard.isHydrated()) {
                dashboard.ensureHydrated();
            }
        } catch (Exception e) {
            logger.info("Dashboard not hydrated, ensuring session is active.");
            dashboard.ensureHydrated();
        }
        
        channelName = "test-chat-" + (random.nextInt(899) + 100);
        logger.info("MessagingTests prepared with channel: {}", channelName);
    }

    @Test(priority = 1, description = "Send a message in a channel")
    @Severity(SeverityLevel.CRITICAL)
    public void testSendMessage() {
        dashboard.openFriendsTab();
        String message = "Automated test message " + random.nextInt(1000);
        dashboard.sendMessage(message);
        
        assertThat(dashboard.getLastMessageText())
                .as("Last message text should match sent text")
                .isEqualTo(message);
    }

    @Test(priority = 2, dependsOnMethods = "testSendMessage", description = "Edit the last sent message")
    @Severity(SeverityLevel.NORMAL)
    public void testEditMessage() {
        String updatedMessage = "Updated automated message " + random.nextInt(1000);
        dashboard.editLastMessage(updatedMessage);
        
        assertThat(dashboard.getLastMessageText())
                .as("Last message text should match updated text")
                .isEqualTo(updatedMessage);
    }

    @Test(priority = 3, dependsOnMethods = "testEditMessage", description = "Delete the last sent message")
    @Severity(SeverityLevel.NORMAL)
    public void testDeleteMessage() {
        String textBeforeDelete = dashboard.getLastMessageText();
        dashboard.deleteLastMessage();
       
        String textAfterDelete = dashboard.getLastMessageText();
        assertThat(textAfterDelete)
                .as("The message should no longer be the last message")
                .isNotEqualTo(textBeforeDelete);
    }
}
