package ua.prom.pages;

import java.util.List;
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
public class CartContents extends PageObject {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired(required = true)
    private Home home;
    @Autowired(required = true)
    private Site site;
    @Autowired(required = true)
    private Buy buy;

    @Autowired(required = true)
    public CartContents(WebDriverProvider webDriverProvider) {
        super(webDriverProvider);
        logger.debug("web driver initialized successfully: {}", webDriverProvider);
    }

    public boolean hasItem(String item) {
        home.go();

        WebElement cartElement = waitForElement(xpath("//div[contains(@class, 'b-shopping-case__top-part')]"));

        if (cartElement == null) {
            throw new IllegalStateException("An element not found. Probably problem in locator.");
        }

        cartElement.click();

        String boughtItemIdAtCurrentPage = buy.getBoughtItemIdAtCurrentPage();
        boolean hasTheSameItem = boughtItemIdAtCurrentPage.equals(item);

        return hasTheSameItem;
    }

    public void removeItem() {
        home.go();

        // open basket
        WebElement cartElement = waitForElement(xpath("//div[contains(@class, 'b-shopping-case__top-part')]"));

        if (cartElement == null) {
            throw new IllegalStateException("An element not found. Probably problem in locator.");
        }

        cartElement.click();

        // find remove button in the basket
        List<WebElement> linksToRemove = waitForElements(xpath("//i[contains(@class, 'b-shopping-cart__delete-item')]"));

        if (linksToRemove == null || linksToRemove.isEmpty()) {
            throw new IllegalStateException("An element not found. Probably problem in locator.");
        }

        WebElement linkToRemove = linksToRemove.get(0);

        // click button to remove element
        linkToRemove.click();
    }

    public void removeAllItems() {
        manage().deleteAllCookies();
    }

    public int cartSize() {
        home.go();
        return site.cartSize();
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
