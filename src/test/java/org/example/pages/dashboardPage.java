package org.example.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class dashboardPage extends basePage {

    @FindBy(css = "nav.guilds__5e434 div.scroller_ef3116, nav[class*='guilds'] div[class*='scrollerBase']")
    private WebElement guildsScroller;

    @FindBy(css = "[aria-label='Add a Server'], [aria-label='Add a server'], [data-list-item-id$='create-join-button']")
    private WebElement addServerButton;

    @FindBy(css = "input[type='file']")
    private WebElement fileInput;

    @FindBy(css = "input[class*='inputDefault']")
    private WebElement nameInput;

    @FindBy(css = "button[type='submit']")
    private WebElement submitButton;

    public dashboardPage(WebDriver driver) {
        super(driver);
    }

    public void ensureHydrated() {
        try {
            logger.info("Waiting for dashboard hydration...");
            wait.until(d -> (Boolean) ((JavascriptExecutor) d).executeScript(
                "return document.querySelectorAll('nav.guilds__5e434, [aria-label=\"Servers\"], [class*=\"guilds\"]').length > 0"
            ));
        } catch (TimeoutException e) {
            logger.warn("Hydration wait timed out, refreshing page...");
            driver.navigate().refresh();
            simulateThinking(1000, 2000);
        }
    }

    public void openAddServerModal() {
        ensureHydrated();
        clearOverlays();
        
        logger.info("Attempting to open Add Server modal via Explore/Discovery path...");
        boolean modalOpened = false;

        // Attempt 1: Direct navigation to discovery (fastest reset)
        driver.get("https://discord.com/guild-discovery");
        simulateThinking(1500, 2000);
        
        WebElement createBtn = (WebElement) ((JavascriptExecutor) driver).executeScript(
            "return Array.from(document.querySelectorAll('button, [role=\"button\"], [class*=\"button\"]'))" +
            ".find(el => el.textContent.toLowerCase().includes('create a server') || " +
            "            el.textContent.toLowerCase().includes('create my own'));"
        );
        
        if (createBtn != null) {
            logger.info("Found Create button on discovery page, clicking...");
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", createBtn);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", createBtn);
            simulateThinking(1500, 2000);
            if (isModalPresent()) modalOpened = true;
        }

        if (!modalOpened) {
            logger.info("Discovery failed, trying sidebar shortcut key...");
            driver.get("https://discord.com/channels/@me");
            ensureHydrated();
            clearOverlays();
            driver.findElement(By.tagName("body")).click();
            actions.keyDown(Keys.COMMAND).keyDown(Keys.SHIFT).sendKeys("n").keyUp(Keys.SHIFT).keyUp(Keys.COMMAND).perform();
            simulateThinking(1500, 2000);
            if (isModalPresent()) modalOpened = true;
        }

        if (!modalOpened) {
            logger.info("Shortcut failed, trying plus icon search...");
            WebElement plus = (WebElement) ((JavascriptExecutor) driver).executeScript(
                "const plusPath = 'M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z';" +
                "const svg = Array.from(document.querySelectorAll('svg')).find(s => s.innerHTML.includes(plusPath));" +
                "if (svg) return svg.closest('[role=\"treeitem\"]') || svg.closest('button') || svg;" +
                "return document.querySelector('[aria-label*=\"Add a Server\" i]') || document.querySelector('[data-list-item-id$=\"create-join-button\"]');"
            );
            if (plus != null) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", plus);
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", plus);
                simulateThinking(2000, 3000);
                if (isModalPresent()) modalOpened = true;
            }
        }

        if (!modalOpened) {
            throw new TimeoutException("Failed to open Add Server modal after Explore, Shortcut, and Plus icon attempts.");
        }
    }

    private boolean isModalPresent() {
        return (Boolean) ((JavascriptExecutor) driver).executeScript(
            "const text = document.body.innerText.toLowerCase();" +
            "return text.includes('create my own') || " +
            "       text.includes('start from a template') || " +
            "       text.includes('create a server') || " +
            "       document.querySelectorAll('[class*=\"modal\"], [class*=\"layerContainer\"]').length > 1 ||" +
            "       document.querySelectorAll('div[role=\"dialog\"]').length > 0;"
        );
    }

    public void createServer(String name, String pfpPath) {
        logger.info("Selecting 'Create My Own' template...");
        WebElement template = (WebElement) ((JavascriptExecutor) driver).executeScript(
            "return Array.from(document.querySelectorAll('button, [role=\"button\"]'))" +
            ".find(el => el.textContent.toLowerCase().includes('create my own'));"
        );
        clickHumanly(template);
        simulateThinking(300, 700);

        logger.info("Selecting audience...");
        WebElement audience = (WebElement) ((JavascriptExecutor) driver).executeScript(
            "return Array.from(document.querySelectorAll('button, [role=\"button\"]'))" +
            ".find(el => el.textContent.toLowerCase().includes('for me and my friends'));"
        );
        clickHumanly(audience);
        simulateThinking(300, 700);

        if (pfpPath != null && !pfpPath.isEmpty()) {
            try {
                fileInput.sendKeys(pfpPath);
                simulateThinking(300, 700);
            } catch (Exception e) {
                logger.warn("Could not upload PFP: {}", e.getMessage());
            }
        }

        logger.info("Finding server name input...");
        WebElement nameField = (WebElement) ((JavascriptExecutor) driver).executeScript(
            "return Array.from(document.querySelectorAll('input[type=\"text\"]'))" +
            ".find(el => (el.value.includes(\"'s server\") || el.placeholder || el.className.includes('input')) " +
            "&& !el.name.includes('friend') && !el.id.includes('friend'));"
        );

        if (nameField == null) nameField = wait.until(ExpectedConditions.elementToBeClickable(nameInput));
        
        clickHumanly(nameField);
        nameField.sendKeys(Keys.chord(Keys.COMMAND, "a"), Keys.BACK_SPACE);
        typeHumanly(nameField, name);
        simulateThinking(300, 600);

        logger.info("Clicking final submit button...");
        WebElement finalBtn = (WebElement) ((JavascriptExecutor) driver).executeScript(
            "return Array.from(document.querySelectorAll('button'))" +
            ".find(el => el.textContent.toLowerCase().includes('create') && !el.textContent.toLowerCase().includes('template'));"
        );
        WebElement actualBtn = finalBtn != null ? finalBtn : submitButton;
        
        try {
            clickHumanly(actualBtn);
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", actualBtn);
        }

        try {
            new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("div[class*='modal'], div[class*='layerContainer']")));
        } catch (Exception ignore) {}
        
        simulateThinking(1000, 1500);
    }

    public void openCreateChannelModal() {
        WebElement plusBtn = (WebElement) ((JavascriptExecutor) driver).executeScript(
            "return document.querySelector('#channels [class*=\"addButton\"]');"
        );
        clickHumanly(wait.until(ExpectedConditions.elementToBeClickable(plusBtn)));
    }

    public void createChannel(String name, boolean isVoice) {
        if (isVoice) {
            logger.info("Selecting voice channel type...");
            WebElement voiceRadio = (WebElement) ((JavascriptExecutor) driver).executeScript(
                "return Array.from(document.querySelectorAll('[class*=\"radioItem\"], [role=\"radio\"], label'))" +
                ".find(el => el.textContent.toLowerCase().includes('voice') || " +
                "            (el.querySelector('input') && el.querySelector('input').value === '2') || " +
                "            el.getAttribute('value') === 'VOICE');"
            );
            if (voiceRadio == null) throw new NoSuchElementException("Voice channel radio option not found");
            clickHumanly(voiceRadio);
            simulateThinking(300, 700);
        }

        logger.info("Finding channel name input...");
        WebElement input = (WebElement) ((JavascriptExecutor) driver).executeScript(
            "return Array.from(document.querySelectorAll('input[type=\"text\"]'))" +
            ".find(el => (el.placeholder && el.placeholder.toLowerCase().includes('new-channel')) || el.className.includes('input'));"
        );

        if (input == null) input = wait.until(ExpectedConditions.elementToBeClickable(nameInput));
        
        clickHumanly(input);
        input.sendKeys(Keys.chord(Keys.COMMAND, "a"), Keys.BACK_SPACE);
        typeHumanly(input, name);
        simulateThinking(300, 600);

        logger.info("Clicking channel create button...");
        WebElement finalBtn = (WebElement) ((JavascriptExecutor) driver).executeScript(
            "return Array.from(document.querySelectorAll('button'))" +
            ".find(el => el.textContent.toLowerCase().trim() === 'create channel');"
        );
        clickHumanly(finalBtn != null ? finalBtn : submitButton);
        simulateThinking(500, 1000);
    }

    public boolean isServerCreated(String name) {
        try {
            ((JavascriptExecutor) driver).executeScript(
                "const scroller = document.querySelector('div.scroller_ef3116') || " +
                "                 document.querySelector('nav.guilds__5e434 div[class*=\"scroller\"]');" +
                "if (scroller) scroller.scrollTop = scroller.scrollHeight;"
            );

            return new WebDriverWait(driver, Duration.ofSeconds(20)).until(d -> (Boolean) ((JavascriptExecutor) d).executeScript(
                "const nameArg = arguments[0].toLowerCase();" +
                "return Array.from(document.querySelectorAll('[aria-label]'))" +
                ".some(el => {" +
                "  const label = el.getAttribute('aria-label').toLowerCase();" +
                "  return label === nameArg || label.startsWith(nameArg);" +
                "});",
                name
            ));
        } catch (Exception e) {
            return false;
        }
    }

    public serverSettingsPage openServerSettings(String name) {
        logger.info("Selecting server: {}", name);
        WebElement server = (WebElement) ((JavascriptExecutor) driver).executeScript(
            "const nameArg = arguments[0].toLowerCase();" +
            "return Array.from(document.querySelectorAll('[aria-label]'))" +
            ".find(el => el.getAttribute('aria-label').toLowerCase().includes(nameArg));" ,
            name
        );
        if (server == null) throw new NoSuchElementException("Server icon not found: " + name);
        clickHumanly(server);
        simulateThinking(300, 700);

        logger.info("Opening server header menu...");
        WebElement header = (WebElement) ((JavascriptExecutor) driver).executeScript(
            "return document.querySelector('div.guildDropdown_f37cb1') || " +
            "       document.querySelector('header.header_f37cb1') || " +
            "       document.querySelector('div.header_f37cb1') || " +
            "       document.querySelector('h2[class*=\"name_f37cb1\"]');"
        );
        
        if (header == null) {
            logger.warn("Precise header not found, falling back to name search...");
            header = (WebElement) ((JavascriptExecutor) driver).executeScript(
                "const nameArg = arguments[0].toLowerCase();" +
                "return Array.from(document.querySelectorAll('header [class*=\"name\"], [class*=\"header\"] [class*=\"name\"]'))" +
                ".find(el => el.textContent.toLowerCase().includes(nameArg));",
                name
            );
        }
        
        if (header == null) throw new NoSuchElementException("Could find server header with name: " + name);
        
        // Loop to ensure menu opens
        WebElement settings = null;
        for (int i = 0; i < 5; i++) {
            clickHumanly(header);
            simulateThinking(800, 1200);

            settings = (WebElement) ((JavascriptExecutor) driver).executeScript(
                "return document.getElementById('guild-header-popout-settings') || " +
                "       document.querySelector('[id*=\"settings\"][role=\"menuitem\"]') || " +
                "       Array.from(document.querySelectorAll('div, [role=\"menuitem\"], span'))" +
                "       .find(el => el.textContent.toLowerCase().trim() === 'server settings' || el.innerText.toLowerCase().includes('server settings'));"
            );
            
            if (settings != null) break;
            logger.warn("Server Settings menu item not found, retrying header click (attempt {})...", i + 1);
        }

        if (settings == null) {
            throw new NoSuchElementException("Could not find 'Server Settings' in the menu after multiple attempts.");
        }

        logger.info("Clicking 'Server Settings'...");
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", settings);
        
        // Wait for settings page hydration markers
        new WebDriverWait(driver, Duration.ofSeconds(25)).until(d -> (Boolean) ((JavascriptExecutor) d).executeScript(
            "const text = document.body.innerText.toLowerCase();" +
            "return text.includes('overview') || text.includes('roles') || document.querySelectorAll('[role=\"tab\"]').length > 2;"
        ));
        
        return new serverSettingsPage(driver);
    }
}
