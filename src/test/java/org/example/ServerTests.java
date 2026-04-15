package org.example;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import org.testng.annotations.*;
import java.io.File;
import java.time.Duration;
import java.util.Random;

/**
 * Senior Lead Architect Infrastructure Suite
 * Optimized for High-Density 2026 Environments
 */
public class ServerTests extends BaseTest {
    private final Random random = new Random();
    private String serverName;
    private final String pfpPath = System.getProperty("user.dir") + "/logo.png";

    @BeforeClass
    public void localSetup() {
        generateServerMetadata();
        ((JavascriptExecutor) driver).executeScript("document.body.style.zoom='80%'");
    }

    private void generateServerMetadata() {
        String[] prefixes = {"Nexus", "Vortex", "Static", "Logic", "Cyber"};
        this.serverName = prefixes[random.nextInt(5)] + " Lab " + (random.nextInt(899) + 100);
    }

    /* --- PROVISIONING PHASE --- */

    @Test(priority = 1, description = "Infrastructure: Provisioning Node Acquisition")
    public void step1_FocusAndScrollSidebar() {
        clearInitialOverlays();
        By scrollerLocator = By.xpath("//nav[contains(@class, 'guilds')]//div[contains(@class, 'scroller')]");
        WebElement scroller = wait.until(ExpectedConditions.presenceOfElementLocated(scrollerLocator));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollTop = arguments[0].scrollHeight", scroller);
        simulateThinking(1000, 1500);

        By addBtnLocator = By.xpath("//div[@aria-label='Add a Server'] | //button[@aria-label='Add a Server']");
        WebElement addButton = wait.until(ExpectedConditions.visibilityOfElementLocated(addBtnLocator));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", addButton);
        actions.release().perform();

        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//div[contains(@class, 'layerContainer')]//div[@role='dialog']")));
        System.out.println("Infrastructure: Modal latch successful.");
    }

    @Test(priority = 2, dependsOnMethods = "step1_FocusAndScrollSidebar")
    public void step2_SelectTemplate() {
        WebElement createOwn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@role='dialog']//*[contains(text(), 'Create My Own')]")));
        clickHumanly(createOwn);
        simulateThinking(1000, 1500);
    }

    @Test(priority = 3, dependsOnMethods = "step2_SelectTemplate")
    public void step3_SelectAudience() {
        WebElement audience = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[contains(text(), 'For me and my friends')]")));
        clickHumanly(audience);
    }

    @Test(priority = 4, dependsOnMethods = "step3_SelectAudience")
    public void step4_AttachPFP() {
        File file = new File(pfpPath);
        if (file.exists()) {
            driver.findElement(By.cssSelector("input[type='file']")).sendKeys(pfpPath);
        }
        simulateThinking(1000, 2000);
    }

    @Test(priority = 5, dependsOnMethods = "step4_AttachPFP")
    public void step5_FinalizeProvisioning() {
        ((JavascriptExecutor) driver).executeScript(
                "var input = document.querySelector('div[class*=\"createGuild\"] input:not([type=\"file\"])');" +
                        "var nativeSetter = Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype, 'value').set;" +
                        "nativeSetter.call(input, '');" +
                        "input.dispatchEvent(new Event('input', { bubbles: true }));"
        );
        WebElement nameInput = driver.findElement(By.xpath("//div[contains(@class, 'createGuild')]//input[not(@type='file')]"));
        typeHumanly(nameInput, serverName);
        nameInput.sendKeys(Keys.ENTER);
        simulateThinking(4000, 6000);
    }

    /* --- CHANNEL PHASE: TARGETED ABSOLUTE XPATH & RANDOMIZED NAMING --- */

    @Test(priority = 6, dependsOnMethods = "step5_FinalizeProvisioning")
    public void step6_OpenTextChannelModal() {
        By plusBtn = By.xpath("//*[@id='channels']/ul/li[3]/div/div[2]/div");
        clickHumanly(wait.until(ExpectedConditions.elementToBeClickable(plusBtn)));
    }

    @Test(priority = 7, dependsOnMethods = "step6_OpenTextChannelModal", description = "Deterministic Text Channel Provisioning")
    public void step7_ProvisionTextChannelName() {
        // 1. Stochastic Identifier: Generating random channel metadata
        String randomName = "logs-" + (random.nextInt(8999) + 1000);

        // 2. High-Fidelity Target: Utilizing absolute XPath for direct node access
        By inputLocator = By.xpath("/html/body/div[2]/div[2]/div/div[6]/div[2]/form/div/div/div/div[3]/main/div/div[2]/div[2]/div/div/input");
        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(inputLocator));

        // 3. Interaction Lock & State Clear
        clickHumanly(input);
        ((JavascriptExecutor) driver).executeScript("arguments[0].value = ''; arguments[0].dispatchEvent(new Event('input', { bubbles: true }));", input);
        input.sendKeys(Keys.chord(Keys.COMMAND, "a"), Keys.BACK_SPACE);

        // 4. Provisioning Execution
        typeHumanly(input, randomName);
        WebElement submit = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@type='submit' and contains(., 'Create')]")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", submit);
        System.out.println("Text Architecture: Provisioned " + randomName);
        simulateThinking(2000, 3000);
    }

    @Test(priority = 8, dependsOnMethods = "step7_ProvisionTextChannelName")
    public void step8_OpenVoiceChannelModal() {
        By plusBtn = By.xpath("//*[@id='channels']/ul/li[3]/div/div[2]/div");
        clickHumanly(wait.until(ExpectedConditions.elementToBeClickable(plusBtn)));
    }

    @Test(priority = 9, dependsOnMethods = "step8_OpenVoiceChannelModal", description = "Deterministic Voice Channel Provisioning")
    public void step9_ProvisionVoiceChannelName() {
        // 1. Radio Toggle: Hardened selection for Voice architecture
        WebElement voiceRadio = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[contains(text(), 'Voice')]/ancestor::div[contains(@role, 'radio') or contains(@class, 'radioItem')]")));
        clickHumanly(voiceRadio);
        simulateThinking(1500, 2000);

        // 2. Node Re-acquisition: Targeting the absolute XPath post-hydration
        By inputLocator = By.xpath("/html/body/div[2]/div[2]/div/div[6]/div[2]/form/div/div/div/div[3]/main/div/div[2]/div[2]/div/div/input");
        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(inputLocator));

        // 3. Interaction Lock & Random Metadata Injection
        String randomVoiceName = "voice-" + (random.nextInt(8999) + 1000);
        clickHumanly(input);
        input.sendKeys(Keys.chord(Keys.COMMAND, "a"), Keys.BACK_SPACE);
        typeHumanly(input, randomVoiceName);

        // 4. Commit Provisioning
        WebElement submit = driver.findElement(By.xpath("//button[@type='submit' and contains(., 'Create')]"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", submit);
        System.out.println("Voice Architecture: Provisioned " + randomVoiceName);
        simulateThinking(2000, 3000);
    }

    /* --- ADMINISTRATIVE & CLEANUP --- */

    @Test(priority = 10, dependsOnMethods = "step9_ProvisionVoiceChannelName")
    public void step10_ElevatePermissions() {
        By header = By.xpath("//header[contains(@class, 'header')]//*[contains(text(), '" + serverName + "')]");
        clickHumanly(wait.until(ExpectedConditions.elementToBeClickable(header)));
        clickHumanly(wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@role='menuitem' and contains(., 'Server Settings')]"))));
        clickHumanly(wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@role='tab' and contains(., 'Roles')]"))));
        clickHumanly(wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@aria-label='Create Role'] | //button[contains(., 'Create Role')]"))));
        WebElement roleInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@aria-label='Role name' or @value='new role']")));
        roleInput.sendKeys(Keys.chord(Keys.COMMAND, "a"), Keys.BACK_SPACE);
        typeHumanly(roleInput, "Lead Architect");
    }

    @Test(priority = 11, dependsOnMethods = "step10_ElevatePermissions")
    public void step11_ConfigurePermissionToggle() {
        clickHumanly(driver.findElement(By.xpath("//div[@role='tab' and contains(., 'Permissions')]")));
        WebElement search = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[placeholder='Search permissions']")));
        clickHumanly(search);
        typeHumanly(search, "Administrator");
        simulateThinking(1000, 1500);
        By toggleLocator = By.xpath("//span[text()='Administrator']/ancestor::div[contains(@class, 'container')]//input/following-sibling::div");
        WebElement toggle = wait.until(ExpectedConditions.elementToBeClickable(toggleLocator));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", toggle);
        clickHumanly(toggle);
        clickHumanly(wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(., 'Save Changes')]"))));
        driver.findElement(By.tagName("body")).sendKeys(Keys.ESCAPE);
        simulateThinking(1500, 2500);
    }

    @Test(priority = 12, dependsOnMethods = "step11_ConfigurePermissionToggle")
    public void step12_DeleteServerCleanup() {
        By header = By.xpath("//header[contains(@class, 'header')]//*[contains(text(), '" + serverName + "')]");
        clickHumanly(wait.until(ExpectedConditions.elementToBeClickable(header)));
        clickHumanly(wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@role='menuitem' and contains(., 'Server Settings')]"))));
        WebElement delTab = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[contains(., 'Delete Server') and @role='tab']")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", delTab);
        clickHumanly(delTab);
        WebElement confirm = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//label[contains(., 'ENTER SERVER NAME')]/following-sibling::input")));
        typeHumanly(confirm, serverName);
        clickHumanly(driver.findElement(By.xpath("//button[@type='submit' and contains(., 'Delete Server')]")));
        wait.until(ExpectedConditions.urlContains("channels/@me"));
        System.out.println("Lifecycle Complete.");
    }

    /* --- STEALTH ENGINES --- */

    private void clearInitialOverlays() {
        try {
            driver.findElement(By.tagName("body")).sendKeys(Keys.ESCAPE);
            ((JavascriptExecutor) driver).executeScript(
                    "var overlays = document.querySelectorAll('[class*=\"layer\"], [class*=\"modal\"], [class*=\"backdrop\"]');" +
                            "overlays.forEach(function(el) { el.remove(); });"
            );
        } catch (Exception ignored) {}
    }

    private void typeHumanly(WebElement element, String text) {
        for (char ch : text.toCharArray()) {
            long delay = (long) (95 + (random.nextGaussian() * 30));
            element.sendKeys(String.valueOf(ch));
            pause((int) Math.max(50, delay), (int) delay + 60);
        }
    }

    private void clickHumanly(WebElement element) {
        actions.moveToElement(element, random.nextInt(6)-3, random.nextInt(6)-3)
                .pause(Duration.ofMillis(random.nextInt(300) + 200))
                .click().perform();
    }

    private void simulateThinking(int min, int max) {
        try { Thread.sleep(random.nextInt(max - min) + min); } catch (InterruptedException ignored) {}
    }

    private void pause(int min, int max) {
        try { Thread.sleep(Math.abs(random.nextInt(max - min + 1)) + min); } catch (InterruptedException ignored) {}
    }
}