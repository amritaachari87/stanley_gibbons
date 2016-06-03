package com.user.action;

import static com.user.action.TestUserRegistration.sleep;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import java.io.File;
import java.util.concurrent.TimeUnit;

public class TestProductSearch {

    private static final String BASE_URL = "https://www.stanleygibbons.com";
    private static final String VALID_NAME = "cbthzmop";
    private static final String VALID_PASSWORD = "Aaaaaa@10";

    private WebDriver driver;

    @BeforeClass
    public static void beforeClass() {
        System.setProperty("webdriver.chrome.driver", new File("resources/chromedriver").getAbsolutePath());
    }

    @Before
    public void setup() {
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        driver.get(BASE_URL);
    }

    @After
    public void tearDown() {
        driver.quit();
    }

    @Test
    public void testSearchProduct() {
        driver.switchTo().activeElement();
        if (driver.findElement(By.cssSelector("div[class='sg-modal-body welcome-modal text-center ng-scope']")) != null) {
            driver.findElement(By.cssSelector("button[class='close']")).click();
        }

        // had to click twice to get the Log In window
        // could not find the Log In element using PartialLinkText so used Xpath
        driver.findElement(By.xpath("//*[@id=\"header-top-icons\"]/ul/li[5]/a/p")).click();
        driver.findElement(By.xpath("//*[@id=\"header-top-icons\"]/ul/li[5]/a/p")).click();

        driver.switchTo().activeElement();
        driver.findElement(By.id("username")).sendKeys(VALID_NAME);
        driver.findElement(By.id("password")).sendKeys(VALID_PASSWORD);
        driver.findElement(By.id("_submit")).click();

        // wait for the element to be ready
        sleep(2000);

        driver.findElement(By.id("main-search")).sendKeys("Great Britain 1841 SG8 PL.127 Mint");
        driver.findElement(By.id("header-search-icon")).click();
    }
}
