package ua.prom;

import java.text.SimpleDateFormat;
import java.util.List;

import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.failures.FailingUponPendingStep;
import org.jbehave.core.failures.PendingStepStrategy;
import org.jbehave.core.io.LoadFromClasspath;
import org.jbehave.core.io.StoryFinder;
import org.jbehave.core.junit.JUnitStories;
import org.jbehave.core.reporters.CrossReference;
import org.jbehave.core.reporters.Format;
import org.jbehave.core.reporters.StoryReporterBuilder;
import org.jbehave.core.steps.InjectableStepsFactory;
import org.jbehave.core.steps.spring.SpringApplicationContextFactory;
import org.jbehave.core.steps.spring.SpringStepsFactory;
import org.jbehave.web.selenium.ContextView;
import org.jbehave.web.selenium.LocalFrameContextView;
import org.jbehave.web.selenium.SeleniumConfiguration;
import org.jbehave.web.selenium.SeleniumContext;
import org.jbehave.web.selenium.SeleniumContextOutput;
import org.jbehave.web.selenium.SeleniumStepMonitor;

import static java.util.Arrays.asList;
import org.jbehave.core.Embeddable;
import org.jbehave.core.configuration.spring.SpringStoryControls;
import org.jbehave.core.configuration.spring.SpringStoryReporterBuilder;
import org.jbehave.core.i18n.LocalizedKeywords;
import static org.jbehave.core.io.CodeLocations.codeLocationFromClass;
import org.jbehave.core.model.ExamplesTableFactory;
import org.jbehave.core.parsers.RegexStoryParser;
import static org.jbehave.core.reporters.Format.CONSOLE;
import org.jbehave.core.steps.ParameterConverters;
import org.jbehave.core.steps.ParameterConverters.DateConverter;
import org.jbehave.core.steps.ParameterConverters.ExamplesTableConverter;
import static org.jbehave.web.selenium.WebDriverHtmlOutput.WEB_DRIVER_HTML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * <p>
 * {@link Embeddable} class to run multiple textual stories via JUnit.
 * </p>
 * <p>
 * Stories are specified in classpath and correspondingly the
 * {@link LoadFromClasspath} story loader is configured.
 * </p>
 */
public class PromUaStories extends JUnitStories {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final PendingStepStrategy pendingStepStrategy;
    private final CrossReference crossReference;
    private final ContextView contextView;
    private final SeleniumContext seleniumContext;
    private final SeleniumStepMonitor stepMonitor;
    private final Format[] formats;
    private final StoryReporterBuilder reporterBuilder;

    public PromUaStories() {
        pendingStepStrategy = new FailingUponPendingStep();
        crossReference = new CrossReference().withJsonOnly().withPendingStepStrategy(pendingStepStrategy)
            .withOutputAfterEachStory(true).excludingStoriesWithNoExecutedScenarios(true);
        contextView = new LocalFrameContextView().sized(640, 120);
        seleniumContext = new SeleniumContext();
        stepMonitor = new SeleniumStepMonitor(contextView, seleniumContext,
            crossReference.getStepMonitor());
        formats = new Format[]{ new SeleniumContextOutput(seleniumContext), CONSOLE, WEB_DRIVER_HTML };
        reporterBuilder = new SpringStoryReporterBuilder()
            .withCodeLocation(codeLocationFromClass(getClass())).withFailureTrace(true)
            .withFailureTraceCompression(true).withDefaultFormats().withFormats(formats)
            .withCrossReference(crossReference);
    }

    @Override
    public Configuration configuration() {
        Class<? extends Embeddable> embeddableClass = this.getClass();

        ParameterConverters parameterConverters = new ParameterConverters();
        // factory to allow parameter conversion and loading from external resources (used by StoryParser too)
        ExamplesTableFactory examplesTableFactory = new ExamplesTableFactory(
            new LocalizedKeywords(),
            new LoadFromClasspath(embeddableClass),
            parameterConverters
        );
        // add custom converters
        parameterConverters.addConverters(
            new DateConverter(new SimpleDateFormat("yyyy-MM-dd")),
            new ExamplesTableConverter(examplesTableFactory)
        );

        return new SeleniumConfiguration()
            .useSeleniumContext(seleniumContext)
            .useStoryLoader(new LoadFromClasspath(embeddableClass))
            .useStoryParser(new RegexStoryParser(examplesTableFactory))
            .usePendingStepStrategy(pendingStepStrategy)
            .useStoryReporterBuilder(reporterBuilder)
            .useStoryControls(new SpringStoryControls().doResetStateBeforeScenario(false))
            .useStepMonitor(stepMonitor)
            .useParameterConverters(parameterConverters);
    }

    @Override
    public InjectableStepsFactory stepsFactory() {
        ApplicationContext context = new SpringApplicationContextFactory("classpath:/promua-steps.xml").createApplicationContext();
        return new SpringStepsFactory(configuration(), context);
    }

    @Override
    protected List<String> storyPaths() {
        return new StoryFinder().findPaths(codeLocationFromClass(this.getClass()).getFile(),
            asList("**/" + System.getProperty("storyFilter", "*") + ".story"), null);
    }
}
