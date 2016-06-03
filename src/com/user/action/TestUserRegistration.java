package com.user.action;

import com.user.exception.EmailAccountExistException;
import com.user.exception.InvalidPasswordException;
import com.user.exception.UserExistException;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TestUserRegistration {

    private static final String BASE_URL = "https://www.stanleygibbons.com";

    private static String VALID_PASSWORD = "Aaaaaa@10";
    private static String INVALID_PASSWORD = "1";

    @BeforeClass
    public static void beforeClass() {
        System.setProperty("webdriver.chrome.driver", new File("resources/chromedriver").getAbsolutePath());
    }

    @Before
    public void setup() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testSuccessfulUserRegistration() throws Exception {
        String username = generateUsername();
        registerUser(BASE_URL, "Hello", "Kitty", username+"@gmail.com", username, VALID_PASSWORD);
    }

    @Test(expected = InvalidPasswordException.class)
    public void testUserRegistrationWithInvalidPassword() throws Exception {
        String username = generateUsername();
        registerUser(BASE_URL, "Hello", "Kitty", username+"@gmail.com", username, INVALID_PASSWORD);
    }

    @Test(expected = UserExistException.class)
    public void testUserRegistrationWithExistingUsername() throws Exception {
        String duplicateUsername = generateUsername();
        registerUser(BASE_URL, "Hello", "Kitty", generateUsername()+"@gmail.com", duplicateUsername, VALID_PASSWORD);
        registerUser(BASE_URL, "Hello", "Kitty", generateUsername()+"@gmail.com", duplicateUsername, VALID_PASSWORD);
    }

    @Test(expected = EmailAccountExistException.class)
    public void testUserRegistrationWithExistingEmail() throws Exception {
        String duplicateEmail = generateUsername()+"@gmail.com";
        registerUser(BASE_URL, "Hello", "Kitty", duplicateEmail, generateUsername(), VALID_PASSWORD);
        registerUser(BASE_URL, "Hello", "Kitty", duplicateEmail, generateUsername(), VALID_PASSWORD);
    }


    private void registerUser(String baseURL, String firstName, String lastName, String email, String username, String password)
            throws EmailAccountExistException, UserExistException, InvalidPasswordException {

        WebDriver driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

        driver.get(baseURL);

        driver.switchTo().activeElement();

        // it randomly opens the welcome window not always
        if (driver.findElement(By.cssSelector("div[class='sg-modal-body welcome-modal text-center ng-scope']")) != null) {
            driver.findElement(By.cssSelector("button[class='close']")).click();
        }

        //Had to click twice to get the Log In window
        driver.findElement(By.partialLinkText("REGISTER")).click();
        driver.findElement(By.partialLinkText("REGISTER")).click();

        driver.switchTo().activeElement();

        driver.findElement(By.id("sg_user_registration_firstName")).sendKeys(firstName);
        driver.findElement(By.id("sg_user_registration_lastName")).sendKeys(lastName);
        driver.findElement(By.id("sg_user_registration_email")).sendKeys(email);
        driver.findElement(By.id("sg_user_registration_username")).sendKeys(username);
        driver.findElement(By.id("sg_user_registration_plainPassword")).sendKeys(password);
        driver.findElement(By.id("registration-submit")).click();

        List<WebElement> errorElements = null;

        try {
            // wait for the element to be ready
            sleep(2000);

            errorElements = driver.findElements(By.cssSelector("div[class='sg-modal__error-text text-danger']"));
            for (WebElement errorElement : errorElements) {
                if (errorElement.getText().contains("email")) {
                    driver.quit();
                    throw new EmailAccountExistException();
                }
            }

            errorElements = driver.findElements(By.cssSelector("div[class='sg-modal__error-text small text-danger']"));
            for (WebElement errorElement : errorElements) {
                if (errorElement.getText().contains("The username is already in use")) {
                    driver.quit();
                    throw new UserExistException();
                }
                if (errorElement.getText().contains("Your password is too short")
                        || errorElement.getText().contains("Increase strength with numbers, letters or special characters")) {
                    driver.quit();
                    throw new InvalidPasswordException();
                }
            }
        } catch (NoSuchElementException e) {
            // do nothing. successful scenario.
        }
        driver.quit();
    }

    protected static void sleep(long milliSeconds) {
        try {
            Thread.sleep(milliSeconds);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private String generateUsername() {
        String ALPHA_NUMERIC_STRING = "abcdefghijklmnopqrstuvwxyz";
        int count = 8;
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }
}
