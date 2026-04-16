package org.example;

import io.qameta.allure.*;
import org.example.pages.loginPage;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("Authentication")
@Feature("Negative Login functionality")
public class PostAuthChromeTests extends baseTest {

    @Test(description = "Post-Provisioning Negative Test: Invalid Password in Chrome")
    @Severity(SeverityLevel.MINOR)
    public void testInvalidPasswordAfterSuccess() {
        getDriver().get("https://discord.com/login");
        loginPage login = new loginPage(getDriver());
        login.login("softwaretesting@tutamail.com", "Architect_Access_Denied_999");

        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
        boolean errorVisible = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(text(), \"didn't work\") or contains(@class, 'error') or contains(@id, 'error')]"))).isDisplayed();

        assertThat(errorVisible)
                .as("Error message should be visible for invalid login")
                .isTrue();
    }
}
