package org.example.pages;

import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class loginPage extends basePage {

    @FindBy(name = "email")
    private WebElement emailInput;

    @FindBy(name = "password")
    private WebElement passwordInput;

    @FindBy(css = "button[type='submit']")
    private WebElement submitButton;

    public loginPage(WebDriver driver) {
        super(driver);
    }

    public void login(String email, String password) {
        if (driver.getCurrentUrl().contains("channels/@me")) {
            logger.info("Already logged in, skipping login steps.");
            return;
        }
        
        clearOverlays();
        
        // Quick check if we get redirected instantly
        try {
            new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(3))
                .until(org.openqa.selenium.support.ui.ExpectedConditions.urlContains("channels/@me"));
            logger.info("Fast redirect to dashboard detected, skipping login.");
            return;
        } catch (Exception ignore) {}

        try {
            waitForClickable(emailInput);
        } catch (TimeoutException e) {
            if (driver.getCurrentUrl().contains("channels/@me")) {
                logger.info("Dashboard loaded during wait, skipping login.");
                return;
            }
            throw e;
        }
        
        clickHumanly(emailInput);
        typeHumanly(emailInput, email);

        clickHumanly(passwordInput);
        typeHumanly(passwordInput, password);
        
        logger.info("Pressing ENTER to submit login...");
        passwordInput.sendKeys(org.openqa.selenium.Keys.ENTER);
    }

    public boolean isDashboardLoaded() {
        return wait.until(ExpectedConditions.urlContains("channels/@me"));
    }
}
