package edu.fgcu.cen4072.discordtests.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class UserSettingsPage extends BasePage {

    public UserSettingsPage(WebDriver driver) {
        super(driver);
        simulateThinking(2000, 3000);
        try {
            new WebDriverWait(driver, Duration.ofSeconds(30)).until(d -> (Boolean) ((JavascriptExecutor) d).executeScript(
                "const text = document.body.innerText.toLowerCase();" +
                "return text.includes('my account') || text.includes('profiles') || " +
                "       text.includes('user settings') || document.querySelectorAll('[role=\"tablist\"]').length > 0;"
            ));
        } catch (Exception e) {
            logger.warn("User Settings hydration check timed out or failed: {}", e.getMessage());
        }
    }

    public void editProfile(String newName, String pronouns, String avatarPath) {
        logger.info("Starting robust profile edit flow...");
        boolean changed = false;
        
        // Ensure we are on the Profiles tab
        WebElement profilesTab = (WebElement) ((JavascriptExecutor) driver).executeScript(
            "return Array.from(document.querySelectorAll('[role=\"tab\"], [class*=\"item\"]'))" +
            ".find(el => el.textContent.toLowerCase().includes('profiles') && el.offsetParent !== null);"
        );
        if (profilesTab != null) {
            clickHumanly(profilesTab);
            simulateThinking(2000, 3000);
        }

        WebElement editBtn = (WebElement) ((JavascriptExecutor) driver).executeScript(
            "return Array.from(document.querySelectorAll('button')).find(el => {" +
            "  const text = el.textContent.toLowerCase();" +
            "  return (text.includes('edit user profile') || " +
            "          text.includes('edit profile') || " +
            "          el.getAttribute('aria-label') === 'Edit User Profile') && " +
            "          el.offsetParent !== null && !el.disabled;" +
            "});"
        );
        if (editBtn != null) {
            logger.info("Found 'Edit Profile' button, clicking...");
            clickHumanly(editBtn);
            simulateThinking(2000, 3000);
        } else {
            logger.info("No 'Edit Profile' button found, checking if already on edit screen...");
        }

        // 1. Edit Name
        if (newName != null) {
            String currentName = getInputValue("Display Name");
            if (newName.equals(currentName)) {
                logger.info("Display Name already set to {}, skipping.", newName);
            } else {
                logger.info("Editing Display Name: {} (Current: {})", newName, currentName);
                WebElement nameInput = null;
                for (int i = 0; i < 10; i++) {
                    nameInput = (WebElement) ((JavascriptExecutor) driver).executeScript(
                        "return document.querySelector('input[name=\"global_name\"]') || " +
                        "       document.querySelector('input[name=\"display_name\"]') || " +
                        "       document.querySelector('input[class*=\"input__\"]') || " +
                        "       Array.from(document.querySelectorAll('input')).find(el => {" +
                        "         const aria = el.getAttribute('aria-label') || '';" +
                        "         const placeholder = el.placeholder || '';" +
                        "         return (aria.toLowerCase().includes('display name') || " +
                        "                 placeholder.toLowerCase().includes('display name')) && " +
                        "                 el.offsetParent !== null;" +
                        "       });"
                    );
                    if (nameInput != null) break;
                    simulateThinking(2000, 3000);
                }

                if (nameInput != null) {
                    clickHumanly(nameInput);
                    nameInput.sendKeys(Keys.chord(getModifierKey(), "a"), Keys.BACK_SPACE);
                    typeHumanly(nameInput, newName);
                    ((JavascriptExecutor) driver).executeScript("arguments[0].dispatchEvent(new Event('input', { bubbles: true }));", nameInput);
                    simulateThinking(2000, 3000);
                    changed = true;
                } else {
                    logger.error("Display Name input NOT found after waiting.");
                }
            }
        }

        // 2. Edit Pronouns
        if (pronouns != null) {
            String currentPronouns = getInputValue("Pronouns");
            if (pronouns.equals(currentPronouns)) {
                logger.info("Pronouns already set to {}, skipping.", pronouns);
            } else {
                logger.info("Editing Pronouns: {} (Current: {})", pronouns, currentPronouns);
                WebElement pronInput = null;
                for (int i = 0; i < 5; i++) {
                    pronInput = (WebElement) ((JavascriptExecutor) driver).executeScript(
                        "return document.querySelector('input[name=\"pronouns\"]') || " +
                        "       Array.from(document.querySelectorAll('input, [role=\"textbox\"]')).find(el => {" +
                        "         const aria = el.getAttribute('aria-label') || '';" +
                        "         const placeholder = el.placeholder || '';" +
                        "         const container = el.closest('div[class*=\"container\"]') || el.parentElement;" +
                        "         return ((container && container.innerText.toLowerCase().includes('pronouns')) || " +
                        "                 aria.toLowerCase().includes('pronouns') || " +
                        "                 placeholder.toLowerCase().includes('pronouns')) && " +
                        "                 el.offsetParent !== null;" +
                        "       });"
                    );
                    if (pronInput != null) break;
                    simulateThinking(2000, 3000);
                }

                if (pronInput != null) {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", pronInput);
                    clickHumanly(pronInput);
                    pronInput.sendKeys(Keys.chord(getModifierKey(), "a"), Keys.BACK_SPACE);
                    typeHumanly(pronInput, pronouns);
                    ((JavascriptExecutor) driver).executeScript("arguments[0].dispatchEvent(new Event('input', { bubbles: true }));", pronInput);
                    simulateThinking(2000, 3000);
                    changed = true;
                } else {
                    logger.error("Pronouns input NOT found after waiting.");
                }
            }
        }

        if (avatarPath != null) {
            uploadAvatar(avatarPath);
            changed = true;
        }

        if (changed) {
            saveChanges();
        } else {
            logger.info("No changes detected, skipping save.");
        }
    }

    public void uploadAvatar(String filePath) {
        if (filePath == null) return;
        logger.info("Uploading avatar from: {}", filePath);
        
        WebElement changeAvatarBtn = null;
        for (int i = 0; i < 5; i++) {
            changeAvatarBtn = (WebElement) ((JavascriptExecutor) driver).executeScript(
                "return Array.from(document.querySelectorAll('button, [role=\"button\"]'))" +
                ".find(el => (el.textContent.toLowerCase().includes('change avatar') || el.textContent.toLowerCase().includes('upload avatar')) && el.offsetParent !== null);"
            );
            if (changeAvatarBtn != null) break;
            simulateThinking(2000, 3000);
        }

        if (changeAvatarBtn != null) {
            clickHumanly(changeAvatarBtn);
            simulateThinking(2000, 3000);

            WebElement uploadMenuOption = (WebElement) ((JavascriptExecutor) driver).executeScript(
                "return Array.from(document.querySelectorAll('[class*=\"item\"], [role=\"menuitem\"], button'))" +
                ".find(el => el.textContent.toLowerCase().includes('upload image') && el.offsetParent !== null);"
            );
            if (uploadMenuOption != null) {
                clickHumanly(uploadMenuOption);
                simulateThinking(2000, 3000);
            }

            WebElement fileInput = (WebElement) ((JavascriptExecutor) driver).executeScript(
                "return document.querySelector('input[type=\"file\"]');"
            );

            if (fileInput != null) {
                fileInput.sendKeys(filePath);
                logger.info("File path sent to input. Waiting for cropper...");
                simulateThinking(2000, 3000);
                
                WebElement applyBtn = (WebElement) ((JavascriptExecutor) driver).executeScript(
                    "return Array.from(document.querySelectorAll('button'))" +
                    ".find(el => (el.textContent.toLowerCase().includes('apply') || el.textContent.toLowerCase().includes('skip')) && el.offsetParent !== null);"
                );
                if (applyBtn != null) {
                    clickHumanly(applyBtn);
                    simulateThinking(2000, 3000);
                }
                clearOverlays();
            }
        } else {
            logger.error("Change Avatar button NOT found.");
        }
    }

    public void saveChanges() {
        logger.info("Attempting to save changes...");
        simulateThinking(2000, 3000);
        
        WebElement saveBtn = null;
        for (int i = 0; i < 10; i++) {
            // Check for rate limit error message on the page
            try {
                String errorText = (String) ((JavascriptExecutor) driver).executeScript(
                    "const err = Array.from(document.querySelectorAll('[class*=\"errorMessage\"], [role=\"alert\"]'))" +
                    ".find(el => el.textContent.toLowerCase().includes('too fast') || el.textContent.toLowerCase().includes('later'));" +
                    "return err ? err.textContent : null;"
                );
                if (errorText != null) {
                    logger.warn("!!! Profile Update Rate Limit Detected: {} !!!", errorText);
                    logger.warn("Waiting 60 seconds to attempt recovery...");
                    try { Thread.sleep(60000); } catch (InterruptedException ignore) {}
                }
            } catch (Exception ignore) {}

            saveBtn = (WebElement) ((JavascriptExecutor) driver).executeScript(
                "return Array.from(document.querySelectorAll('button')).find(el => " +
                "  (el.textContent.toLowerCase().includes('save changes') || " +
                "   el.closest('[class*=\"noticeRegion\"]')) && " +
                "  el.offsetParent !== null && !el.disabled);"
            );
            
            if (saveBtn == null) {
                saveBtn = (WebElement) ((JavascriptExecutor) driver).executeScript(
                    "return document.querySelector('[class*=\"noticeRegion\"] button[class*=\"colorGreen\"]') || " +
                    "       document.querySelector('div[class*=\"noticeRegion\"] button:last-child');"
                );
            }

            if (saveBtn != null) {
                logger.info("Save button found. Clicking...");
                try {
                    clickHumanly(saveBtn);
                } catch (Exception e) {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", saveBtn);
                }
                simulateThinking(2000, 3000);
                Boolean stillExists = (Boolean) ((JavascriptExecutor) driver).executeScript(
                    "return document.body.innerText.toLowerCase().includes('save changes') && document.querySelector('[class*=\"noticeRegion\"]') !== null;"
                );
                if (!stillExists) {
                    logger.info("Changes saved successfully.");
                    return;
                }
            }
            simulateThinking(2000, 3000);
        }
        logger.warn("Could not confirm changes were saved.");
    }

    public void closeUserSettings() {
        logger.info("Closing User Settings...");
        boolean closed = false;
        for (int i = 0; i < 3; i++) {
            try {
                WebElement closeBtn = (WebElement) ((JavascriptExecutor) driver).executeScript(
                    "return document.querySelector('div[class*=\"closeButton\"]') || " +
                    "       Array.from(document.querySelectorAll('div, button')).find(el => el.getAttribute('aria-label') === 'Close');"
                );
                if (closeBtn != null) {
                    clickHumanly(closeBtn);
                    closed = true;
                    break;
                }
            } catch (StaleElementReferenceException e) {}
            simulateThinking(500, 1000);
        }

        if (!closed) {
            driver.findElement(By.tagName("body")).sendKeys(Keys.ESCAPE);
        }
        simulateThinking(1000, 2000);
    }

    public String getInputValue(String label) {
        String script = 
            "const labelText = arguments[0].toLowerCase();" +
            "if (labelText.includes('display name')) {\n" +
            "  const el = document.querySelector('input[name=\"global_name\"]') || document.querySelector('input[name=\"display_name\"]');\n" +
            "  if (el) return el.value;\n" +
            "}\n" +
            "const allElements = Array.from(document.querySelectorAll('h2, h3, div, label, span, [class*=\"title\"]'));" +
            "const targetLabel = allElements.find(el => el.textContent.toLowerCase().trim() === labelText || el.innerText.toLowerCase().includes(labelText));" +
            "if (!targetLabel) return null;" +
            "const container = targetLabel.closest('div[class*=\"container\"]') || targetLabel.closest('div[class*=\"item\"]') || targetLabel.closest('[role=\"group\"]') || targetLabel.parentElement;" +
            "const input = container ? (container.querySelector('input') || container.querySelector('textarea')) : null;" +
            "if (input) return input.value;" +
            "let next = targetLabel.nextElementSibling;\n" +
            "while(next) {\n" +
            "  const siblingInput = next.querySelector('input') || next.querySelector('textarea') || (next.tagName === 'INPUT' ? next : null);\n" +
            "  if (siblingInput) return siblingInput.value;\n" +
            "  next = next.nextElementSibling;\n" +
            "}\n" +
            "return null;";
        return (String) ((JavascriptExecutor) driver).executeScript(script, label);
    }
}
