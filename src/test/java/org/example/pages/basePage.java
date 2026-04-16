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
        logger.debug("Performing simplified human click on: {}", element);
        try {
            // 1. Move to element with slight jitter
            actions.moveToElement(element, random.nextInt(4) - 2, random.nextInt(4) - 2)
                    .pause(Duration.ofMillis(random.nextInt(50) + 30))
                    .perform();
            
            // 2. Standard atomic click
            element.click();
            logger.debug("Standard click successful.");
            
            // 3. Optional move away
            if (moveAway) {
                actions.moveByOffset(random.nextInt(50) + 20, random.nextInt(50) + 20)
                        .perform();
            }
        } catch (Exception e) {
            logger.warn("Standard click failed, using JS click fallback: {}", e.getMessage());
            try {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
            } catch (Exception e2) {
                logger.error("All click attempts failed.");
            }
        }
    }

    protected void typeHumanly(WebElement element, String text) {
        logger.debug("Typing '{}' into element: {}", text, element);
        
        // Initial "thinking" delay before starting to type
        simulateThinking(400, 900);

        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            
            // 3% chance of a "typo" if it's an alphanumeric character
            if (random.nextInt(100) < 3 && Character.isLetterOrDigit(ch)) {
                char typo = (char) (ch + (random.nextBoolean() ? 1 : -1));
                element.sendKeys(String.valueOf(typo));
                simulateThinking(150, 300); // Quick pause after mistake
                element.sendKeys(Keys.BACK_SPACE);
                simulateThinking(200, 400); // Pause before correction
            }
            
            element.sendKeys(String.valueOf(ch));
            
            // Human rhythm: Gaussian jitter + occasional longer pauses between "chunks"
            long delay = (long) (105 + (random.nextGaussian() * 35));
            if (ch == '@' || ch == '.' || random.nextInt(100) < 5) {
                simulateThinking(200, 500); // Pause at natural break points or randomly
            } else {
                simulateThinking((int) Math.max(65, delay), (int) delay + 45);
            }
        }
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

    protected void clearOverlays() {
        try {
            driver.findElement(By.tagName("body")).sendKeys(Keys.ESCAPE);
            ((JavascriptExecutor) driver).executeScript(
                "// Dismiss buttons first\n" +
                "document.querySelectorAll('button').forEach(btn => {\n" +
                "  const text = btn.textContent.toLowerCase();\n" +
                "  if (text.includes('got it') || text.includes('skip') || text.includes('dismiss') || text.includes('maybe later')) {\n" +
                "    try { btn.click(); } catch(e) {}\n" +
                "  }\n" +
                "});\n" +
                "var obstructions = document.querySelectorAll('div[class*=\"layerContainer\"], div[class*=\"backdrop\"], div[class*=\"modal\"], div[class*=\"tooltip\"]');" +
                "obstructions.forEach(function(el) { " +
                "  try { el.style.display = 'none'; el.remove(); } catch(e) {} " +
                "});" +
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
