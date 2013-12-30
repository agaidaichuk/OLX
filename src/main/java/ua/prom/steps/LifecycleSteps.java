package ua.prom.steps;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.jbehave.core.annotations.BeforeScenario;
import org.jbehave.web.selenium.WebDriverProvider;
import org.openqa.selenium.WebDriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LifecycleSteps {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired(required = true)
    private WebDriverProvider webDriverProvider;

    @BeforeScenario
    public void emptyCart() {
        try {
            webDriverProvider.get().manage().deleteAllCookies();
        } catch (WebDriverException e) {
            logger.error("empty cart error", e);
        }
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
