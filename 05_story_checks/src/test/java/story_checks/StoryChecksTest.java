package story_checks;

import com.testfabrik.webmate.javasdk.*;
import com.testfabrik.webmate.javasdk.browsersession.BrowserSessionRef;
import com.testfabrik.webmate.javasdk.selenium.WebmateSeleniumSession;
import com.testfabrik.webmate.javasdk.testmgmt.TestRun;
import com.testfabrik.webmate.javasdk.testmgmt.TestRunEvaluationStatus;
import com.testfabrik.webmate.javasdk.testmgmt.spec.StoryCheckSpec;
import helpers.Credentials;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.MalformedURLException;
import java.net.URL;

import static helpers.Credentials.MY_WEBMATE_APIKEY;
import static helpers.Credentials.*;
import static helpers.Helpers.waitForElement;
import static org.junit.Assert.assertEquals;

import com.testfabrik.webmate.javasdk.*;
import com.testfabrik.webmate.javasdk.browsersession.*;
import com.testfabrik.webmate.javasdk.selenium.WebmateSeleniumSession;
import com.testfabrik.webmate.javasdk.testmgmt.*;
import com.testfabrik.webmate.javasdk.testmgmt.spec.StoryCheckSpec;
import org.junit.*;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.MalformedURLException;
import java.net.URL;

public class StoryChecksTest  {

    // Share the selenium driver between executions of tests
    private static RemoteWebDriver driver;

    @BeforeClass
    public static void setup() throws MalformedURLException {
        // create the selenium driver
        setupSeleniumSession(BrowserType.CHROME.toString(), "86", "WINDOWS_7_64");

        // setup the webmate session
        // if this call and the corresponding teardown call are removed, the selenium test executes just fine
        setupWebmateSession();
    }

    @AfterClass
    public static void tearDown() {
        try {
            // teardown the webmate session and report the result to webmate
            // if this call and the corresponding setup call are removed, the selenium test executes just fine
            teardownWebmateSession();
        } finally {
            driver.quit();
        }
    }

    /** Utility method to setup the selenium driver. It uses plain Selenium and sets
     *  some capabilities needed to connect to webmate.
     */
    private static void setupSeleniumSession(String browserName, String browserVersion,
                                             String browserPlatform) throws MalformedURLException {

        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("browserName", browserName);
        caps.setCapability("version", browserVersion);
        caps.setCapability("platform", browserPlatform);
        caps.setCapability(WebmateCapabilityType.API_KEY, MY_WEBMATE_APIKEY);
        caps.setCapability(WebmateCapabilityType.USERNAME, MY_WEBMATE_USERNAME);
        caps.setCapability(WebmateCapabilityType.PROJECT, MY_WEBMATE_PROJECTID.toString());
        // See com.testfabrik.webmate.javasdk.WebmateCapabilityType for webmate specific capabilities
        caps.setCapability("wm:autoScreenshots", true);
        caps.setCapability("wm:name", "Story Check Test");
        caps.setCapability("wm:tags", "webinar=automation");

        driver = new RemoteWebDriver(new URL(WEBMATE_SELENIUM_URL), caps);
    }

    @Test
    public void redirectTest() {
        driver.get("http://www.examplepage.org/version/future");

        System.out.println("Selecting some elements....");
        WebDriverWait wait = new WebDriverWait(driver, 20);
//        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".container"))).click();

        browserSession.createState("after click");

        System.out.println("Clicking on something that will redirect us...");
        waitForElement(driver, By.id("goto-examplepage")).click();
        assertEquals("Cross Browser Issues Example", driver.getTitle());

    }

    @Test
    public void formularTest() {
        driver.get("http://www.examplepage.org/form_interaction");

        System.out.println("Click on link");
        waitForElement(driver, By.id("lk")).click();
        assertEquals("Link Clicked!", waitForElement(driver, By.cssSelector(".success")).getText());

        browserSession.createState("after link");

        browserSession.withAction("Click on button", action -> {
            System.out.println("Clicking on Button");
            waitForElement(driver, By.id("bn")).click();
        });

        browserSession.withAction("Click on Checkbox", action -> {
            System.out.println("Clicking on Checkbox");
            waitForElement(driver, By.id("ck")).click();
            action.finishActionAsFailure("Not really a failure but anyway");
        });

        browserSession.withAction("Click on Radiobutton", action -> {
            System.out.println("Clicking on RadioButton");
            waitForElement(driver, By.id("rd")).click();
            browserSession.createState("after radio button");
        });

        System.out.println("Clicking on Element with a Hover Event");
        waitForElement(driver, By.id("mover")).click();

        System.out.println("Entering some Text...");
        waitForElement(driver, By.id("text-input")).click();
        waitForElement(driver, By.id("text-input")).sendKeys("Test test");

        System.out.println("Entering more Text...");
        waitForElement(driver, By.id("area")).click();
        waitForElement(driver, By.id("area")).sendKeys("Here some more test");

    }

    /** ============= Code that interacts with the webmate SDK =============== */

    private static WebmateAPISession webmateSession;

    private static WebmateSeleniumSession seleniumSession;

    private static BrowserSessionRef browserSession;

    private static boolean hasAtLeaseOneTestFailed = false;

    /** Authenticate with the webmate SDK and setup selenium and browsersessions */
    private static void setupWebmateSession() {
        WebmateAuthInfo authInfo = new WebmateAuthInfo(Credentials.MY_WEBMATE_USERNAME, Credentials.MY_WEBMATE_APIKEY);
        webmateSession = new WebmateAPISession(
                authInfo,
                WebmateEnvironment.create(),
                MY_WEBMATE_PROJECTID);

        seleniumSession = webmateSession.addSeleniumSession(driver.getSessionId().toString());

        browserSession = webmateSession.browserSession.getBrowserSessionForSeleniumSession(driver.getSessionId().toString());
    }

    /** Finish the webmate session and report the test run status to webmate. */
    private static void teardownWebmateSession() {
        System.out.println("Finishing test run");
        if (hasAtLeaseOneTestFailed) {
            seleniumSession.finishTestRun(TestRunEvaluationStatus.FAILED, "At least one operation has failed");
        } else {
            seleniumSession.finishTestRun(TestRunEvaluationStatus.PASSED, "Successful.");
        }

    }

    /** This is a simple JUnit Test Rule that starts and finishes
     *  StoryChecks in the webmate session. It uses the JUnit display name
     *  for the test run name. If a test fails, the corresponding StoryCheck
     *  is also failed.
     */
    @Rule
    public final TestRule actionRule = new TestWatcher() {
        TestRun currentTest;

        @Override
        public Statement apply(Statement base, Description description) {
            return super.apply(base, description);
        }

        @Override
        protected void succeeded(Description description) {
            this.currentTest.finish(TestRunEvaluationStatus.PASSED);
        }

        @Override
        protected void failed(Throwable e, Description description) {
            String errorText = description.getDisplayName() + " has failed: " + e.getMessage();
            this.currentTest.finish(TestRunEvaluationStatus.FAILED, errorText);
            hasAtLeaseOneTestFailed = true;
        }

        @Override
        protected void starting(Description description) {
            this.currentTest = webmateSession.testMgmt.startExecutionWithBuilder(
                    StoryCheckSpec.StoryCheckBuilder.builder(description.getDisplayName()));
        }
    };

}
