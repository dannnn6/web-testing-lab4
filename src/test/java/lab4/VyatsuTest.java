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
        driver = new ChromeDriver();
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

}