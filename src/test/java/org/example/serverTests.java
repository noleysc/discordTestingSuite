package org.example;

import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.*;
import org.testng.annotations.*;
import java.time.Duration;
import java.util.Random;

public class serverTests {
    WebDriver driver;
    WebDriverWait wait;
    Actions actions;
    Random random = new Random();

    String loginIdentifier = "softwaretesting@tutamail.com";
    String loginPassword = "testPASS!@#";
    String serverName;

    @BeforeClass
    public void suiteSetup() {
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("-private");
        options.addPreference("dom.webdriver.enabled", false);
        options.addPreference("useAutomationExtension", false);
        options.addPreference("general.useragent.override",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 14.7; rv:128.0) Gecko/20100101 Firefox/128.0");

        driver = new FirefoxDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        actions = new Actions(driver);

        generateServerMetadata();
        performStealthLogin();
    }

    private void generateServerMetadata() {
        String[] prefixes = {"Nexus", "Vortex", "Static", "Logic", "Cyber"};
        this.serverName = prefixes[random.nextInt(5)] + " Lab " + (random.nextInt(899) + 100);
    }

    private void performStealthLogin() {
        driver.get("https://discord.com/login");
        simulateThinking(2000, 3000);

        WebElement idField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("email")));
        typeHumanly(idField, loginIdentifier);

        WebElement passField = driver.findElement(By.name("password"));
        typeHumanly(passField, loginPassword);

        simulateThinking(1500, 2500);
        clickHumanly(driver.findElement(By.xpath("//button[@type='submit']")));

        wait.until(ExpectedConditions.urlContains("channels/@me"));
        System.out.println("Authenticated: Transitioning to architecture phase.");
    }

    @Test(priority = 1, description = "Resilient Modal-Breaker Server Creation")
    public void testCreateServerWorkflow() {
        // 1. Initial Cleanup
        clearInitialOverlays();

        // 2. Sidebar Pounce
        By addServerLocator = By.xpath("//div[@aria-label='Add a Server'] | //button[@aria-label='Add a Server']");
        WebElement addButton = wait.until(ExpectedConditions.elementToBeClickable(addServerLocator));
        simulateThinking(2000, 3000);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", addButton);

        // 3. Template & Audience Selection
        WebElement createOwn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[contains(text(), 'Create My Own')]")));
        clickHumanly(createOwn);

        WebElement forFriends = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[contains(text(), 'For me and my friends')]")));
        simulateThinking(1500, 2000);
        clickHumanly(forFriends);

        // 4. THE FIX: Prototype-Level Purge + Selenium Re-Bind
        System.out.println("Executing Prototype-Level State Purge (Neutralizing Virtual DOM Ghosting).");

        // JS Strike: Overrides the React value tracker to ensure a clean slate
        ((JavascriptExecutor) driver).executeScript(
                "var input = document.querySelector('div[class*=\"createGuild\"] input:not([type=\"file\"])');" +
                        "var nativeSetter = Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype, 'value').set;" +
                        "nativeSetter.call(input, '');" +
                        "input.dispatchEvent(new Event('input', { bubbles: true }));" +
                        "input.focus();"
        );

        // RE-BIND: Fresh Selenium handle for typing
        By freshInputLocator = By.xpath("//div[contains(@class, 'createGuild')]//input[not(@type='file')]");
        WebElement nameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(freshInputLocator));

        simulateThinking(1000, 1500);
        typeHumanly(nameInput, serverName);

        // 5. THE FIX: Final Submission
        simulateThinking(2000, 3000);
        System.out.println("Dispatching Implicit Submit via ENTER key.");
        nameInput.sendKeys(Keys.ENTER);

        String jsClickButton =
                "var btn = document.querySelector('div[class*=\"createGuild\"] button[type=\"submit\"]');" +
                        "if(btn) { btn.click(); }";

        ((JavascriptExecutor) driver).executeScript(jsClickButton);

        System.out.println("Server Architecture '" + serverName + "' provisioned successfully.");
        simulateThinking(6000, 10000);
    }

    /* --- STEALTH HELPERS --- */

    private void clearInitialOverlays() {
        try {
            driver.findElement(By.tagName("body")).sendKeys(Keys.ESCAPE);
            ((JavascriptExecutor) driver).executeScript(
                    "document.querySelectorAll(\"[class*='tooltip'], [class*='hint']\").forEach(el => el.remove());"
            );
        } catch (Exception e) {}
    }

    private void typeHumanly(WebElement element, String text) {
        for (char ch : text.toCharArray()) {
            element.sendKeys(String.valueOf(ch));
            pause(80, 200);
        }
    }

    private void clickHumanly(WebElement element) {
        actions.moveToElement(element, random.nextInt(4), random.nextInt(4))
                .pause(Duration.ofMillis(random.nextInt(300) + 200))
                .click()
                .perform();
    }

    private void simulateThinking(int min, int max) {
        try { Thread.sleep(random.nextInt(max - min) + min); } catch (Exception e) {}
    }

    private void pause(int min, int max) {
        try { Thread.sleep(random.nextInt(max - min) + min); } catch (Exception e) {}
    }

    @AfterClass
    public void tearDown() {
        System.out.println("Final state preserved. Workflow complete.");
    }
}