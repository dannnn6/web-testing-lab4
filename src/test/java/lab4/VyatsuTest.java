package lab4;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import java.util.Map;
import java.util.List;

import java.time.Duration;
import java.util.Objects;

import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.OutputType;

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
        Files.createDirectories(folder);

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
}