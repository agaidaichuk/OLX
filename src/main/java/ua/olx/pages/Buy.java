package ua.olx.pages;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.apache.commons.validator.routines.UrlValidator;
import org.jbehave.web.selenium.WebDriverProvider;
import static org.openqa.selenium.By.xpath;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Buy extends PageObject {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired(required = true)
    private SearchResults searchResults;

    @Autowired(required = true)
    public Buy(WebDriverProvider webDriverProvider) {
        super(webDriverProvider);
        logger.debug("web driver initialized successfully: {}", webDriverProvider);
    }

    public String buyFirst(String thing) {
        List<WebElement> elements = waitForElements(xpath("//span[(contains(@class, 'b-button__text') and .[contains(text(), 'Заказать')])]"));
        if (elements == null || elements.isEmpty()) {
            throw new IllegalStateException("An element not found. Probably problem in locator.");
        }

        WebElement element = elements.get(0);

        // buy first
        element.click();

        String itemId = getBoughtItemIdAtCurrentPage();

        return itemId;
    }

    public String getBoughtItemIdAtCurrentPage() {
        WebElement boughtItem = waitForElement(xpath("//div[contains(@class, 'b-order-prepare__amount-info')]//a[contains(@class, 'b-order-prepare__product-name')]"));

        String url = boughtItem.getAttribute("href");

        if (url == null || url.isEmpty()) {
            throw new IllegalStateException("URL not found.");
        }

        UrlValidator urlValidator = new UrlValidator();
        if (!urlValidator.isValid(url)) {
            throw new IllegalStateException("URL is not valid: " + url);
        }

        String itemId = searchResults.getItemIdFromURL(url);

        return itemId;
    }

    public void buyAnyFirstThing() {
        List<WebElement> elements = waitForElements(xpath("//span[(contains(@class, 'b-button__text') and .[contains(text(), 'Заказать')])]"));
        if (elements == null || elements.isEmpty()) {
            throw new IllegalStateException("An element not found. Probably problem in locator.");
        }

        WebElement element = elements.get(0);

        // buy first
        element.click();
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
