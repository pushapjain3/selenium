package tests;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.*;
import org.testng.Assert;
import org.testng.ITestResult;

import utils.ExtentReportManager;
import pages.LoginPage;

import com.aventstack.extentreports.*;

public class LoginTests {
    WebDriver driver;
    LoginPage loginPage;
    ExtentReports extent;
    ExtentTest test;

    @BeforeClass
    public void startReport() {
        extent = ExtentReportManager.getInstance();
    }

    @BeforeMethod
    public void setup() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("https://www.saucedemo.com/");
        loginPage = new LoginPage(driver);
    }

    @Test(dataProvider = "loginData")
    public void loginWithMultipleCredentials(String username, String password, boolean isValid) {
        test = extent.createTest("Login test with user: " + username);

        loginPage.enterUsername(username);
        loginPage.enterPassword(password);
        loginPage.clickLogin();

        if (isValid) {
            boolean result = driver.getCurrentUrl().contains("inventory");
            test.info("Expected valid login. Actual URL: " + driver.getCurrentUrl());
            Assert.assertTrue(result, "Valid login failed");
        } else {
            boolean result = driver.getCurrentUrl().contains("saucedemo");
            test.info("Expected invalid login. Actual URL: " + driver.getCurrentUrl());
            Assert.assertTrue(result, "Invalid login didn't redirect to login page");
        }
    }

    @DataProvider(name = "loginData")
    public Object[][] getData() {
        return new Object[][] {
            {"standard_user", "secret_sauce", true},
            {"invalid_user", "wrong_pass", false},
            {"", "", false},
            {"locked_out_user", "secret_sauce", false}
        };
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            test.fail(result.getThrowable());
        } else if (result.getStatus() == ITestResult.SUCCESS) {
            test.pass("Test passed");
        } else {
            test.skip("Test skipped");
        }
        driver.quit();
    }

    @AfterClass
    public void endReport() {
        extent.flush();
    }
}
