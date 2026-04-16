package org.example;

import io.qameta.allure.*;
import org.example.pages.landingPage;
import org.example.pages.loginPage;
import org.testng.annotations.Test;
import static org.assertj.core.api.Assertions.assertThat;

@Epic("Authentication")
@Feature("Login functionality")
public class loginTests extends baseTest {
    private final String loginIdentifier = "softwaretesting@tutamail.com";
    private final String loginPassword = "testPASS!@#";

    @Test(description = "User should be able to login successfully via landing page")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that a user can navigate from the landing page to the login page and authenticate successfully.")
    public void testSuccessfulLogin() {
        landingPage landing = new landingPage(getDriver());
        landing.goTo();

        loginPage login = landing.clickLogin();
        login.login(loginIdentifier, loginPassword);

        assertThat(login.isDashboardLoaded())
                .as("Discord dashboard should be loaded after login")
                .isTrue();
    }
}
