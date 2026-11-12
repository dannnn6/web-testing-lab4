package lab4;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OtzovikTest {

    WebDriver driver;
    WebDriverWait wait;

    final String URL = "https://otzovik.com";

    @BeforeEach
    void setup() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    void quit() {
        if (driver != null) {
            driver.quit();
        }
    }

    void screenshot(String name) throws IOException {
        Path folder = Path.of("screenshots/otzovik");
        Files.createDirectories(folder);
        byte[] image = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        Files.write(folder.resolve(name + ".png"), image);
    }

    @Test
    void yandexMetroSearchTest() throws IOException {
        driver.get(URL);
        screenshot("otzovik_yandexMetroSearchTest_openPage");

        WebElement search = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[@id=\"header-search-input\"]")
        ));
        search.sendKeys("яндекс");
        screenshot("otzovik_yandexMetroSearchTest_writeText");

        WebElement list = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[text()='Yandex Metro - программа для Android']")
        ));
        list.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h1[@class='product-name']")
        ));

        screenshot("otzovik_yandexMetroSearchTest_resultPage");

        List<WebElement> reviews = driver.findElements(By.xpath("//span[@class='votes']"));

        assertFalse(reviews.isEmpty());
    }

    @Test
    void incompleteReviewTest() throws IOException {
        driver.get(URL);
        screenshot("otzovik_incompleteReviewTest_openPage");

        WebElement writeReview = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(normalize-space(.), 'Написать отзыв')]")
        ));
        writeReview.click();
        wait.until(ExpectedConditions.urlContains("postreview.php"));
        screenshot("otzovik_incompleteReviewTest_startForm");

        WebElement objectInput = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//input[@id='tproduct']")
        ));
        objectInput.sendKeys("Ботанический сад Киров");
        screenshot("otzovik_incompleteReviewTest_objectName");

        WebElement garden = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(normalize-space(.), 'Ботанический сад ВятГУ')]")
        ));
        garden.click();
        screenshot("otzovik_incompleteReviewTest_reviewForm");

        WebElement preview = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[@id='previewbtn']")
        ));
        preview.click();

        WebElement errors = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//div[contains(normalize-space(.), 'Обнаружены ошибки, либо не все поля заполнены.')]")));

        screenshot("otzovik_incompleteReviewTest_validationErrors");

        assertTrue(errors.isDisplayed());
    }
}
