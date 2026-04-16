package org.example.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class landingPage extends basePage {

    @FindBy(css = "a[href*='login'], [class*='loginButton'], [class*='login-button']")
    private WebElement loginButton;

    public landingPage(WebDriver driver) {
        super(driver);
    }

    public void goTo() {
        driver.get("https://discord.com");
        waitForPageLoad();
        simulateThinking(100, 300);
    }

    public loginPage clickLogin() {
        if (!driver.getCurrentUrl().contains("/login")) {
            waitForClickable(loginButton);
            clickHumanly(loginButton);
        }
        return new loginPage(driver);
    }
}
