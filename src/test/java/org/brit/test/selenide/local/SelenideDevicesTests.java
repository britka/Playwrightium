package org.brit.test.selenide.local;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.conditions.webdriver.UrlCondition;
import com.codeborne.selenide.testng.TextReport;
import com.microsoft.playwright.options.AriaRole;
import org.apache.commons.io.FileUtils;
import org.brit.driver.PWDriverDeviceProvider;
import org.brit.driver.PWDriverProvider;
import org.brit.emulation.Device;
import org.brit.locators.ArialSearchOptions;
import org.brit.locators.PlaywrightiumBy;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.testng.reporters.TextReporter;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import static com.codeborne.selenide.Condition.or;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverConditions.urlContaining;
import static com.codeborne.selenide.WebDriverRunner.url;
import static org.assertj.core.api.Assertions.assertThat;
import static org.openqa.selenium.support.ui.ExpectedConditions.and;
import static org.openqa.selenium.support.ui.ExpectedConditions.urlContains;

/**
 * Created by Serhii Bryt
 * 01.04.2024 16:18
 **/
@Listeners({TextReport.class})
public class SelenideDevicesTests {

    @AfterMethod
    public void afterMethod(){
        closeWebDriver();
    }

    @Test
    public void deviceTest() {
        Configuration.browser = PWDriverDeviceProvider.class.getName();
        open("https://google.com");
        Dimension dimension = webdriver().object().manage().window().getSize();
        Device device = PWDriverDeviceProvider.device;
        assertThat(dimension.getHeight()).isEqualTo(device.getViewport().height);
        assertThat(dimension.getWidth()).isEqualTo(device.getViewport().width);
        String timeZone = executeJavaScript("return Intl.DateTimeFormat().resolvedOptions().timeZone");
        assertThat(timeZone).isEqualTo("Europe/Athens");
        String locale = executeJavaScript("return Intl.DateTimeFormat().resolvedOptions().locale");
        assertThat(locale).isEqualTo("el-GR");
    }

    @Test
    public void geolocationTest() throws IOException {
        Configuration.browser = PWDriverProvider.class.getName();
        open("https://www.bing.com/maps");
        $("button.locateMeBtn,a#LocateMeButton").click();
        webdriver().shouldHave(urlContaining("46.655")).shouldHave(urlContaining("32.617"));
    }
}
