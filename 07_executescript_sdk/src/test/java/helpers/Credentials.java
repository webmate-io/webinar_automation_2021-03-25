package helpers;

import com.testfabrik.webmate.javasdk.ProjectId;

import java.util.UUID;

public class Credentials {
    public static String WEBMATE_SELENIUM_URL = "https://selenium.webmate.io/wd/hub";
    public static String MY_WEBMATE_USERNAME = "xxxx@xxxxxx.xxx";
    public static String MY_WEBMATE_APIKEY = "XXXX-XXXXX-XXXX-XXXXXX-XXXXX";
    public static ProjectId MY_WEBMATE_PROJECTID = new ProjectId(UUID.fromString("XXXXX-XXXXXX-XXXXXX-XXXXXX-XXXX"));
}

