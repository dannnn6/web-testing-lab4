
package lab4;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class CdsTest {

    WebDriver driver;
    WebDriverWait wait;

    final String URL = "https://m.cdsvyatka.com/";

    @BeforeEach
    void setup() {
        ChromeDriver chrome = new ChromeDriver();
        driver = chrome;

        chrome.executeCdpCommand("Network.enable", Map.of());
        chrome.executeCdpCommand("Network.setBlockedURLs", Map.of(
                "urls", List.of(
                        "*://yandex.ru/ads/system/context.js"
                )
        ));

        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @AfterEach
    void quit() {
        if (driver != null) {
            driver.quit();
        }
    }

    void screenshot(String name) throws IOException {
        Path folder = Path.of("screenshots/cds");

        byte[] image = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);

        Files.write(folder.resolve(name + ".png"), image);
    }

    @Test
    void busMapTest() throws IOException {
        driver.get(URL);

        wait.until(d -> !new Select(d.findElement(By.id("id_CitySelect"))).getOptions().isEmpty());

        new Select(driver.findElement(By.id("id_CitySelect"))).selectByValue("1");

        wait.until(d -> new Select(d.findElement(By.id("id_MarshSelect")))
                .getOptions().stream()
                .anyMatch(e -> "1001".equals(e.getAttribute("value"))));

        new Select(driver.findElement(By.id("id_MarshSelect"))).selectByValue("1001");

        screenshot("cds_busMapTest_selection");

        driver.findElement(By.id("id_buttonMap")).click();

        wait.until(ExpectedConditions.urlContains("mobile_map.php"));
        screenshot("cds_busMapTest_openMap");

        // Ожидаем появления хотя бы одного автобуса
        WebDriverWait busWait = new WebDriverWait(
                driver, Duration.ofSeconds(40));

        busWait.until(d -> Boolean.TRUE.equals(
                ((JavascriptExecutor) d).executeScript(
                        "return typeof gBusMarker_List !== 'undefined' " +
                                "&& Object.values(gBusMarker_List).some(" +
                                "b => b.marker && map.hasLayer(b.marker));"
                )
        ));

        // Открываем карточку первого автобуса
        ((JavascriptExecutor) driver).executeScript(
                "Object.values(gBusMarker_List)" +
                        ".find(b => b.marker && map.hasLayer(b.marker))" +
                        ".marker.fire('click');"
        );

        WebElement info = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.className("leaflet-popup-content")));

        screenshot("cds_busMapTest_busInfo");

        assertFalse(info.getText().isBlank());
    }

    @Test
    void predictionTest() throws IOException {
        driver.get(URL);

        wait.until(d -> !new Select(d.findElement(By.id("id_CitySelect"))).getOptions().isEmpty());

        new Select(driver.findElement(By.id("id_CitySelect"))).selectByValue("1");

        wait.until(d -> new Select(d.findElement(By.id("id_MarshSelect")))
                .getOptions().stream()
                .anyMatch(e -> "1001".equals(e.getAttribute("value"))));

        new Select(driver.findElement(By.id("id_MarshSelect"))).selectByValue("1001");

        wait.until(d -> new Select(d.findElement(By.id("id_StopSelect")))
                .getOptions().stream()
                .anyMatch(e -> "181".equals(e.getAttribute("value"))));

        new Select(driver.findElement(By.id("id_StopSelect"))).selectByValue("181");

        screenshot("cds_predictionTest_selection");

        driver.findElement(By.id("id_buttonPrediction")).click();

        wait.until(ExpectedConditions.urlContains("prediction.php"));

        WebElement row = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//tr[td[contains(@class,'minutes')]]")));

        screenshot("cds_predictionTest_result");

        List<WebElement> cells = row.findElements(By.tagName("td"));

        assertTrue(cells.size() >= 4);
        assertTrue(cells.get(1).getText().contains("мин."));
        assertFalse(cells.get(3).getText().isBlank());
    }

}
