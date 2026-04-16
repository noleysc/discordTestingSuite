package edu.fgcu.cen4072.discordtests.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class LoginPage extends BasePage {

    @FindBy(name = "email")
    private WebElement emailInput;

    @FindBy(name = "password")
    private WebElement passwordInput;

    @FindBy(css = "button[type='submit']")
    private WebElement loginButton;

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public void login(String identifier, String password) {
        logger.info("Forcing login flow...");

        logger.info("Waiting for login page to load...");
        try {
            waitForVisible(By.name("email"));
        } catch (Exception e) {
            logger.error("Login fields not found! URL: " + driver.getCurrentUrl());
            saveScreenshot(driver);
            throw e;
        }

        logger.info("Entering credentials...");
        clickHumanly(emailInput);
        emailInput.sendKeys(org.openqa.selenium.Keys.chord(getModifierKey(), "a"), org.openqa.selenium.Keys.BACK_SPACE);
        typeHumanly(emailInput, identifier);

        clickHumanly(passwordInput);
        passwordInput.sendKeys(org.openqa.selenium.Keys.chord(getModifierKey(), "a"), org.openqa.selenium.Keys.BACK_SPACE);
        typeHumanly(passwordInput, password);

        logger.info("Pressing ENTER to submit login...");
        passwordInput.sendKeys(org.openqa.selenium.Keys.ENTER);

        // Rate limit check
        try {
            boolean rateLimited = (Boolean) ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "return document.body.innerText.toLowerCase().includes('rate limited');"
            );
            if (rateLimited) {
                logger.warn("!!! CRITICAL: Rate limit detected !!!");
                logger.warn("Waiting 60 seconds for rate limit to cool down...");
                try { Thread.sleep(60000); } catch (InterruptedException ignore) {}
                passwordInput.sendKeys(org.openqa.selenium.Keys.ENTER);
            }
        } catch (Exception ignore) {}
    }

    public boolean isDashboardLoaded() {
        try {
            return wait.until(ExpectedConditions.urlContains("channels/@me"));
        } catch (TimeoutException e) {
            return false;
        }
    }
}
