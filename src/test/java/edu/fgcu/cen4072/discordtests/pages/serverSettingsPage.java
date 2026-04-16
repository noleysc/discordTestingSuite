package edu.fgcu.cen4072.discordtests.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class ServerSettingsPage extends BasePage {

    public ServerSettingsPage(WebDriver driver) {
        super(driver);
    }

    public void createRole(String roleName) {
        logger.info("Starting role creation process...");
        
        // 1. Ensure we are on the Roles tab
        WebElement rolesTab = (WebElement) ((JavascriptExecutor) driver).executeScript(
            "return Array.from(document.querySelectorAll('[role=\"tab\"], [class*=\"item\"]'))" +
            ".find(el => el.textContent.toLowerCase().includes('roles') && el.offsetParent !== null);"
        );
        if (rolesTab != null) {
            clickHumanly(rolesTab);
            simulateThinking(2000, 3000);
        }

        // 2. Click Create Role button
        WebElement createBtn = (WebElement) ((JavascriptExecutor) driver).executeScript(
            "return Array.from(document.querySelectorAll('button'))" +
            ".find(el => (el.textContent.toLowerCase().includes('create role') || el.getAttribute('aria-label') === 'Create Role') && el.offsetParent !== null);"
        );
        
        if (createBtn != null) {
            clickHumanly(createBtn);
            simulateThinking(2000, 3000);
        } else {
            logger.warn("Create Role button not found directly, checking if we need to click 'Create Role' in empty state...");
            WebElement emptyCreate = (WebElement) ((JavascriptExecutor) driver).executeScript(
                "return Array.from(document.querySelectorAll('button')).find(el => el.textContent.toLowerCase().includes('create role'));"
            );
            if (emptyCreate != null) clickHumanly(emptyCreate);
        }

        // 3. Enter Role Name
        WebElement roleInput = (WebElement) ((JavascriptExecutor) driver).executeScript(
            "return document.querySelector('input[aria-label=\"Role name\"]') || " +
            "       document.querySelector('input[value=\"new role\"]') || " +
            "       Array.from(document.querySelectorAll('input')).find(el => el.value === 'new role' || el.placeholder === 'new role');"
        );

        if (roleInput != null) {
            clickHumanly(roleInput);
            roleInput.sendKeys(Keys.chord(getModifierKey(), "a"), Keys.BACK_SPACE);
            typeHumanly(roleInput, roleName);
            simulateThinking(2000, 3000);
        }

        saveChanges();
    }

    public void enableAdministratorPermission() {
        logger.info("Enabling Administrator permission...");
        
        // 1. Click Permissions tab in Role settings
        WebElement permissionsTab = null;
        for (int i = 0; i < 5; i++) {
            permissionsTab = (WebElement) ((JavascriptExecutor) driver).executeScript(
                "return Array.from(document.querySelectorAll('[role=\"tab\"], [class*=\"item\"]'))" +
                ".find(el => el.textContent.toLowerCase().includes('permissions') && el.offsetParent !== null);"
            );
            if (permissionsTab != null) break;
            simulateThinking(1000, 1500);
        }

        if (permissionsTab != null) {
            clickHumanly(permissionsTab);
            simulateThinking(2000, 3000);
        } else {
            logger.warn("Permissions tab not found directly, checking if already on it...");
        }

        // 2. Search for 'Administrator'
        WebElement searchInput = null;
        for (int i = 0; i < 5; i++) {
            searchInput = (WebElement) ((JavascriptExecutor) driver).executeScript(
                "return document.querySelector('input[placeholder*=\"Search permissions\"]') || " +
                "       document.querySelector('input[aria-label*=\"Search permissions\"]') || " +
                "       Array.from(document.querySelectorAll('input')).find(el => el.placeholder.toLowerCase().includes('search') && el.offsetParent !== null);"
            );
            if (searchInput != null) break;
            simulateThinking(1000, 1500);
        }

        if (searchInput != null) {
            clickHumanly(searchInput);
            typeHumanly(searchInput, "Administrator");
            simulateThinking(2000, 3000);
        }

        // 3. Toggle the Administrator switch
        WebElement adminToggle = null;
        for (int i = 0; i < 5; i++) {
            adminToggle = (WebElement) ((JavascriptExecutor) driver).executeScript(
                "const adminText = Array.from(document.querySelectorAll('span, div'))" +
                ".find(el => el.textContent.trim() === 'Administrator' && el.offsetParent !== null);" +
                "if (!adminText) return null;" +
                "// Look for the toggle in the row that contains the 'Administrator' text" +
                "const row = adminText.closest('[class*=\"row\"], [class*=\"container\"], [class*=\"item\"]');" +
                "return row ? row.querySelector('[role=\"switch\"], input[type=\"checkbox\"]') : null;"
            );
            if (adminToggle != null) break;
            simulateThinking(1000, 1500);
        }
        
        if (adminToggle != null) {
            String state = adminToggle.getAttribute("aria-checked");
            if ("false".equals(state) || state == null) {
                logger.info("Clicking Administrator toggle...");
                clickHumanly(adminToggle);
                simulateThinking(2000, 3000);
            } else {
                logger.info("Administrator permission already enabled (state: {}).", state);
            }
        } else {
            logger.error("Administrator toggle NOT found!");
        }

        saveChanges();
    }

    public void saveChanges() {
        logger.info("Saving changes in settings...");
        WebElement saveBtn = (WebElement) ((JavascriptExecutor) driver).executeScript(
            "return Array.from(document.querySelectorAll('button')).find(el => el.textContent.toLowerCase().includes('save changes') && !el.disabled);"
        );
        if (saveBtn != null) {
            clickHumanly(saveBtn);
            simulateThinking(2000, 3000);
        }
    }

    public void deleteServer(String serverName) {
        logger.info("Commencing server deletion for: {}", serverName);
        
        // 1. Find Delete Server tab in sidebar
        WebElement delTab = (WebElement) ((JavascriptExecutor) driver).executeScript(
            "const sidebar = document.querySelector('[class*=\"sidebar\"]');" +
            "return Array.from(sidebar.querySelectorAll('[role=\"tab\"], [class*=\"item\"]'))" +
            ".find(el => el.textContent.toLowerCase().includes('delete server'));"
        );
        
        if (delTab != null) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", delTab);
            clickHumanly(delTab);
            simulateThinking(2000, 3000);

            // 2. Handle Confirmation Modal
            WebElement nameInput = (WebElement) ((JavascriptExecutor) driver).executeScript(
                "return document.querySelector('div[role=\"dialog\"] input[type=\"text\"]') || " +
                "       document.querySelector('input[aria-label*=\"enter server name\" i]');"
            );
            
            if (nameInput != null) {
                clickHumanly(nameInput);
                typeHumanly(nameInput, serverName);
                simulateThinking(2000, 3000);
                
                WebElement confirmBtn = (WebElement) ((JavascriptExecutor) driver).executeScript(
                    "return Array.from(document.querySelectorAll('button')).find(el => el.textContent.toLowerCase().includes('delete server') && el.offsetParent !== null);"
                );
                if (confirmBtn != null) {
                    clickHumanly(confirmBtn);
                    simulateThinking(2000, 3000);
                }
            }
        }
    }
}
