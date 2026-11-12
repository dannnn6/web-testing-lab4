package lab4;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.util.Map;
import java.util.List;

import java.time.Duration;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class VyatsuTest {

    WebDriver driver;
    WebDriverWait wait;

    final String URL = "https://old.vyatsu.ru";

    @BeforeEach
    void setup() {
        ChromeDriver chrome = new ChromeDriver();
        driver = chrome;
        driver.manage().window().maximize();

        chrome.executeCdpCommand("Network.enable", Map.of());
        chrome.executeCdpCommand("Network.setBlockedURLs", Map.of(
                "urls", List.of(
                        "*://mc.yandex.ru/*",
                        "*://openstat.net/*"
                )
        ));

        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    void quit() {
        if (driver != null) {
            driver.quit();
        }
    }

    void screenshot(String name) throws IOException {
        Path folder = Path.of("screenshots");

        byte[] image = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);

        Files.write(folder.resolve(name + ".png"), image);
    }

    @Test
    void titleTest() throws IOException {
        driver.get(URL);
        screenshot("vyatsu_titleTest");
        assertEquals("Официальный сайт ВятГУ", driver.getTitle());
    }

    @Test
    void svedeniyaTest() throws IOException {
        driver.get(URL);
        screenshot("vyatsu_svedeniyaTest_openPage");
        WebElement firstLi =
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"jsddm\"]/li[1]")));
        Actions action = new Actions(driver);
        action.moveToElement(firstLi).perform();
        screenshot("vyatsu_svedeniyaTest_openMenu");
        WebElement targetMenuItem =
                wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//*[@id=\"jsddm\"]/li[1]//a[contains(text(),\"Образовательные стандарты\")]")));
        targetMenuItem.click();
        wait.until(ExpectedConditions.titleContains("Сведения об ОО Образовательные стандарты и требования"));
        screenshot("vyatsu_svedeniyaTest_openNewPage");
        assertEquals("Сведения об ОО Образовательные стандарты и требования", driver.getTitle());
    }

    @Test
    void searchTest() throws IOException {
        driver.get(URL);
        screenshot("vyatsu_searchTest_openPage");
        WebElement searchString =
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@name=\"query\"]")));
        searchString.sendKeys("отпуск");
        screenshot("vyatsu_searchTest_writeText");
        searchString.sendKeys(Keys.ENTER);
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.tagName("body"), "Результаты поиска"));
        screenshot("vyatsu_searchTest_openResultPage");
        List<WebElement> results = wait.until(
                ExpectedConditions.numberOfElementsToBeMoreThan(
                        By.xpath("//h3[contains(., 'Результаты поиска')]/following-sibling::div//a"), 0)
        );

        assertFalse(results.isEmpty());
    }

    @Test
    void subscribeCorrectTest() throws IOException {
        driver.get(URL);
        screenshot("vyatsu_subscribeCorrectTest_openPage");
        WebElement stud = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//a[normalize-space(.)='Студенту']")));
        stud.click();
        screenshot("vyatsu_subscribeCorrectTest_openStudPage");
        WebElement searchString =
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"email-input\"]")));
        String email = "12345678@123.ru";
        searchString.sendKeys(email);
        screenshot("vyatsu_subscribeCorrectTest_writeText");
        searchString.sendKeys(Keys.ENTER);

        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        String message = alert.getText();
        alert.accept();
        assertEquals("Ваш адрес e-mail \"" + email + "\" успешно добавлен в список рассылки новостей", message.strip());
    }

    @Test
    void subscribeAlreadyTest() throws IOException {
        driver.get(URL);
        screenshot("vyatsu_subscribeAlreadyTest_openPage");
        WebElement stud = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//a[normalize-space(.)='Студенту']")));
        stud.click();
        screenshot("vyatsu_subscribeAlreadyTest_openStudPage");
        WebElement searchString =
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"email-input\"]")));
        String email = "12345678@123.ru";
        searchString.sendKeys(email);
        screenshot("vyatsu_subscribeAlreadyTest_writeText");
        searchString.sendKeys(Keys.ENTER);

        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        String message = alert.getText();
        alert.accept();
        assertEquals("Указанный e-mail уже находится в базе данных", message.strip());
    }

    @Test
    void subscribeIncorrectTest() throws IOException {
        driver.get(URL);
        screenshot("vyatsu_subscribeIncorrectTest_openPage");
        WebElement stud = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//a[normalize-space(.)='Студенту']")));
        stud.click();
        screenshot("vyatsu_subscribeIncorrectTest_openStudPage");
        WebElement searchString =
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"email-input\"]")));
        String email = "12345678";
        searchString.sendKeys(email);
        screenshot("vyatsu_subscribeIncorrectTest_writeText");
        searchString.sendKeys(Keys.ENTER);

        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        String message = alert.getText();
        alert.accept();
        assertEquals("Неправильно введен e-mail", message.strip());
    }
}