package ua.prom.pages;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
public class SearchResults extends PageObject {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired(required = true)
    public SearchResults(WebDriverProvider webDriverProvider) {
        super(webDriverProvider);
        logger.debug("web driver initialized successfully: {}", webDriverProvider);
    }

    public int resultsFound() {
        List<WebElement> elements = findElements(xpath("//div[contains(@class, 'b-product-line')]"));
        int size = elements.size();
        return size;
    }

    public String getItemIdFromURL(String url) {
        String[] urlParts = url.split("/");

        final int minUrlPartsLength = 3;
        if (urlParts.length < minUrlPartsLength) {
            throw new IllegalStateException("URL length not valid. Expected: " + minUrlPartsLength + ". Actual: " + Arrays.asList(urlParts));
        }

        // String to be scanned to find the pattern.
        String line = urlParts[3];
        String pattern = "(^[a-zA-Z0-9]*).*";

        // Create a Pattern object
        Pattern r = Pattern.compile(pattern);

        String itemId;
        // Now create matcher object.
        Matcher m = r.matcher(line);
        if (m.find()) {
            itemId = m.group(1);
        } else {
            throw new IllegalStateException("Item not found in URL part: " + line);
        }

        return itemId;
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
