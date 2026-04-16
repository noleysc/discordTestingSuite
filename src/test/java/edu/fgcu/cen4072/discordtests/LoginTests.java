package edu.fgcu.cen4072.discordtests;

import io.qameta.allure.*;
import edu.fgcu.cen4072.discordtests.pages.LandingPage;
import edu.fgcu.cen4072.discordtests.pages.LoginPage;
import org.testng.annotations.Test;
import static org.assertj.core.api.Assertions.assertThat;

@Epic("Authentication")
@Feature("Login functionality")
public class LoginTests extends BaseTest {

    private final String loginIdentifier = "softwaretesting@tutamail.com";
    private final String loginPassword = "testPASS!@#";

    @Test(priority = 1, description = "Login to Discord with valid credentials")
    @Severity(SeverityLevel.BLOCKER)
    public void testValidLogin() {
        if (getDriver().getCurrentUrl().contains("channels/@me")) {
            logger.info("Session already active, skipping login test.");
            return;
        }
        
        getDriver().get("https://discord.com/login");
        LoginPage login = new LoginPage(getDriver());
        login.login(loginIdentifier, loginPassword);

        assertThat(login.isDashboardLoaded())
                .as("Discord dashboard should be loaded after login")
                .isTrue();
    }
}
