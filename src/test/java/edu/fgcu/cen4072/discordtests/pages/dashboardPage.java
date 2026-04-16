package edu.fgcu.cen4072.discordtests.pages;

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
        injectStealth();
        try {
            logger.info("Waiting for dashboard hydration (60s)... Current URL: " + driver.getCurrentUrl());
            new WebDriverWait(driver, Duration.ofSeconds(60)).until(d -> (Boolean) ((JavascriptExecutor) d).executeScript(
                "return document.querySelectorAll('nav.guilds__5e434, [aria-label=\"Servers\"], [class*=\"guilds\"], [class*=\"appAsidePanelWrapper\"]').length > 0"
            ));
            cleanUpPhantomServers();
        } catch (TimeoutException e) {
            logger.warn("Hydration timeout at URL: " + driver.getCurrentUrl() + ". Checking for manual intervention markers...");
            if (detectManualIntervention()) {
                logger.error("!!! INTERVENTION REQUIRED: Please solve CAPTCHA/Verify on your second monitor !!!");
                try { Thread.sleep(60000); } catch (InterruptedException ignored) {}
            }
            
            logger.info("Refreshing and attempting final hydration...");
            driver.navigate().refresh();
            simulateThinking(5000, 8000);
            
            new WebDriverWait(driver, Duration.ofSeconds(60)).until(d -> (Boolean) ((JavascriptExecutor) d).executeScript(
                "return document.querySelectorAll('nav.guilds__5e434, [aria-label=\"Servers\"], [class*=\"guilds\"]').length > 0"
            ));
            cleanUpPhantomServers();
        }
    }

    private void cleanUpPhantomServers() {
        logger.info("Running Server Purge Sequence: Checking for abandoned test servers...");
        try {
            boolean serverDeleted = true;
            while(serverDeleted) {
                serverDeleted = false;
                WebElement targetServer = (WebElement) ((JavascriptExecutor) driver).executeScript(
                    "return Array.from(document.querySelectorAll('[aria-label]'))" +
                    ".find(el => el.getAttribute('aria-label').toLowerCase().includes('nexus lab') || el.getAttribute('aria-label').toLowerCase().includes('logs-'));"
                );
                
                if (targetServer != null) {
                    logger.info("Phantom server found. Commencing purge...");
                    clickHumanly(targetServer);
                    simulateThinking(1000, 1500);
                    
                    WebElement header = (WebElement) ((JavascriptExecutor) driver).executeScript(
                        "return document.querySelector('div.guildDropdown_f37cb1') || document.querySelector('header.header_f37cb1');"
                    );
                    if (header != null) {
                        clickHumanly(header);
                        simulateThinking(1000, 1500);
                        WebElement settings = (WebElement) ((JavascriptExecutor) driver).executeScript(
                            "return Array.from(document.querySelectorAll('div, [role=\"menuitem\"]')).find(el => el.textContent.toLowerCase().includes('server settings'));"
                        );
                        if (settings != null) {
                            clickHumanly(settings);
                            simulateThinking(2000, 3000);
                            
                            serverSettingsPage ssp = new serverSettingsPage(driver);
                            String sName = (String) ((JavascriptExecutor) driver).executeScript("return document.querySelector('header h2') ? document.querySelector('header h2').innerText : 'Unknown';");
                            ssp.deleteServer(sName);
                            serverDeleted = true;
                            simulateThinking(2000, 3000);
                        }
                    }
                }
            }
            logger.info("Server Purge Sequence Complete.");
        } catch (Exception e) {
            logger.warn("Server Purge interrupted, continuing with test: " + e.getMessage());
        }
    }

    private boolean detectManualIntervention() {
        return (Boolean) ((JavascriptExecutor) driver).executeScript(
            "const t = document.body.innerText.toLowerCase();" +
            "return t.includes('hcaptcha') || t.includes('verify you are human') || " +
            "       t.includes('new login location') || t.includes('password') || " +
            "       document.querySelectorAll('iframe[src*=\"hcaptcha\"]').length > 0 ||" +
            "       window.location.href.includes('/login');"
        );
    }

    public void openAddServerModal() {
        injectStealth();
        ensureHydrated();
        clearOverlays();
        
        logger.info("Opening Add Server modal via Direct URL Navigation (Discovery Path)...");
        boolean modalOpened = false;

        // Step 1: Direct navigation to Guild Discovery (The most stable path to 'Create' buttons)
        driver.get("https://discord.com/guild-discovery");
        simulateThinking(3000, 5000);
        
        // Characteristic Match on Discovery Page
        WebElement discoverCreateBtn = (WebElement) ((JavascriptExecutor) driver).executeScript(
            "return Array.from(document.querySelectorAll('button, [role=\"button\"]'))" +
            ".find(el => el.textContent.toLowerCase().includes('create a server') || " +
            "            el.textContent.toLowerCase().includes('create my own'));"
        );

        if (discoverCreateBtn != null) {
            try {
                clickHumanly(discoverCreateBtn);
                simulateThinking(2000, 3500);
                if (isModalPresent()) modalOpened = true;
            } catch (Exception e) { logger.warn("Discovery button click failed: {}", e.getMessage()); }
        }

        // Step 2: Fallback to Sidebar Characteristics if direct navigation didn't work
        if (!modalOpened) {
            logger.info("Direct path failed, attempting Sidebar characteristic search...");
            driver.get("https://discord.com/channels/@me");
            simulateThinking(2000, 3000);
            
            WebElement plus = (WebElement) ((JavascriptExecutor) driver).executeScript(
                "return document.querySelector('[aria-label*=\"Add a Server\" i]') || " +
                "       document.querySelector('[data-list-item-id$=\"create-join-button\"]');"
            );
            if (plus != null) {
                clickHumanly(plus);
                simulateThinking(1500, 2500);
                if (isModalPresent()) modalOpened = true;
            }
        }

        if (!modalOpened) {
            throw new TimeoutException("All paths (Direct URL, Discovery, and Sidebar) failed to open Add Server modal.");
        }
    }

    private boolean isModalPresent() {
        return (Boolean) ((JavascriptExecutor) driver).executeScript(
            "const text = document.body.innerText.toLowerCase();" +
            "return document.querySelectorAll('div[role=\"dialog\"]').length > 0 && " +
            "       (text.includes('create my own') || text.includes('start from a template') || text.includes('customize your server') || text.includes('create a server'));"
        );
    }

    public void createServer(String name, String pfpPath) {
        logger.info("Selecting 'Create My Own' template...");
        WebElement template = null;
        for (int i = 0; i < 10; i++) {
            template = (WebElement) ((JavascriptExecutor) driver).executeScript(
                "return Array.from(document.querySelectorAll('button, [role=\"button\"], div[class*=\"container\"]'))" +
                ".find(el => el.textContent.toLowerCase().includes('create my own'));"
            );
            if (template != null) break;
            simulateThinking(500, 1000);
        }
        if (template != null) {
            clickHumanly(template);
            simulateThinking(500, 1000);
        } else {
            logger.info("Template option not found, skipping.");
        }

        logger.info("Selecting audience...");
        WebElement audience = null;
        for (int i = 0; i < 5; i++) {
            audience = (WebElement) ((JavascriptExecutor) driver).executeScript(
                "return Array.from(document.querySelectorAll('button, [role=\"button\"], div[class*=\"container\"]'))" +
                ".find(el => el.textContent.toLowerCase().includes('for me and my friends') || " +
                "            el.textContent.toLowerCase().includes('for a club') || " +
                "            el.textContent.toLowerCase().includes('skip this question') || " +
                "            el.textContent.toLowerCase().includes('create my own'));"
            );
            if (audience != null) break;
            simulateThinking(500, 1000);
        }
        if (audience != null) {
            clickHumanly(audience);
            simulateThinking(500, 1000);
        } else {
            logger.info("Audience option not found, skipping.");
        }

        if (pfpPath != null && !pfpPath.isEmpty()) {
            try {
                fileInput.sendKeys(pfpPath);
                simulateThinking(300, 700);
            } catch (Exception e) {
                logger.warn("Could not upload PFP: {}", e.getMessage());
            }
        }

        logger.info("Finding server name input...");
        WebElement nameField = null;
        for (int i = 0; i < 10; i++) {
            nameField = (WebElement) ((JavascriptExecutor) driver).executeScript(
                "return Array.from(document.querySelectorAll('input'))" +
                ".find(el => el.type !== 'file' && el.type !== 'checkbox' && el.type !== 'radio' && el.offsetParent !== null && !el.readOnly && !el.disabled && " +
                "            (el.value.includes(\"'s server\") || el.placeholder || el.className.includes('input') || el.id.includes('uid')) " +
                "            && !el.name.toLowerCase().includes('friend') && !el.id.toLowerCase().includes('friend'));"
            );
            if (nameField != null) break;
            simulateThinking(500, 1000);
        }

        if (nameField == null) {
            try {
                nameField = wait.until(ExpectedConditions.elementToBeClickable(nameInput));
            } catch (Exception e) {
                throw new NoSuchElementException("Server name input not found. Modal state: " + isModalPresent());
            }
        }
        
        Keys modifier = System.getProperty("os.name").toLowerCase().contains("mac") ? Keys.COMMAND : Keys.CONTROL;
        clickHumanly(nameField);
        nameField.sendKeys(Keys.chord(modifier, "a"), Keys.BACK_SPACE);
        typeHumanly(nameField, name);
        simulateThinking(500, 1000);

        logger.info("Clicking final submit button...");
        WebElement finalBtn = null;
        for (int i = 0; i < 5; i++) {
            finalBtn = (WebElement) ((JavascriptExecutor) driver).executeScript(
                "return Array.from(document.querySelectorAll('button'))" +
                ".find(el => el.textContent.toLowerCase().includes('create') && !el.textContent.toLowerCase().includes('template'));"
            );
            if (finalBtn != null) break;
            simulateThinking(500, 1000);
        }
        
        WebElement actualBtn = finalBtn != null ? finalBtn : submitButton;
        
        try {
            clickHumanly(actualBtn);
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", actualBtn);
        }

        try {
            new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("div[class*='modal'], div[class*='layerContainer']")));
        } catch (Exception ignore) {}
        
        simulateThinking(1500, 2500);
    }

    public void openCreateChannelModal() {
        logger.info("Opening Create Channel modal...");
        boolean opened = false;
        try {
            WebElement plusBtn = (WebElement) ((JavascriptExecutor) driver).executeScript(
                "return Array.from(document.querySelectorAll('[aria-label*=\"Create Channel\" i], [aria-label*=\"Create Text Channel\" i], #channels [class*=\"addButton\"], [data-list-item-id*=\"create-channel\"]'))" +
                ".find(el => el.offsetParent !== null);"
            );
            if (plusBtn != null) {
                logger.info("Clicking channel plus button...");
                clickHumanly(plusBtn);
                simulateThinking(1500, 2500);
                opened = (Boolean) ((JavascriptExecutor) driver).executeScript(
                    "return document.querySelectorAll('div[role=\"dialog\"] input, div[class*=\"modal\"] input').length > 0;"
                );
            }
        } catch (Exception e) {
            logger.warn("Primary channel plus button click failed: " + e.getMessage());
        }

        if (!opened) {
            logger.info("Falling back to server header menu for channel creation...");
            WebElement header = (WebElement) ((JavascriptExecutor) driver).executeScript(
                "return document.querySelector('div.guildDropdown_f37cb1') || " +
                "       document.querySelector('header.header_f37cb1') || " +
                "       document.querySelector('div.header_f37cb1') || " +
                "       document.querySelector('h2[class*=\"name_f37cb1\"]');"
            );
            if (header != null) {
                try {
                    clickHumanly(header);
                    simulateThinking(1000, 1500);
                    WebElement createChannelItem = (WebElement) ((JavascriptExecutor) driver).executeScript(
                        "return Array.from(document.querySelectorAll('[role=\"menuitem\"]'))" +
                        ".find(el => el.textContent.toLowerCase().includes('create channel'));"
                    );
                    if (createChannelItem != null) {
                        clickHumanly(createChannelItem);
                        simulateThinking(1500, 2500);
                    }
                } catch (Exception e) {}
            }
        }
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
            if (voiceRadio != null) {
                clickHumanly(voiceRadio);
                simulateThinking(300, 700);
            } else {
                logger.warn("Voice channel radio option not found. Attempting to proceed anyway...");
            }
        }

        logger.info("Finding channel name input...");
        WebElement input = (WebElement) ((JavascriptExecutor) driver).executeScript(
            "return Array.from(document.querySelectorAll('div[role=\"dialog\"] input, div[class*=\"modal\"] input, input[type=\"text\"]'))" +
            ".find(el => el.offsetParent !== null && !el.readOnly && !el.disabled && (el.placeholder || el.className.includes('input')));"
        );

        if (input == null) {
            try {
                input = wait.until(ExpectedConditions.elementToBeClickable(nameInput));
            } catch (Exception e) {
                throw new NoSuchElementException("Could not locate the channel name input field.");
            }
        }
        
        Keys modifier = System.getProperty("os.name").toLowerCase().contains("mac") ? Keys.COMMAND : Keys.CONTROL;
        clickHumanly(input);
        input.sendKeys(Keys.chord(modifier, "a"), Keys.BACK_SPACE);
        typeHumanly(input, name);
        simulateThinking(300, 600);

        logger.info("Clicking channel create button...");
        WebElement finalBtn = (WebElement) ((JavascriptExecutor) driver).executeScript(
            "return Array.from(document.querySelectorAll('div[role=\"dialog\"] button, div[class*=\"modal\"] button'))" +
            ".find(el => el.textContent.toLowerCase().trim() === 'create channel' || " +
            "            el.textContent.toLowerCase().includes('create'));"
        );
        
        try {
            clickHumanly(finalBtn != null ? finalBtn : submitButton);
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", finalBtn != null ? finalBtn : submitButton);
        }
        
        try {
            new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("div[class*='modal'], div[class*='layerContainer']")));
        } catch (Exception ignore) {}
        
        simulateThinking(1000, 2000);
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

    public void selectServer(String name) {
        logger.info("Selecting server: {}", name);
        WebElement server = (WebElement) ((JavascriptExecutor) driver).executeScript(
            "const nameArg = arguments[0].toLowerCase();" +
            "return Array.from(document.querySelectorAll('[aria-label]'))" +
            ".find(el => el.getAttribute('aria-label').toLowerCase().includes(nameArg));" ,
            name
        );
        if (server == null) throw new NoSuchElementException("Server icon not found: " + name);
        clickHumanly(server);
        simulateThinking(1000, 2000);
    }

    public void selectChannel(String name) {
        logger.info("Selecting channel: {}", name);
        WebElement channel = (WebElement) ((JavascriptExecutor) driver).executeScript(
            "const nameArg = arguments[0].toLowerCase();" +
            "return Array.from(document.querySelectorAll('[class*=\"channelName\"]'))" +
            ".find(el => el.textContent.toLowerCase().includes(nameArg) && el.offsetParent !== null);" ,
            name
        );
        if (channel == null) {
            // Try searching by aria-label
            channel = (WebElement) ((JavascriptExecutor) driver).executeScript(
                "const nameArg = arguments[0].toLowerCase();" +
                "return Array.from(document.querySelectorAll('[aria-label*=\"' + nameArg + '\"]'))" +
                ".find(el => el.offsetParent !== null);" ,
                name
            );
        }
        if (channel == null) throw new NoSuchElementException("Channel not found: " + name);
        clickHumanly(channel);
        simulateThinking(1000, 2000);
    }

    public void sendMessage(String text) {
        logger.info("Sending message: {}", text);
        WebElement chatBox = (WebElement) ((JavascriptExecutor) driver).executeScript(
            "return document.querySelector('[role=\"textbox\"][aria-label*=\"Message\"]') || " +
            "       document.querySelector('div[class*=\"slateTextArea\"]') || " +
            "       document.querySelector('[class*=\"textArea\"] [role=\"textbox\"]');"
        );
        
        if (chatBox == null) throw new NoSuchElementException("Chat input box not found.");
        
        clickHumanly(chatBox);
        typeHumanly(chatBox, text);
        chatBox.sendKeys(Keys.ENTER);
        simulateThinking(1000, 2000);
    }

    public String getLastMessageText() {
        return (String) ((JavascriptExecutor) driver).executeScript(
            "const messages = document.querySelectorAll('[class*=\"messageContent\"]');" +
            "return messages.length > 0 ? messages[messages.length - 1].innerText : null;"
        );
    }

    public void editLastMessage(String newText) {
        logger.info("Editing last message to: {}", newText);
        // Strategy: Press Up Arrow to edit last message
        WebElement chatBox = (WebElement) ((JavascriptExecutor) driver).executeScript(
            "return document.querySelector('[role=\"textbox\"][aria-label*=\"Message\"]') || " +
            "       document.querySelector('div[class*=\"slateTextArea\"]');"
        );
        if (chatBox != null) {
            clickHumanly(chatBox);
            chatBox.sendKeys(Keys.ARROW_UP);
            simulateThinking(1000, 1500);
            
            WebElement editInput = (WebElement) ((JavascriptExecutor) driver).executeScript(
                "return document.querySelector('div[class*=\"inner\"] [role=\"textbox\"]');"
            );
            if (editInput != null) {
                Keys modifier = getModifierKey();
                editInput.sendKeys(Keys.chord(modifier, "a"), Keys.BACK_SPACE);
                typeHumanly(editInput, newText);
                editInput.sendKeys(Keys.ENTER);
                simulateThinking(1000, 2000);
            }
        }
    }

    public void deleteLastMessage() {
        logger.info("Deleting last message...");
        ((JavascriptExecutor) driver).executeScript(
            "const messages = document.querySelectorAll('[class*=\"messageContent\"]');" +
            "if (messages.length > 0) {" +
            "  const lastMsg = messages[messages.length - 1];" +
            "  const container = lastMsg.closest('[class*=\"message_\"]');" +
            "  if (container) {" +
            "    const event = new MouseEvent('mouseover', { view: window, bubbles: true, cancelable: true });" +
            "    container.dispatchEvent(event);" +
            "  }" +
            "}"
        );
        simulateThinking(500, 1000);
        
        WebElement moreBtn = (WebElement) ((JavascriptExecutor) driver).executeScript(
            "return document.querySelector('[aria-label=\"More\"]') || " +
            "       document.querySelector('[class*=\"button\"] [aria-label*=\"More\"]');"
        );
        
        if (moreBtn != null) {
            clickHumanly(moreBtn);
            simulateThinking(800, 1200);
            
            WebElement deleteItem = (WebElement) ((JavascriptExecutor) driver).executeScript(
                "return Array.from(document.querySelectorAll('[role=\"menuitem\"]'))" +
                ".find(el => el.textContent.toLowerCase().includes('delete message'));"
            );
            
            if (deleteItem != null) {
                clickHumanly(deleteItem);
                simulateThinking(1000, 1500);
                
                WebElement confirmDelete = (WebElement) ((JavascriptExecutor) driver).executeScript(
                    "return Array.from(document.querySelectorAll('button'))" +
                    ".find(el => el.textContent.toLowerCase().trim() === 'delete');"
                );
                if (confirmDelete != null) {
                    clickHumanly(confirmDelete);
                    simulateThinking(1000, 2000);
                }
            }
        }
    }

    public void changeStatus(String status) {
        logger.info("Changing online status to: {}", status);
        WebElement avatar = (WebElement) ((JavascriptExecutor) driver).executeScript(
            "return document.querySelector('[class*=\"avatarWrapper\"]') || " +
            "       document.querySelector('div[class*=\"panels\"] [class*=\"avatar\"]');"
        );
        if (avatar == null) {
            throw new NoSuchElementException("User avatar not found in dashboard panels.");
        }

        clickHumanly(avatar);
        simulateThinking(1500, 2500);

        // Look for the status menu item directly
        WebElement statusMenu = (WebElement) ((JavascriptExecutor) driver).executeScript(
            "return Array.from(document.querySelectorAll('[id*=\"status-picker\"] [class*=\"item\"], [role=\"menuitem\"]'))" +
            ".find(el => el.textContent.toLowerCase().includes(arguments[0].toLowerCase()) && el.offsetParent !== null);",
            status
        );

        if (statusMenu == null) {
            logger.info("Direct status menu item not found, attempting to find 'Set Status' or similar submenu trigger...");
            WebElement subMenuTrigger = (WebElement) ((JavascriptExecutor) driver).executeScript(
                "return Array.from(document.querySelectorAll('[role=\"menuitem\"]'))" +
                ".find(el => (el.textContent.toLowerCase().includes('online') || el.textContent.toLowerCase().includes('idle') || " +
                "            el.textContent.toLowerCase().includes('do not disturb') || el.textContent.toLowerCase().includes('invisible')) && " +
                "            el.offsetParent !== null);"
            );
            if (subMenuTrigger != null) {
                clickHumanly(subMenuTrigger);
                simulateThinking(1000, 2000);
                statusMenu = (WebElement) ((JavascriptExecutor) driver).executeScript(
                    "return Array.from(document.querySelectorAll('[role=\"menuitem\"], [id*=\"status-picker\"] [class*=\"item\"]'))" +
                    ".find(el => el.textContent.toLowerCase().includes(arguments[0].toLowerCase()) && el.offsetParent !== null);",
                    status
                );
            }
        }

        if (statusMenu != null) {
            logger.info("Found status menu item, clicking: {}", status);
            clickHumanly(statusMenu);
            simulateThinking(2000, 3000);
        } else {
            // Save screenshot or log DOM for debugging
            logger.error("Failed to find status menu item for: {}", status);
            throw new NoSuchElementException("Could not find status menu item for: " + status);
        }
    }

    public String getCurrentStatus() {
        return (String) ((JavascriptExecutor) driver).executeScript(
            "const avatar = document.querySelector('[class*=\"avatarWrapper\"]') || " +
            "               document.querySelector('div[class*=\"panels\"] [class*=\"avatar\"]') || " +
            "               document.querySelector('[aria-label*=\"User area\"] [class*=\"avatar\"]');" +
            "if (!avatar) return 'unknown';" +
            "const statusDot = avatar.querySelector('rect[mask*=\"status\"], [class*=\"status\"], [aria-label*=\"Online\"], [aria-label*=\"Idle\"], [aria-label*=\"Disturb\"], [aria-label*=\"Invisible\"], [aria-label*=\"Offline\"]');" +
            "if (!statusDot) {" +
            "  // Try finding by the mask or sibling\n" +
            "  const allRects = Array.from(avatar.querySelectorAll('rect'));\n" +
            "  const statusRect = allRects.find(r => r.getAttribute('mask') && r.getAttribute('mask').includes('status'));\n" +
            "  if (statusRect) return statusRect.getAttribute('aria-label') || 'unknown';\n" +
            "  return 'unknown';\n" +
            "}" +
            "return statusDot.getAttribute('aria-label') || 'unknown';"
        );
    }
    public void openFriendsTab() {
        logger.info("Opening Friends tab...");
        WebElement homeBtn = (WebElement) ((JavascriptExecutor) driver).executeScript(
            "return document.querySelector('[aria-label=\"Direct Messages\"]') || " +
            "       document.querySelector('[data-list-item-id$=\"home\"]');"
        );
        if (homeBtn != null) {
            clickHumanly(homeBtn);
            simulateThinking(1000, 1500);
            
            WebElement friendsLink = (WebElement) ((JavascriptExecutor) driver).executeScript(
                "return Array.from(document.querySelectorAll('[class*=\"channel\"]'))" +
                ".find(el => el.textContent.toLowerCase().includes('friends'));"
            );
            if (friendsLink != null) {
                clickHumanly(friendsLink);
                simulateThinking(1000, 2000);
            }
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

    public void toggleMute() {
        logger.info("Toggling mute...");
        simulateThinking(2000, 4000); // Wait for user panel to load
        WebElement muteBtn = (WebElement) ((JavascriptExecutor) driver).executeScript(
            "return document.querySelector('button[aria-label=\"Mute\"]') || document.querySelector('button[aria-label=\"Unmute\"]');"
        );
        if (muteBtn != null) {
            clickHumanly(muteBtn);
            simulateThinking(500, 1000);
        } else {
            logger.warn("Mute button not found.");
        }
    }

    public void toggleDeafen() {
        logger.info("Toggling deafen...");
        simulateThinking(2000, 4000); // Wait for user panel to load
        WebElement deafenBtn = (WebElement) ((JavascriptExecutor) driver).executeScript(
            "return document.querySelector('button[aria-label=\"Deafen\"]') || document.querySelector('button[aria-label=\"Undeafen\"]');"
        );
        if (deafenBtn != null) {
            clickHumanly(deafenBtn);
            simulateThinking(500, 1000);
        } else {
            logger.warn("Deafen button not found.");
        }
    }

    public userSettingsPage openUserSettings() {
        logger.info("Opening User Settings...");
        simulateThinking(1000, 2000);

        // 0. Handle potential "Stay in Browser" popup
        try {
            WebElement stayInBrowser = (WebElement) ((JavascriptExecutor) driver).executeScript(
                "return Array.from(document.querySelectorAll('button'))" +
                ".find(el => el.textContent.toLowerCase().includes('stay in browser') || el.textContent.toLowerCase().includes('continue in browser'));"
            );
            if (stayInBrowser != null) {
                logger.info("Dismissing 'Stay in Browser' popup...");
                clickHumanly(stayInBrowser);
                simulateThinking(1000, 2000);
            }
        } catch (Exception e) {}
        
        boolean settingsOpened = false;

        // Strategy 1: Hotkey (Ctrl + ,)
        logger.info("Attempting Strategy 1: Ctrl+, Hotkey...");
        try {
            Keys modifier = getModifierKey();
            driver.findElement(By.tagName("body")).sendKeys(Keys.chord(modifier, ","));
            simulateThinking(3000, 5000);
            settingsOpened = (Boolean) ((JavascriptExecutor) driver).executeScript(
                "return document.querySelectorAll('[class*=\"layer\"] [class*=\"standardSidebarView\"]').length > 0 || " +
                "       document.body.innerText.toLowerCase().includes('my account') || " +
                "       window.location.href.includes('settings');"
            );
        } catch (Exception e) {
            logger.warn("Strategy 1 (Hotkey) failed: {}", e.getMessage());
        }

        if (!settingsOpened) {
            // Strategy 2: Offset click from Deafen button (User's Suggestion)
            logger.info("Attempting Strategy 2: Offset click from Deafen button...");
            try {
                WebElement deafenBtn = (WebElement) ((JavascriptExecutor) driver).executeScript(
                    "return document.querySelector('button[aria-label=\"Deafen\"]') || document.querySelector('button[aria-label=\"Undeafen\"]');"
                );
                if (deafenBtn != null) {
                    actions.moveToElement(deafenBtn, 36, 0).click().perform();
                    simulateThinking(3000, 5000);
                    settingsOpened = (Boolean) ((JavascriptExecutor) driver).executeScript(
                        "return document.querySelectorAll('[class*=\"layer\"] [class*=\"standardSidebarView\"]').length > 0 || " +
                        "       document.body.innerText.toLowerCase().includes('my account') || " +
                        "       window.location.href.includes('settings');"
                    );
                }
            } catch (Exception e) {
                logger.warn("Strategy 2 (Offset click) failed: {}", e.getMessage());
            }
        }

        if (!settingsOpened) {
            // Strategy 3: Direct button click
            logger.info("Attempting Strategy 3: Direct button click...");
            for (int attempt = 0; attempt < 3; attempt++) {
                WebElement settingsBtn = (WebElement) ((JavascriptExecutor) driver).executeScript(
                    "return document.querySelector('button[aria-label=\"User Settings\"]') || " +
                    "       document.querySelector('div[class*=\"panels\"] button[aria-label=\"User Settings\"]') || " +
                    "       Array.from(document.querySelectorAll('button')).find(el => el.getAttribute('aria-label') === 'User Settings');"
                );

                if (settingsBtn != null) {
                    logger.info("Clicking User Settings button (attempt {})...", attempt + 1);
                    clickHumanly(settingsBtn);
                    
                    simulateThinking(4000, 6000);
                    settingsOpened = (Boolean) ((JavascriptExecutor) driver).executeScript(
                        "return document.querySelectorAll('[class*=\"layer\"] [class*=\"sidebar\"]').length > 0 || " +
                        "       document.querySelectorAll('[class*=\"standardSidebarView\"]').length > 0 || " +
                        "       window.location.href.includes('settings');"
                    );
                    if (settingsOpened) {
                        logger.info("User Settings layer confirmed open.");
                        break;
                    }
                }
                simulateThinking(2000, 4000);
            }
        }

        if (settingsOpened) {
            return new userSettingsPage(driver);
        } else {
            throw new NoSuchElementException("Failed to open User Settings menu using all strategies (Hotkey, Offset, Direct Click).");
        }
    }
}
