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
import java.util.Objects;

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

    @Test
    void completeReviewPreviewTest() throws IOException {
        driver.get(URL);
        screenshot("otzovik_completeReviewPreviewTest_openPage");

        // Открываем создание отзыва
        WebElement writeReview = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(normalize-space(.), 'Написать отзыв')]")
        ));
        writeReview.click();

        wait.until(ExpectedConditions.urlContains("postreview.php"));
        screenshot("otzovik_completeReviewPreviewTest_startForm");

        // Ищем ботанический сад
        WebElement objectInput = wait.until(ExpectedConditions.elementToBeClickable(By.id("tproduct")));
        objectInput.sendKeys("Ботанический сад Киров");

        WebElement garden = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(normalize-space(.), 'Ботанический сад ВятГУ')]")
        ));
        garden.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("h2.product-name")
        ));
        screenshot("otzovik_completeReviewPreviewTest_reviewForm");

        // Общая оценка
        WebElement rating = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector(".rating[data-name='rating0'] svg[data-value='5']")
        ));
        rating.click();

        // Заголовок отзыва
        WebElement title = driver.findElement(By.id("content_title"));
        title.sendKeys("Красивый ботанический сад для прогулок");

        // Текст отзыва
        WebElement body = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#content_body_main .editor-content textarea.text_area_auto_height_ta")));
        body.sendKeys("Это тестовый текст для проверки формы создания отзыва, он не несет особой смысловой нагрузки и не будет публиковаться, если я случайно не отправлю отзыв");

        // Плюсы
        WebElement pros = driver.findElement(By.name("content_pros"));
        pros.sendKeys("Красивое место, удобное расположение");

        // Минусы
        WebElement cons = driver.findElement(
                By.name("content_cons")
        );
        cons.sendKeys("Недостатков нет");

        // Рекомендация
        WebElement recommend = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("label:has(input[name='recommend'][value='Y'])")));
        recommend.click();

        screenshot("otzovik_completeReviewPreviewTest_filledForm");

        // Предпросмотр
        WebElement preview = wait.until(ExpectedConditions.elementToBeClickable(By.id("previewbtn")));
        preview.click();

        // Ждём появления содержимого предпросмотра
        WebElement previewResult = wait.until(driver -> {
            WebElement element = driver.findElement(By.id("preview_review"));

            if (element.isDisplayed() && !element.getText().isBlank()) {
                return element;
            }

            return null;
        });

        screenshot("otzovik_completeReviewPreviewTest_preview");

        // Проверяем предпросмотр
        assertTrue(previewResult.isDisplayed());
        assertFalse(previewResult.getText().isBlank());
        assertTrue(Objects.requireNonNull(driver.findElement(By.id("content")).getAttribute("class")).contains("preview-review"));
    }
}
