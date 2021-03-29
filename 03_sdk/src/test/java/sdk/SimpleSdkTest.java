package sdk;

import com.testfabrik.webmate.javasdk.WebmateAPISession;
import com.testfabrik.webmate.javasdk.WebmateAuthInfo;
import com.testfabrik.webmate.javasdk.WebmateCapabilityType;
import com.testfabrik.webmate.javasdk.WebmateEnvironment;
import com.testfabrik.webmate.javasdk.selenium.WebmateSeleniumSession;
import com.testfabrik.webmate.javasdk.testmgmt.TestRunEvaluationStatus;
import helpers.Credentials;
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

public class SimpleSdkTest {
    @Test
    void performTest() throws MalformedURLException {

        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability(WebmateCapabilityType.API_KEY, MY_WEBMATE_APIKEY);
        caps.setCapability(WebmateCapabilityType.USERNAME, MY_WEBMATE_USERNAME);
        caps.setCapability(WebmateCapabilityType.PROJECT, MY_WEBMATE_PROJECTID.toString());

        caps.setCapability("browserName", "CHROME");
        caps.setCapability("platform", "WINDOWS_7_64");
        caps.setCapability("version", "84");

        caps.setCapability("wm:name", "SDK Test");

        RemoteWebDriver driver = new RemoteWebDriver(new URL(WEBMATE_SELENIUM_URL), caps);

        WebmateAPISession webmateSession = new WebmateAPISession(
                new WebmateAuthInfo(Credentials.MY_WEBMATE_USERNAME, Credentials.MY_WEBMATE_APIKEY),
                WebmateEnvironment.create(),
                MY_WEBMATE_PROJECTID);

        WebmateSeleniumSession seleniumSession = webmateSession.addSeleniumSession(driver.getSessionId().toString());

        try {
            driver.get("http://www.examplepage.org/version/future");

            waitForElement(driver, By.id("goto-examplepage")).click();
            assertEquals("Cross Browser Issues Example", driver.getTitle());

            driver.get("http://www.examplepage.org/form_interaction");

            waitForElement(driver, By.id("lk")).click();
            assertEquals("Link Clicked!", waitForElement(driver, By.cssSelector(".doesnotexist")).getText());

            waitForElement(driver, By.id("bn")).click();

            waitForElement(driver, By.id("ck")).click();

            waitForElement(driver, By.id("rd")).click();

            waitForElement(driver, By.id("mover")).click();

            waitForElement(driver, By.id("text-input")).click();
            waitForElement(driver, By.id("text-input")).sendKeys("Test test");

            waitForElement(driver, By.id("area")).click();
            waitForElement(driver, By.id("area")).sendKeys("Here some more test");
        } catch (Throwable e) {
            seleniumSession.finishTestRun(TestRunEvaluationStatus.FAILED,
                    "Ein Fehler ist aufgetreten: " + e.getMessage());
        } finally {
            driver.quit();
        }
    }
}
