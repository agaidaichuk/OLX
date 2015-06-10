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
public class Home extends PageObject {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired(required = true)
    private AdvancedSearch advancedSearch;

    @Autowired(required = true)
    public Home(WebDriverProvider webDriverProvider) {
        super(webDriverProvider);
        logger.debug("web driver initialized successfully: {}", webDriverProvider);
    }

    public void go() {
        get("http://olx.ua/");
        manage().window().maximize();
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

    public void goToBuySection() {
        advancedSearch.category("Промышленные товары");
    }

    public void browseAnyCategory() {
        String url;
        WebElement category = waitForElement(xpath("//a[contains(.,'Электроинструменты')]"));
        if (category == null) {
            throw new IllegalStateException("Could not find element. Probably content was updated.");
        }
        // in some places pages are open in a new window instead in the same
        url = category.getAttribute("href");
        get(url);

        WebElement subCategory = waitForElement(xpath("//a[contains(.,'Шлифовальные машины')]"));
        if (subCategory == null) {
            throw new IllegalStateException("Could not find element. Probably content was updated.");
        }
        // in some places pages are open in a new window instead in the same
        url = subCategory.getAttribute("href");
        get(url);
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
