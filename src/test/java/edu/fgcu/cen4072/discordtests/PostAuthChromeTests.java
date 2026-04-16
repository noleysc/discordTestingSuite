package edu.fgcu.cen4072.discordtests;

import io.qameta.allure.*;
import edu.fgcu.cen4072.discordtests.pages.LoginPage;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("Authentication")

@Feature("Negative Login functionality")
public class PostAuthChromeTests extends BaseTest {

    @Test(priority = 1, description = "Test login with invalid password")
    @Severity(SeverityLevel.NORMAL)
    public void testInvalidPassword() {
        getDriver().get("https://discord.com/login");
        LoginPage login = new LoginPage(getDriver());
        
        login.login("softwaretesting@tutamail.com", "WRONG_PASSWORD");

        // Wait for error message
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
        boolean errorVisible = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//div[contains(text(), \"didn't work\") or contains(@class, 'error') or contains(@id, 'error')]"))).isDisplayed();

        assertThat(errorVisible)
                .as("Error message should be visible for invalid login")
                .isTrue();
    }
}
