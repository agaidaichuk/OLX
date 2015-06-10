package ua.olx.pages;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.jbehave.web.selenium.WebDriverProvider;
import static org.openqa.selenium.By.xpath;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AdvancedSearch extends PageObject {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired(required = true)
    public AdvancedSearch(WebDriverProvider webDriverProvider) {
        super(webDriverProvider);
        logger.debug("web driver initialized successfully: {}", webDriverProvider);
    }

    public void go() {
        get("http://olx.ua/");
    }

    public void go(final String section) {
        go();
        // http://www.w3schools.com/xpath/xpath_functions.asp
        WebElement webElement = waitForElement(xpath("//a[contains(.,'" + section + "')]"));
        // in some places pages are open in a new window instead in the same
        String url = webElement.getAttribute("href");
        get(url);
    }

    public void search(String thing) {
        WebElement input = waitForElement(xpath("//*[@id='search_text']"));
        input.sendKeys(thing);
        WebElement button = waitForElement(xpath("//*[@id='search_submit']"));
        button.click();
    }

    public void category(final String category) {
        WebElement webElement = waitForElement(xpath("//a[contains(.,'" + category + "')]"));
        // in some places pages are open in a new window instead in the same
        String url = webElement.getAttribute("href");
        get(url);
    }

    public void searchFor(String thing) {
        WebElement webElement = waitForElement(xpath("//*[@id='search_text']"));
        webElement.sendKeys(thing);
        webElement.submit();
    }

    @PostConstruct
    public void initIt() throws Exception {
        logger.debug("instance initialized successfully");
    }

    @PreDestroy
    public void cleanUp() throws Exception {
        logger.debug("cleaning up instance");
    }
}
