# Playwrightium

## Intro

Playwrightium is the implementation of Webdriver interface with Playwright Java inside.   
This combination allows us to run Selenide tests with Playwright without rewriting the code.

## The story

I like Java. I like [Selenium](https://selenium.dev/). I like [Selenide](https://selenide.org/).
But also I like [Playwright Java](https://playwright.dev/java/). But I want to use all (or almost all) features of
Playwright and continue using Selenide syntax.

What should I do?
I developed Playwrightium.

## How to install

### Maven

Add to your **pom.xml**

Add dependency

```xml

<dependency>
    <groupId>io.github.britka</groupId>
    <artifactId>playwrightium</artifactId>
    <version>LAST_VERSION</version>
</dependency>
```

### Gradle

```groovy
implementation 'io.github.britka:playwrightium:LAST_VERSION'
```

This dependency encapsulates

* Selenide
* Playwright Java

## How to use as regular webdriver

You can use it like a regular `Webdriver`
For example:

```java
WebDriver driver = new PlaywrightiumDriver();
driver.get("https://example.com");
driver.findElement(By.name("username")).sendKeys("Some value");
driver.findElement(By.cssSelector("input[value=submit][name=submitbutton]")).click();
```

If you need more example please refer to
the [playwrightium tests](src/test/java/org/brit/test/playwrightium/PlaywrightiumBasicTests.java)

> [!NOTE]
> For the first time [Playwright](https://playwright.dev/java/) will install browsers images:

* chromium
* firefox
* webkit (Safari)

After installation your test will run.

> [!NOTE]
> If you want to skip browsers download you should set up this using `PlaywrightiumOptions` and tell `Playwrightium` to use local browser

Example: 
```java
PlaywrightiumOptions playwrightiumOptions = new PlaywrightiumOptions();
playwrightiumOptions.setSkipDownloadBrowsers(true);
playwrightiumOptions.setBrowserName(Browsers.CHROME_CHANNEL);
PlaywrightiumDriver playwrightiumDriver = new PlaywrightiumDriver(playwrightiumOptions);
```

## What Playwrightium can do

### Navigate to url

```java
driver.get("https://examle.com");
```

### Search for element and element collections by different locators:

    * By.xpath
    * By.cssSelector
    * By.id
    * By.name
    * By.linkText
    * By.partialLinkText
    * By.className
    * By.tagName

For example:

```java
WebElement xpathElement = driver.findElement(By.xpath("//label[text()='Some text']"));
WebElement cssElement = driver.findElement(By.xpath("//label[text()='Some text']"));

List<WebElement> elementList = driver.findElements(By.name("any name"));
```

Also you can use Playwright locators
See [documentation](https://playwright.dev/java/docs/locators)

For this use `PlaywrightiumBy` class:

* PlaywrightiumBy.byRole
* PlaywrightiumBy.byAltTextbyLabel
* PlaywrightiumBy.byPlaceholder
* PlaywrightiumBy.byTestId
* PlaywrightiumBy.byText
* PlaywrightiumBy.byTitle

For example:

```java
import org.openqa.selenium.WebElement;

WebElement submitButton = driver.findElement(PlaywrightiumBy.byRole(AriaRole.BUTTON, AriaRoleOptions.builder().setName("submit").build()));
WebElement driver.findElement(PlaywrightiumBy.byLabel("LabeText", true));
```

### Work with WebElements

For example

```java
var name = element.getAttribute("name");
element.click();
element.isDisplayed();
element.getText();
```
> [!IMPORTANT]
> Method `sendKeys` works quickly amd smooth. But Selenide method `setValue` can work slowly. 
> This method makes many things.
> So if it is possible use `sendKey` from Playwrightium 


### Switch to frame

```java
driver.switchTo().frame("frameName");
```

### Works with Select webelement

```java
ISelect select = new PlaywrightiumSelect(driver.findElement(By.name("dropdown"))); 
select.selectByValue("dd"+faker.number().numberBetween(1,7));
String selectValue = select.getFirstSelectedOption().getAttribute("value");
```

* Use waiters

```java
 new WebDriverWait(driver, Duration.ofSeconds(10)).
    until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[contains(.,'Processed Form Details')]")));
```

### Use alerts

> [!NOTE]
> To use alerts you should know [how it works in Playwright](https://playwright.dev/java/docs/dialogs)

So the example will be

```java
// At first describe what we want to do with alert
Alert alert = driver.switchTo().alert();
alert.sendKeys(testString);
alert.accept();

// After we do some actions that will lead to alert appearance.
driver.findElement(By.id("promptexample")).click();

// After we can make some checks
assertThat(alert.getText()).isEqualTo("I prompt you");
```

### Use Actions class

```java
new Actions(driver).moveToElement(driver.findElement(By.id("someId"))).build().perform();
```

> [!IMPORTANT]
> Some functionality is on WIP. So I cannot guarantee that all will work fine

### Run JavaScript scripts

```java
((JavascriptExecutor)driver).executeScript("return alert();");
```

### Record video

Initialize Playwrightium driver using `PlaywrightiumOptions` class

```java
PlaywrightiumOptions playwrightiumOptions = new PlaywrightiumOptions();
playwrightiumOptions.setRecordVideo(true);

driver =new PlaywrightiumDriver(playwrightiumOptions);
```

> [!IMPORTANT]
> This will create folder 'builds/video' in the root of your project

or to initialize records folder

```java
PlaywrightiumOptions playwrightiumOptions = new PlaywrightiumOptions();
playwrightiumOptions.setRecordVideo(true);
playwrightiumOptions.setRecordsFolder(Path.of("videosFolder"));
driver =new PlaywrightiumDriver(playwrightiumOptions);
```

> [!IMPORTANT]
> This will create folder 'videosFolder' in the root of your project

> [!IMPORTANT]
> If you initialize driver in BeforeAll(or similar) section it will record all tests as 1 video file
> To record video of separate tests you should initialize browser before each test.

> [!IMPORTANT]
> This method will work everywhere: locally, in Selenoid, Selenium Grid, Aerokube Moon


How this feature works.

For `chromium` based browsers Playwright is trying to use CDP protocol to screencast browser context and collect
screenshots
and after that it uses `ffmpeg` to create video from images.

* See [CDP protocol. Page.](https://chromedevtools.github.io/devtools-protocol/tot/Page/)
    * [Page.startScreencast](https://chromedevtools.github.io/devtools-protocol/tot/Page/#method-startScreencast)
    * [Page.stopScreencast](https://chromedevtools.github.io/devtools-protocol/tot/Page/#method-stopScreencast)
    * [Page.screencastFrameAck](https://chromedevtools.github.io/devtools-protocol/tot/Page/#method-screencastFrameAck)
    * [Page.screencastFrame](https://chromedevtools.github.io/devtools-protocol/tot/Page/#event-screencastFrame)
* See [ffmpeg](https://ffmpeg.org/)
    * [ffmpeg FAQ about `image2pipe` approach](https://ffmpeg.org/faq.html#How-do-I-encode-single-pictures-into-movies_003f)

### Run remotely.

#### Using Selenoid or Selenium Grid

> [!IMPORTANT]
> This will work with Chromium-based browsers (Chromium, Chrome, MS Edge, Opera etc.)

Initialize Playwrightium driver using `PlaywrightiumOptions` class

```java
PlaywrightiumOptions playwrightiumOptions = new PlaywrightiumOptions();
playwrightiumOptions.setConnectionByWS(false);
playwrightiumOptions.setHeadless(true);
return new PlaywrightiumDriver("http://localhost:4444/wd/hub",chromeOptions);
```

> [!IMPORTANT]
> `chromeOptions.setConnectionByWS(false);` is telling us that we will use http connections
> as we do in regular connection to Selenoid or Selenium Grid.

#### Using Aerokube Moon

> [!IMPORTANT]
> Works for all browsers.

Initialize Playwrightium driver using `PlaywrightiumOptions` class

```java
PlaywrightiumOptions playwrightiumOptions = new PlaywrightiumOptions();
playwrightiumOptions.setConnectionByWS(true);
playwrightiumOptions.setHeadless(true);
return new PlaywrightiumDriver("http://localhost:4444/wd/hub",playwrightiumOptions);
```

> [!IMPORTANT]
> `chromeOptions.setConnectionByWS(true);` is telling us that we will use ws connections
> as we do in regular connection to Aerokube Moon.

## How to use it with Selenide

To use it with Selenide you should implement `WebDriverProvider` interface.
For example:

```java
public class PWDriverProvider implements WebDriverProvider {
    @Nonnull
    @Override
    public WebDriver createDriver(@Nonnull Capabilities capabilities) {
        PlaywrightWebdriverOptions playwrightiumOptions = new PlaywrightWebdriverOptions();
        playwrightiumOptions.merge(capabilities);
        return new PlaywrightiumDriver(playwrightiumOptions);
    }
}
```

Then you should use it with `Configuration`
E.g.

```java
Configuration.browser = PWDriverProvider.class.getName();
```

## Playwrightium options

| Option name          | Type                                                                                                                                   | Description                                                                                                                                                                         |
|----------------------|----------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| headless             | boolean                                                                                                                                | Run tests in "headless" node or not                                                                                                                                                 |
| browserName          | string  or [Browsers](src/main/java/org/brit/options/Browsers.java)                                                                    | What browser to run. Available values: chromium, firefox, webkit                                                                                                                    |
| recordVideo          | boolean                                                                                                                                | Indicates will the video be recorded or not                                                                                                                                         |
| recordsFolder        | string                                                                                                                                 | The folder where video recordings will be saved. Default {project.basedir}/build/video                                                                                              |
| connectionByWS       | boolean                                                                                                                                | Indicates how we will run tests remotely. If we will use Selenoid/Selenium grid then we should choose false (works for chrome only), if we will use Moon then we should choose true |
| emulation            | [Device](src/main/java/org/brit/emulation/Device.java)                                                                                 | Emulates the device                                                                                                                                                                 |
| locale               | [Locale](https://docs.oracle.com/javase/8/docs/api/java/util/Locale.html)                                                              | Emulates locale                                                                                                                                                                     |
| timeZone             | [TimeZone](https://docs.oracle.com/javase/8/docs/api/java/util/TimeZone.html)                                                          | Emulates timezone                                                                                                                                                                   |
| geolocation          | [Geolocation](https://www.javadoc.io/doc/com.microsoft.playwright/playwright/latest/com/microsoft/playwright/options/Geolocation.html) | Emulates geolocation                                                                                                                                                                |
| permissions          | List<[Permissions](src/main/java/org/brit/permission/Permissions.java)>                                                                | Switch on permissions                                                                                                                                                               |
| enableTracing        | boolean                                                                                                                                | Enables tracing for test run                                                                                                                                                        |
| tracingOptions       | [TracingOptions](src/main/java/org/brit/options/TracingOptions.java)                                                                   | Sets tracing options when tracing options are enabled                                                                                                                               |
| skipDownloadBrowsers | boolean                                                                                                                                | Skips downloads of predefined Playwright browsers                                                                                                                                   |

> [!IMPORTANT]
> For now all this options might be set only
> by [PlaywrightiumOptions](src/main/java/org/brit/options/PlaywrightiumOptions.java)

## Tracing 
When tests were run using `enableTracing` option by default `tracing.zip` file should be created in `{projectRootDirectory}/tracing/` directory.  
To run tracing viewer you can use:
* `viewTracing.bat` or `viewTracing.sh` scripts depends on you OS. 
```commandline
viewTracing.sh path/to/your/tracing.zip
```
* run in commandline 
```commandline
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="show-trace path\to\your\trace.zip"
```
* or use [Online trace viewer](https://trace.playwright.dev/)

> [!IMPORTANT]  
> Be careful!!! This may decrease the speed of test running

And that's all. You can easily use it with Selenide.
See [test for Selenide](src/test/java/org/brit/test/selenide/)

Enjoy!
