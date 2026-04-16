package org.example.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Random;

public abstract class basePage {
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected Actions actions;
    protected Random random = new Random();
    protected static final Logger logger = LoggerFactory.getLogger(basePage.class);

    public basePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        this.actions = new Actions(driver);
        PageFactory.initElements(driver, this);
    }

    protected void clickHumanly(WebElement element) {
        clickHumanly(element, false);
    }

    protected void clickHumanly(WebElement element, boolean moveAway) {
        if (element == null) {
            logger.error("Attempted to click a null element");
            throw new IllegalArgumentException("Cannot click a null element");
        }
        logger.debug("Performing Titanium Stealth Robot click on: {}", element);
        try {
            // 1. Ensure element is fully in view
            ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block: 'center', inline: 'center', behavior: 'instant'});", element
            );
            simulateThinking(800, 1200);

            // 2. Attempt Hardware-Level Click using Java AWT Robot
            try {
                java.awt.Robot robot = new java.awt.Robot();
                
                // Get window position and viewport offsets
                Point windowPos = driver.manage().window().getPosition();
                Long innerHeight = (Long) ((JavascriptExecutor) driver).executeScript("return window.innerHeight;");
                Long outerHeight = (Long) ((JavascriptExecutor) driver).executeScript("return window.outerHeight;");
                Long innerWidth = (Long) ((JavascriptExecutor) driver).executeScript("return window.innerWidth;");
                Long outerWidth = (Long) ((JavascriptExecutor) driver).executeScript("return window.outerWidth;");
                
                int yOffset = (int) (outerHeight - innerHeight); // Top browser chrome (tabs, address bar)
                int xOffset = (int) (outerWidth - innerWidth) / 2; // Side borders
                
                Rectangle rect = element.getRect();
                
                // Calculate absolute OS coordinates with slight jitter off-center
                int targetX = windowPos.getX() + xOffset + rect.getX() + (rect.getWidth() / 2) + (random.nextInt(10) - 5);
                int targetY = windowPos.getY() + yOffset + rect.getY() + (rect.getHeight() / 2) + (random.nextInt(10) - 5);
                
                robot.mouseMove(targetX, targetY);
                simulateThinking(150, 300);
                
                robot.mousePress(java.awt.event.InputEvent.BUTTON1_DOWN_MASK);
                simulateThinking(50, 120); // Human click hold time
                robot.mouseRelease(java.awt.event.InputEvent.BUTTON1_DOWN_MASK);
                
                logger.debug("Robot click executed at absolute {},{}", targetX, targetY);

                if (moveAway) {
                    simulateThinking(200, 500);
                    robot.mouseMove(targetX + random.nextInt(200) - 100, targetY + random.nextInt(200) - 100);
                }
                return; // Success!
            } catch (Exception robotEx) {
                logger.warn("Robot hardware click failed, falling back to Actions: {}", robotEx.getMessage());
            }

            // 3. Fallback to Selenium Actions
            int xOffset = (int) (element.getRect().getWidth() * (0.3 + random.nextDouble() * 0.4));
            int yOffset = (int) (element.getRect().getHeight() * (0.3 + random.nextDouble() * 0.4));

            actions.moveToElement(element, xOffset - (element.getRect().getWidth()/2), yOffset - (element.getRect().getHeight()/2))
                    .pause(Duration.ofMillis(random.nextInt(100) + 50))
                    .clickAndHold()
                    .pause(Duration.ofMillis(random.nextInt(80) + 40))
                    .release()
                    .perform();
            
        } catch (Exception e) {
            logger.warn("Actions click failed, using Deep JS Bubbling Fallback: {}", e.getMessage());
            try {
                // Deep JS click that bubbles properly for React
                ((JavascriptExecutor) driver).executeScript(
                    "const el = arguments[0];" +
                    "const event = new MouseEvent('click', { view: window, bubbles: true, cancelable: true, buttons: 1 });" +
                    "el.dispatchEvent(event);", element
                );
            } catch (Exception e2) {
                logger.error("All click attempts failed for element.");
                throw new RuntimeException("Click failed: " + e2.getMessage());
            }
        }
    }

    protected void typeHumanly(WebElement element, String text) {
        logger.debug("Performing Gaussian typing for: {}", text);
        simulateThinking(600, 1200);

        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            
            // 2% chance of a "hesitation" pause
            if (random.nextInt(100) < 2) simulateThinking(400, 800);
            
            // 1% chance of a typo on long strings
            if (text.length() > 5 && random.nextInt(100) < 1 && Character.isLetterOrDigit(ch)) {
                element.sendKeys(String.valueOf((char)(ch + 1)));
                simulateThinking(100, 250);
                element.sendKeys(Keys.BACK_SPACE);
                simulateThinking(150, 350);
            }

            element.sendKeys(String.valueOf(ch));
            
            // Human rhythm: Gaussian distribution around 120ms
            double delay = (random.nextGaussian() * 40) + 120;
            simulateThinking((int) Math.max(45, delay), (int) delay + 30);

            // Longer pause after punctuation or spaces
            if (ch == ' ' || ch == '.' || ch == '-') simulateThinking(150, 450);
        }
    }

    protected void injectStealth() {
        try {
            ((JavascriptExecutor) driver).executeScript(
                "Object.defineProperty(navigator, 'webdriver', {get: () => undefined});" +
                "window.chrome = { runtime: {} };" +
                "Object.defineProperty(navigator, 'languages', {get: () => ['en-US', 'en']});" +
                "Object.defineProperty(navigator, 'plugins', {get: () => [1, 2, 3, 4, 5]});"
            );
        } catch (Exception ignored) {}
    }

    protected void simulateThinking(int min, int max) {
        if (max <= min) {
            max = min + 100;
        }
        try {
            Thread.sleep(random.nextInt(max - min + 1) + min);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void clearOverlays() {
        try {
            driver.findElement(By.tagName("body")).sendKeys(Keys.ESCAPE);
            ((JavascriptExecutor) driver).executeScript(
                "// Dismiss buttons first with expanded text matches\n" +
                "document.querySelectorAll('button').forEach(btn => {\n" +
                "  const text = btn.textContent.toLowerCase();\n" +
                "  if (text.includes('got it') || text.includes('skip') || text.includes('dismiss') || \n" +
                "      text.includes('maybe later') || text.includes('no thanks') || text.includes('i\\'ll do it later')) {\n" +
                "    try { btn.click(); } catch(e) {}\n" +
                "  }\n" +
                "});\n" +
                "// Remove obstructing layers\n" +
                "var obstructions = document.querySelectorAll('div[class*=\"layerContainer\"], div[class*=\"backdrop\"], div[class*=\"modal\"], div[class*=\"tooltip\"], [class*=\"focusLock\"]');" +
                "obstructions.forEach(function(el) { " +
                "  try { el.style.display = 'none'; el.remove(); } catch(e) {} " +
                "});" +
                "// Re-enable pointer events on the main app\n" +
                "var mount = document.querySelector('#app-mount');" +
                "if(mount) { mount.style.pointerEvents = 'auto'; mount.style.opacity = '1'; }"
            );
            logger.debug("UI Overlays cleared via JS.");
        } catch (Exception ignored) {}
    }

    protected void waitForPageLoad() {
        wait.until(webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
    }

    protected WebElement waitForClickable(WebElement element) {
        return wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    protected WebElement waitForVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }
}
