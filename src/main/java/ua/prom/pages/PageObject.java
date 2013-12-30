package ua.prom.pages;

import com.google.common.base.Function;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import org.jbehave.web.selenium.WebDriverPage;
import org.jbehave.web.selenium.WebDriverProvider;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.springframework.util.Assert;

abstract class PageObject extends WebDriverPage {

    public static final int DEFAULT_WAIT_TIMEOUT = 40;
    public static final TimeUnit DEFAULT_WAIT_TIMEOUT_UNIT = TimeUnit.SECONDS;

    public static final int DEFAULT_RETRY_TIMEOUT = 1;
    public static final TimeUnit DEFAULT_RETRY_TIMEOUT_UNIT = TimeUnit.SECONDS;

    private static final ExecutorService THREAD_POOL = Executors.newCachedThreadPool();

    private static <T> T timedCall(FutureTask<T> task, long timeout, TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException, java.util.concurrent.TimeoutException {
        THREAD_POOL.execute(task);
        return task.get(timeout, timeUnit);
    }

    PageObject(WebDriverProvider driverProvider) {
        super(driverProvider);
    }

    WebElement waitForElement(final By locator) {
        // XXX: in some cases firefox not wait for the full page to load after calling .get or .click. This may cause immediate find's to break and using an implicit or explicit wait doesn't help
        // WebElement element = fluentWaitForElement(locator);
        // XXX: workaround that works fine
        WebElement element = repeatableFindForElement(locator);

        return element;
    }

    List<WebElement> waitForElements(final By locator) {
        // XXX: in some cases firefox not wait for the full page to load after calling .get or .click. This may cause immediate find's to break and using an implicit or explicit wait doesn't help
        // List<WebElement> elements = fluentWaitForElements(locator);
        // XXX: workaround that works fine
        List<WebElement> elements = repeatableFindForElements(locator);

        return elements;
    }

    // XXX: in some cases firefox not wait for the full page to load after calling .get or .click. This may cause immediate find's to break and using an implicit or explicit wait doesn't help
    private WebElement fluentWaitForElement(final By locator) {
        Wait<WebDriver> wait = new FluentWait<WebDriver>(getDriverProvider().get())
            .withTimeout(PageObject.DEFAULT_WAIT_TIMEOUT, PageObject.DEFAULT_WAIT_TIMEOUT_UNIT)
            .pollingEvery(PageObject.DEFAULT_RETRY_TIMEOUT, PageObject.DEFAULT_RETRY_TIMEOUT_UNIT)
            .ignoring(NoSuchElementException.class);

        WebElement element = wait.until(new Function<WebDriver, WebElement>() {
            @Override
            public WebElement apply(WebDriver driver) {
                return driver.findElement(locator);
            }
        });

        return element;
    }

    // XXX: workaround that works fine
    private WebElement repeatableFindForElement(final By locator) {

        RepeatableFind repeatableFind = new RepeatableFind.RepeatableFindBuilder()
            .withDriver(getDriverProvider().get())
            .withTimeout(PageObject.DEFAULT_WAIT_TIMEOUT, PageObject.DEFAULT_WAIT_TIMEOUT_UNIT)
            .pollingEvery(PageObject.DEFAULT_RETRY_TIMEOUT, PageObject.DEFAULT_RETRY_TIMEOUT_UNIT)
            .by(locator).build();

        WebElement element = repeatableFind.find();

        return element;
    }

    // XXX: in some cases firefox not wait for the full page to load after calling .get or .click. This may cause immediate find's to break and using an implicit or explicit wait doesn't help
    private List<WebElement> fluentWaitForElements(final By locator) {
        Wait<WebDriver> wait = new FluentWait<WebDriver>(getDriverProvider().get())
            .withTimeout(PageObject.DEFAULT_WAIT_TIMEOUT, PageObject.DEFAULT_WAIT_TIMEOUT_UNIT)
            .pollingEvery(PageObject.DEFAULT_RETRY_TIMEOUT, PageObject.DEFAULT_RETRY_TIMEOUT_UNIT)
            .ignoring(NoSuchElementException.class);

        List<WebElement> elements = wait.until(new Function<WebDriver, List<WebElement>>() {
            @Override
            public List<WebElement> apply(WebDriver driver) {
                return driver.findElements(locator);
            }
        });

        return elements;
    }

    // XXX: workaround that works fine
    private List<WebElement> repeatableFindForElements(final By locator) {
        RepeatableFind repeatableFind = new RepeatableFind.RepeatableFindBuilder()
            .withDriver(getDriverProvider().get())
            .withTimeout(PageObject.DEFAULT_WAIT_TIMEOUT, PageObject.DEFAULT_WAIT_TIMEOUT_UNIT)
            .pollingEvery(PageObject.DEFAULT_RETRY_TIMEOUT, PageObject.DEFAULT_RETRY_TIMEOUT_UNIT)
            .by(locator).build();

        List<WebElement> elements = repeatableFind.findAll();

        return elements;
    }

    static class RepeatableFind {

        private final WebDriver driver;
        private final int duration;
        private final TimeUnit durationUnit;
        private final int pollingInterval;
        private final TimeUnit pollingIntervalUnit;
        private final By locator;

        private RepeatableFind(WebDriver driver, int duration, TimeUnit durationUnit, int pollingInterval, TimeUnit pollingIntervalUnit, By locator) {
            this.driver = driver;
            this.duration = duration;
            this.durationUnit = durationUnit;
            this.pollingInterval = pollingInterval;
            this.pollingIntervalUnit = pollingIntervalUnit;
            this.locator = locator;
        }

        public WebElement find() {
            WebElement foundElement = null;
            final long pollingIntervalInMs = TimeUnit.MILLISECONDS.convert(pollingInterval, pollingIntervalUnit);

            FutureTask<WebElement> task = new FutureTask<WebElement>(new Callable<WebElement>() {
                @Override
                public WebElement call() throws Exception {
                    WebElement element = null;
                    while (element == null) {
                        if (Thread.interrupted()) {
                            return element;
                        }

                        try {
                            element = driver.findElement(locator);
                        } catch (RuntimeException e) {
                            // do nothing
                        }
                        try {
                            Thread.sleep(pollingIntervalInMs);
                        } catch (InterruptedException e) {
                            // do nothing
                        }
                    }

                    return element;
                }
            });

            try {
                foundElement = timedCall(task, duration, durationUnit);
            } catch (InterruptedException ex) {
                task.cancel(true);
            } catch (ExecutionException ex) {
                task.cancel(true);
            } catch (TimeoutException ex) {
                task.cancel(true);
            } catch (java.util.concurrent.TimeoutException ex) {
                task.cancel(true);
            }

            return foundElement;
        }

        public List<WebElement> findAll() {
            List<WebElement> foundElements = null;
            final long pollingIntervalInMs = TimeUnit.MILLISECONDS.convert(pollingInterval, pollingIntervalUnit);

            FutureTask<List<WebElement>> task = new FutureTask<List<WebElement>>(new Callable<List<WebElement>>() {
                @Override
                public List<WebElement> call() throws Exception {
                    List<WebElement> elements = null;
                    while (elements == null || elements.isEmpty()) {
                        if (Thread.interrupted()) {
                            return elements;
                        }

                        try {
                            elements = driver.findElements(locator);
                        } catch (RuntimeException e) {
                            // do nothing
                        }
                        try {
                            Thread.sleep(pollingIntervalInMs);
                        } catch (InterruptedException e) {
                            // do nothing
                        }
                    }

                    return elements;
                }
            });

            try {
                foundElements = timedCall(task, duration, durationUnit);
            } catch (InterruptedException ex) {
                task.cancel(true);
            } catch (ExecutionException ex) {
                task.cancel(true);
            } catch (TimeoutException ex) {
                task.cancel(true);
            } catch (java.util.concurrent.TimeoutException ex) {
                task.cancel(true);
            }

            return foundElements;
        }

        static class RepeatableFindBuilder {

            private WebDriver driver;
            private int duration;
            private TimeUnit durationUnit;
            private int pollingInterval;
            private TimeUnit pollingIntervalUnit;
            private By locator;

            public RepeatableFindBuilder withDriver(WebDriver driver) {
                this.driver = driver;
                return this;
            }

            public RepeatableFindBuilder withTimeout(int duration, TimeUnit durationUnit) {
                this.duration = duration;
                this.durationUnit = durationUnit;
                return this;
            }

            public RepeatableFindBuilder pollingEvery(int pollingInterval, TimeUnit pollingIntervalUnit) {
                this.pollingInterval = pollingInterval;
                this.pollingIntervalUnit = pollingIntervalUnit;
                return this;
            }

            public RepeatableFindBuilder by(By locator) {
                this.locator = locator;
                return this;
            }

            public RepeatableFind build() {
                Assert.notNull(driver);
                Assert.notNull(duration);
                Assert.notNull(durationUnit);
                Assert.notNull(pollingInterval);
                Assert.notNull(pollingIntervalUnit);
                Assert.notNull(locator);

                return new RepeatableFind(
                    driver, duration,
                    durationUnit, pollingInterval,
                    pollingIntervalUnit,
                    locator
                );
            }
        }
    }
}
