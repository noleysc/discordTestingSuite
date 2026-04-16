package edu.fgcu.cen4072.discordtests.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class LandingPage extends BasePage {

    @FindBy(xpath = "//a[contains(@href, 'login')]")
    private WebElement loginButton;

    public LandingPage(WebDriver driver) {
        super(driver);
    }

    public LoginPage clickLogin() {
        logger.info("Navigating to login page from landing...");
        clickHumanly(loginButton);
        return new LoginPage(driver);
    }
}
