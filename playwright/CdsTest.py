
from pathlib import Path
import re
from playwright.sync_api import Page, expect

URL = "https://m.cdsvyatka.com/"

Path("screenshots/cds_PY").mkdir(parents=True, exist_ok=True)


def test_bus_map(page: Page):
    page.route("**/yandex.ru/ads/system/context.js",
               lambda route: route.abort())

    page.goto(URL)

    page.wait_for_function(
        "() => document.querySelector('#id_CitySelect').options.length > 0"
    )

    page.locator("#id_CitySelect").select_option("1")

    page.locator("#id_MarshSelect option[value='1001']").wait_for(
        state="attached"
    )

    page.locator("#id_MarshSelect").select_option("1001")

    page.screenshot(path="screenshots/cds_PY/cds_busMapTest_selection.png")

    page.locator("#id_buttonMap").click()

    expect(page).to_have_url(re.compile(r".*/mobile_map\.php\?.*"))
    page.wait_for_timeout(5000)
    page.screenshot(path="screenshots/cds_PY/cds_busMapTest_openMap.png")

    page.wait_for_function(
        """() => typeof gBusMarker_List !== 'undefined'
        && Object.values(gBusMarker_List).some(
            b => b.marker && map.hasLayer(b.marker)
        )""",
        timeout=40000
    )

    # Открываем карточку первого автобуса
    page.evaluate(
        """() => {
            Object.values(gBusMarker_List)
                .find(b => b.marker && map.hasLayer(b.marker))
                .marker.fire('click');
        }"""
    )

    info = page.locator(".leaflet-popup-content")

    expect(info).to_be_visible()
    page.wait_for_timeout(5000)

    page.screenshot(path="screenshots/cds_PY/cds_busMapTest_busInfo.png")

    assert info.inner_text().strip() != ""

def test_prediction(page: Page):
    page.route("**/yandex.ru/ads/system/context.js",
               lambda route: route.abort())

    page.goto(URL)

    page.wait_for_function(
        "() => document.querySelector('#id_CitySelect').options.length > 0"
    )

    page.locator("#id_CitySelect").select_option("1")

    page.locator("#id_MarshSelect option[value='1001']").wait_for(
        state="attached"
    )

    page.locator("#id_MarshSelect").select_option("1001")

    page.locator("#id_StopSelect option[value='181']").first.wait_for(
        state="attached"
    )

    page.locator("#id_StopSelect").select_option("181")

    page.screenshot(path="screenshots/cds_PY/cds_predictionTest_selection.png")

    page.locator("#id_buttonPrediction").click()

    expect(page).to_have_url(re.compile(r".*/prediction\.php\?.*"))

    row = page.locator("tr:has(td.minutes)").first

    expect(row).to_be_visible()

    page.screenshot(path="screenshots/cds_PY/cds_predictionTest_result.png")

    cells = row.locator("td")

    assert cells.count() >= 4
    assert "мин." in cells.nth(1).inner_text()
    assert cells.nth(3).inner_text().strip() != ""


