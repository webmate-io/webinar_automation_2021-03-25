package executescript_sdk;

import com.testfabrik.webmate.javasdk.WebmateCapabilityType;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;

import static helpers.Credentials.MY_WEBMATE_APIKEY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static helpers.Credentials.*;
import static helpers.Helpers.waitForElement;

public class ExecuteScriptSdkTest {
    @Test
    void performTest() throws MalformedURLException {

        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("browserName", "CHROME");
        caps.setCapability("version", "83");
        caps.setCapability("platform", "WINDOWS_7_64");
        caps.setCapability(WebmateCapabilityType.API_KEY, MY_WEBMATE_APIKEY);
        caps.setCapability(WebmateCapabilityType.USERNAME, MY_WEBMATE_USERNAME);
        caps.setCapability(WebmateCapabilityType.PROJECT, MY_WEBMATE_PROJECTID.toString());

        caps.setCapability("wm:autoScreenshots", true);

        RemoteWebDriver driver = new RemoteWebDriver(new URL(WEBMATE_SELENIUM_URL), caps);

        try {
            driver.get("http://www.examplepage.org/version/future");

            waitForElement(driver, By.id("goto-examplepage")).click();
            assertEquals("Cross Browser Issues Example", driver.getTitle());

            driver.get("http://www.examplepage.org/form_interaction");

            waitForElement(driver, By.id("lk")).click();

            driver.executeScript("wmTestRunSetName(\"Ein neuer Name für meinen Test\")");

            assertEquals("Link Clicked!", waitForElement(driver, By.cssSelector(".success")).getText());

            waitForElement(driver, By.id("bn")).click();

            waitForElement(driver, By.id("ck")).click();

            waitForElement(driver, By.id("rd")).click();

            waitForElement(driver, By.id("mover")).click();

            waitForElement(driver, By.id("text-input")).click();
            waitForElement(driver, By.id("text-input")).sendKeys("Test test");

            waitForElement(driver, By.id("area")).click();
            waitForElement(driver, By.id("area")).sendKeys("Here some more test");
        } finally {
            driver.quit();
        }
    }
}
