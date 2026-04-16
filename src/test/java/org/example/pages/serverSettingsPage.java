package org.example.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class serverSettingsPage extends basePage {

    public serverSettingsPage(WebDriver driver) {
        super(driver);
    }

    public void createRole(String name) {
        logger.info("Step 1: Clicking 'Create Role'...");
        
        boolean rolesClicked = false;
        for (int i = 0; i < 10; i++) {
            try {
                WebElement rolesTab = (WebElement) ((JavascriptExecutor) driver).executeScript(
                    "return Array.from(document.querySelectorAll('[role=\"tab\"], div[class*=\"item\"]'))" +
                    ".find(el => el.textContent.toLowerCase().includes('roles') && !el.textContent.toLowerCase().includes('permissions') && el.offsetParent !== null);"
                );
                if (rolesTab != null) {
                    clickHumanly(rolesTab);
                    rolesClicked = true;
                    break;
                }
            } catch (Exception e) {
                logger.warn("Roles tab click retry... " + e.getMessage());
            }
            simulateThinking(500, 1000);
        }
        if (!rolesClicked) throw new NoSuchElementException("Roles tab not found or click failed.");
        
        simulateThinking(1500, 2500);

        boolean createClicked = false;
        for (int i = 0; i < 10; i++) {
            try {
                WebElement createBtn = (WebElement) ((JavascriptExecutor) driver).executeScript(
                    "return Array.from(document.querySelectorAll('button, [role=\"button\"], div[class*=\"button\"]'))" +
                    ".find(el => el.textContent.toLowerCase().includes('create role') || el.getAttribute('aria-label') === 'Create Role' || " +
                    "            (el.className.includes('lookFilled') && el.className.includes('colorBrand') && el.textContent.includes('Create Role')));"
                );
                if (createBtn != null) {
                    clickHumanly(createBtn);
                    createClicked = true;
                    break;
                }
            } catch (Exception e) {}
            simulateThinking(500, 1000);
        }
        if (!createClicked) throw new NoSuchElementException("Create Role button not found or click failed.");
        
        simulateThinking(1500, 2500);

        logger.info("Step 2 & 3: Inputting role name into text box underneath 'Role name'...");
        boolean inputFound = false;
        for (int i = 0; i < 10; i++) {
            try {
                WebElement roleInput = (WebElement) ((JavascriptExecutor) driver).executeScript(
                    "return Array.from(document.querySelectorAll('input[type=\"text\"]'))" +
                    ".find(el => el.getAttribute('aria-label') === 'Role name' || " +
                    "            el.placeholder === 'Role Name' || " +
                    "            (el.parentElement && el.parentElement.textContent.toLowerCase().includes('role name')) || " +
                    "            el.className.includes('input'));"
                );
                if (roleInput != null) {
                    Keys modifier = System.getProperty("os.name").toLowerCase().contains("mac") ? Keys.COMMAND : Keys.CONTROL;
                    clickHumanly(roleInput);
                    roleInput.sendKeys(Keys.chord(modifier, "a"), Keys.BACK_SPACE);
                    typeHumanly(roleInput, name);
                    inputFound = true;
                    break;
                }
            } catch (Exception e) {}
            simulateThinking(500, 1000);
        }
        if (!inputFound) throw new NoSuchElementException("Role name input field not found or interact failed.");
        
        simulateThinking(500, 800);
    }

    public void enableAdministratorPermission() {
        logger.info("Step 4: Clicking 'Permissions' tab...");
        boolean permsClicked = false;
        for (int i = 0; i < 10; i++) {
            try {
                WebElement permsTab = (WebElement) ((JavascriptExecutor) driver).executeScript(
                    "return Array.from(document.querySelectorAll('[role=\"tab\"], div[class*=\"item\"]'))" +
                    ".find(el => el.textContent.toLowerCase().trim() === 'permissions');"
                );
                if (permsTab != null) {
                    clickHumanly(permsTab);
                    permsClicked = true;
                    break;
                }
            } catch (Exception e) {}
            simulateThinking(500, 1000);
        }
        if (!permsClicked) throw new NoSuchElementException("Permissions tab not found or click failed.");
        
        simulateThinking(1500, 2500);

        logger.info("Step 5 & 6: Typing 'Administrator' in search bar...");
        boolean searchTyped = false;
        for (int i = 0; i < 10; i++) {
            try {
                WebElement searchInput = (WebElement) ((JavascriptExecutor) driver).executeScript(
                    "return document.querySelector('input[placeholder*=\"Search permissions\" i]') || " +
                    "       document.querySelector('input[aria-label*=\"Search permissions\" i]');"
                );
                if (searchInput != null) {
                    clickHumanly(searchInput);
                    typeHumanly(searchInput, "Administrator");
                    searchTyped = true;
                    break;
                }
            } catch (Exception e) {}
            simulateThinking(500, 1000);
        }
        if (!searchTyped) logger.warn("Permission search input not found, attempting to find toggle directly");
        
        simulateThinking(800, 1200);

        logger.info("Step 7: Clicking the role 'Administrator'...");
        boolean toggleClicked = false;
        for (int i = 0; i < 10; i++) {
            try {
                WebElement adminToggle = (WebElement) ((JavascriptExecutor) driver).executeScript(
                    "return Array.from(document.querySelectorAll('[class*=\"row\"], [class*=\"switchItem\"], div[class*=\"container\"]'))" +
                    ".filter(el => el.textContent.includes('Administrator') || el.textContent.includes('administrator'))" +
                    ".map(el => el.querySelector('[role=\"switch\"], input[type=\"checkbox\"], div[class*=\"control\"]'))" +
                    ".find(el => el != null);"
                );
                if (adminToggle != null) {
                    clickHumanly(adminToggle);
                    toggleClicked = true;
                    break;
                }
                ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, window.innerHeight / 2);");
            } catch (Exception e) {}
            simulateThinking(500, 1000);
        }
        if (!toggleClicked) throw new NoSuchElementException("Administrator permission toggle not found or click failed.");
        
        simulateThinking(1000, 1500);

        logger.info("Saving changes...");
        for (int i = 0; i < 10; i++) {
            try {
                WebElement save = (WebElement) ((JavascriptExecutor) driver).executeScript(
                    "return Array.from(document.querySelectorAll('button'))" +
                    ".find(el => el.textContent.toLowerCase().includes('save changes') && !el.disabled);"
                );
                if (save != null) {
                    clickHumanly(save);
                    break;
                }
            } catch (Exception e) {}
            simulateThinking(500, 1000);
        }
        simulateThinking(1000, 1500);

        logger.info("Exiting server settings menu...");
        driver.findElement(By.tagName("body")).sendKeys(Keys.ESCAPE);
        simulateThinking(1000, 2000);
    }

    public void deleteServer(String serverName) {
        logger.info("Opening Delete Server tab...");
        boolean delClicked = false;
        for (int i = 0; i < 10; i++) {
            try {
                WebElement delTab = (WebElement) ((JavascriptExecutor) driver).executeScript(
                    "return Array.from(document.querySelectorAll('[role=\"tab\"], [class*=\"item\"]'))" +
                    ".find(el => el.textContent.toLowerCase().includes('delete server'));"
                );
                if (delTab != null) {
                    clickHumanly(delTab);
                    delClicked = true;
                    break;
                }
                ((JavascriptExecutor) driver).executeScript("document.querySelector('div[class*=\"sidebar\"]').scrollTop += 200;");
            } catch (Exception e) {}
            simulateThinking(500, 1000);
        }
        if (!delClicked) throw new NoSuchElementException("Delete Server tab not found or click failed.");
        
        simulateThinking(1000, 2000);

        logger.info("Entering server name for confirmation...");
        boolean nameTyped = false;
        for (int i = 0; i < 10; i++) {
            try {
                WebElement confirmInput = (WebElement) ((JavascriptExecutor) driver).executeScript(
                    "return Array.from(document.querySelectorAll('input'))" +
                    ".find(el => el.parentElement && el.parentElement.textContent.toLowerCase().includes('enter server name')) || " +
                    "document.querySelector('div[class*=\"modal\"] input');"
                );
                if (confirmInput != null) {
                    clickHumanly(confirmInput);
                    typeHumanly(confirmInput, serverName);
                    nameTyped = true;
                    break;
                }
            } catch (Exception e) {}
            simulateThinking(500, 1000);
        }
        if (!nameTyped) throw new NoSuchElementException("Delete confirmation input not found or interact failed.");

        logger.info("Confirming deletion...");
        boolean confirmClicked = false;
        for (int i = 0; i < 10; i++) {
            try {
                WebElement delBtn = (WebElement) ((JavascriptExecutor) driver).executeScript(
                    "return Array.from(document.querySelectorAll('button'))" +
                    ".find(el => el.textContent.toLowerCase().includes('delete server') && (el.getAttribute('type') === 'submit' || el.className.includes('colorRed')));"
                );
                if (delBtn != null) {
                    clickHumanly(delBtn);
                    confirmClicked = true;
                    break;
                }
            } catch (Exception e) {}
            simulateThinking(500, 1000);
        }
        if (!confirmClicked) throw new NoSuchElementException("Final delete button not found or click failed.");
        
        try {
            wait.until(ExpectedConditions.urlContains("channels/@me"));
        } catch (Exception e) {
            logger.warn("Wait for url to contain channels/@me failed. " + e.getMessage());
        }
    }
}
