package ua.prom.pages;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.jbehave.web.selenium.WebDriverProvider;
import static org.openqa.selenium.By.xpath;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Site extends PageObject {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired(required = true)
    public Site(WebDriverProvider webDriverProvider) {
        super(webDriverProvider);
        logger.debug("web driver initialized successfully: {}", webDriverProvider);
    }

    public int cartSize() {
        WebElement cartElement = waitForElement(xpath("//a[contains(@class, 'b-shopping-case__link')]"));

        if (cartElement == null) {
            throw new IllegalStateException("An element not found. Probably problem in locator.");
        }

        int cartSize = 0;
        WebElement cartSizeElement = null;

        try {
            cartSizeElement = findElement(xpath("//span[@class='b-shopping-case__quantity']"));
        } catch (NoSuchElementException e) {
            // do nothing
        }

        if (cartSizeElement != null) {
            String cartSizeText = cartSizeElement.getText();
            if (cartSizeText == null) {
                throw new IllegalStateException("An element not found. Probably problem in locator.");
            }

            cartSizeText = cartSizeText.trim();
            cartSize = Integer.parseInt(cartSizeText);
        }

        return cartSize;
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
