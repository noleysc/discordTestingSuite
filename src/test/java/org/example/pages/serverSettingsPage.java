package org.example.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class serverSettingsPage extends basePage {

    public serverSettingsPage(WebDriver driver) {
        super(driver);
    }

    public void createRole(String name) {
        clearOverlays();
        logger.info("Step 1: Clicking 'Create Role'...");
        WebElement rolesTab = (WebElement) ((JavascriptExecutor) driver).executeScript(
            "return Array.from(document.querySelectorAll('[role=\"tab\"], [class*=\"item\"]'))" +
            ".find(el => el.textContent.trim().toLowerCase() === 'roles' || " +
            "            (el.getAttribute('data-list-item-id') && el.getAttribute('data-list-item-id').includes('roles')));"
        );
        if (rolesTab == null) throw new NoSuchElementException("Roles tab not found in sidebar");
        clickHumanly(rolesTab);
        simulateThinking(500, 800);

        WebElement createBtn = (WebElement) ((JavascriptExecutor) driver).executeScript(
            "return Array.from(document.querySelectorAll('button, [role=\"button\"]'))" +
            ".find(el => el.textContent.toLowerCase().includes('create role') || " +
            "            el.className.includes('lookFilled') && el.className.includes('colorBrand'));"
        );
        if (createBtn == null) throw new NoSuchElementException("Create Role button not found");
        clickHumanly(createBtn);
        simulateThinking(800, 1200);

        logger.info("Step 2 & 3: Inputting role name into text box underneath 'Role name'...");
        WebElement roleInput = (WebElement) ((JavascriptExecutor) driver).executeScript(
            "return Array.from(document.querySelectorAll('input[type=\"text\"]'))" +
            ".find(el => el.getAttribute('aria-label') === 'Role name' || " +
            "            el.placeholder === 'Role Name' || " +
            "            (el.parentElement.textContent.toLowerCase().includes('role name')));"
        );
        if (roleInput == null) throw new NoSuchElementException("Role name input field not found");
        
        clickHumanly(roleInput);
        roleInput.sendKeys(Keys.chord(Keys.COMMAND, "a"), Keys.BACK_SPACE);
        typeHumanly(roleInput, name);
        simulateThinking(500, 800);
    }

    public void enableAdministratorPermission() {
        clearOverlays();
        logger.info("Step 4: Clicking 'Permissions' tab...");
        WebElement permsTab = (WebElement) ((JavascriptExecutor) driver).executeScript(
            "return Array.from(document.querySelectorAll('[role=\"tab\"]'))" +
            ".find(el => el.textContent.toLowerCase().includes('permissions'));"
        );
        if (permsTab == null) throw new NoSuchElementException("Permissions tab not found");
        clickHumanly(permsTab);
        simulateThinking(500, 800);

        logger.info("Step 5 & 6: Typing 'Administrator' in search bar...");
        WebElement searchInput = (WebElement) ((JavascriptExecutor) driver).executeScript(
            "return document.querySelector('input[placeholder*=\"Search permissions\"]') || " +
            "       document.querySelector('input[aria-label*=\"Search permissions\"]');"
        );
        if (searchInput == null) throw new NoSuchElementException("Permission search input not found");
        
        clickHumanly(searchInput);
        typeHumanly(searchInput, "Administrator");
        simulateThinking(800, 1200);

        logger.info("Step 7: Clicking the role 'Administrator'...");
        WebElement adminToggle = (WebElement) ((JavascriptExecutor) driver).executeScript(
            "return Array.from(document.querySelectorAll('[class*=\"row\"], [class*=\"switchItem\"]'))" +
            ".find(el => el.textContent.includes('Administrator'))" +
            ".querySelector('[role=\"switch\"], input[type=\"checkbox\"], div');"
        );
        if (adminToggle == null) throw new NoSuchElementException("Administrator permission toggle not found");
        
        clickHumanly(adminToggle);
        simulateThinking(500, 800);

        logger.info("Saving changes...");
        WebElement save = (WebElement) ((JavascriptExecutor) driver).executeScript(
            "return Array.from(document.querySelectorAll('button'))" +
            ".find(el => el.textContent.toLowerCase().includes('save changes'));"
        );
        if (save != null) {
            clickHumanly(save);
            simulateThinking(1000, 1500);
        }
    }

    public void deleteServer(String serverName) {
        logger.info("Opening Delete Server tab...");
        WebElement delTab = (WebElement) ((JavascriptExecutor) driver).executeScript(
            "return Array.from(document.querySelectorAll('[role=\"tab\"], [class*=\"item\"]'))" +
            ".find(el => el.textContent.toLowerCase().includes('delete server'));"
        );
        if (delTab == null) throw new NoSuchElementException("Delete Server tab not found");
        clickHumanly(delTab);
        simulateThinking(300, 700);

        logger.info("Entering server name for confirmation...");
        WebElement confirmInput = (WebElement) ((JavascriptExecutor) driver).executeScript(
            "return Array.from(document.querySelectorAll('input'))" +
            ".find(el => el.parentElement.textContent.toLowerCase().includes('enter server name'));"
        );
        if (confirmInput == null) throw new NoSuchElementException("Delete confirmation input not found");
        typeHumanly(confirmInput, serverName);

        logger.info("Confirming deletion...");
        WebElement delBtn = (WebElement) ((JavascriptExecutor) driver).executeScript(
            "return Array.from(document.querySelectorAll('button'))" +
            ".find(el => el.textContent.toLowerCase().includes('delete server') && el.getAttribute('type') === 'submit');"
        );
        if (delBtn == null) throw new NoSuchElementException("Final delete button not found");
        clickHumanly(delBtn);
        wait.until(ExpectedConditions.urlContains("channels/@me"));
    }
}
