
from pathlib import Path

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

    expect(page).to_have_url("**/mobile_map.php?*")

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

    page.screenshot(path="screenshots/cds_PY/cds_busMapTest_busInfo.png")

    assert info.inner_text().strip() != ""


