package ua.prom.steps;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.jbehave.core.annotations.AfterStories;
import org.jbehave.web.selenium.FirefoxWebDriverProvider;
import org.jbehave.web.selenium.WebDriverProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JournaledStoriesSteps {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final String JOURNAL_FIREFOX_COMMANDS = System.getProperty("JOURNAL_FIREFOX_COMMANDS", "false");

    @Autowired(required = true)
    private WebDriverProvider webDriverProvider;

    @AfterStories
    public void afterStories() throws Exception {

        if (!JOURNAL_FIREFOX_COMMANDS.equals("false") && webDriverProvider instanceof FirefoxWebDriverProvider) {
            FirefoxWebDriverProvider.WebDriverJournal journal = ((FirefoxWebDriverProvider) webDriverProvider).getJournal();
            logger.info("Journal of WebDriver Commands:");
            for (Object entry : journal) {
                logger.info(entry.toString());
            }
            ((FirefoxWebDriverProvider) webDriverProvider).clearJournal();
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
