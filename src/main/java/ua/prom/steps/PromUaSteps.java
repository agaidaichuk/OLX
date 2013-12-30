package ua.prom.steps;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.hamcrest.Matchers;
import org.jbehave.core.annotations.Alias;
import org.jbehave.core.annotations.Composite;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import ua.prom.pages.AdvancedSearch;
import ua.prom.pages.Buy;
import ua.prom.pages.CartContents;
import ua.prom.pages.Home;
import ua.prom.pages.SearchResults;
import ua.prom.pages.Site;

import static org.hamcrest.MatcherAssert.assertThat;

import static org.hamcrest.Matchers.equalTo;
import org.jbehave.core.annotations.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PromUaSteps {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired(required = true)
    private AdvancedSearch advancedSearch;
    @Autowired(required = true)
    private Home home;
    @Autowired(required = true)
    private Site site;
    @Autowired(required = true)
    private SearchResults searchResults;
    @Autowired(required = true)
    private CartContents cartContents;
    @Autowired(required = true)
    private Buy buy;

    private String justBought = "";

    @Given("I am shopping for a $thing in $section on Prom.UA")
    public void shoppingForSomethingOnPromUa(String thing, String section) {
        home.go(section);
        home.search(thing);
    }

    @Given("I am on Prom.UA")
    public void homepageOnPromUa() {
        home.go();
    }

    @Given("I am searching on Prom.UA")
    public void advancedSearchingOnPromUa() {
        advancedSearch.go();
    }

    @Given("that the cart is empty")
    public void cartIsEmptyAndOnStartPage() {
        home.go();
        cartIsEmpty();
    }

    @Then("the cart will be empty")
    public void cartIsEmpty() {
        assertThat(site.cartSize(), equalTo(0));
    }

    @Then("the cart contents is empty")
    public void cartContentsIsEmpty() {
        assertThat(cartContents.cartSize(), equalTo(0));
    }

    @Given("the cart contains one item")
    public void anItemInThePromUaCart() {
        cartContents.removeAllItems();
        shoppingForSomethingOnPromUa("Перфоратор", "Промышленные товары");
        cartIsEmpty();
        putThingInCart("Перфоратор");
        cartNotEmpty(1);
    }

    @When("an item is added to the cart")
    public void putThingInCart() {
        putThingInCart("Перфоратор");
    }

    @When("I search for an item")
    public void searchForItem() {
        home.search("Перфоратор");
    }

    @When("I want to browse through a gallery")
    @Composite(steps = { "When I want to buy something at Prom.UA", "When I want to browse the category",
        "When Buy something" })
    public void browseToFirstGallery() {
    }

    @When("I want to buy something at Prom.UA")
    public void selectBuySection() {
        home.go();
        home.goToBuySection();
    }

    @When("I want to browse the category")
    public void browseAnyCategory() {
        selectBuySection();
        home.browseAnyCategory();
    }

    @When("Buy something")
    public void selectFirstGallery() {
        browseAnyCategory();
        buy.buyAnyFirstThing();
    }

    @When("a $thing is placed in the cart")
    public void putThingInCart(String thing) {
        justBought = buy.buyFirst(thing);
        assertThat(justBought, Matchers.notNullValue());
    }

    @When("the item is removed")
    public void removeItem() {
        cartContents.removeItem();
    }

    @When("I specify the <thing_category> category")
    public void specifyCategory(@Named("thing_category") String category) {
        advancedSearch.category(category);
    }

    @When("I search for <thing_product>")
    public void seachForThing(@Named("thing_product") String thing) {
        advancedSearch.searchFor(thing);
    }

    @Then("the cart contains that item")
    public void cartHasThatItem() {
        assertThat(cartContents.hasItem(justBought), Matchers.is(true));
    }

    @Then("the cart has $num items")
    @Alias("the cart has $num item")
    public void cartNotEmpty(int num) {
        assertThat(site.cartSize(), Matchers.equalTo(num));
    }

    @Then("there are search results")
    @Alias("results will be displayed on UI")
    public void thereAreSearchResults() {
        assertThat(searchResults.resultsFound(), Matchers.greaterThan(0));
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
