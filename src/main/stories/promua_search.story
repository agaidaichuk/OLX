Search Prom.UA by category

Meta:
@author Irinka V.
@category advanced

Narrative: 

In order to show the advance cart functionality
As a user
I want to search for an item in a category

Scenario: Advanced Search for a product

Given I am searching on Prom.UA
When I specify the <thing_category> category
And I search for <thing_product>
Then there are search results

Examples:
| thing_category         | thing_product             |
| Промышленные товары    | Перфоратор Makita HR 2300 |
| Потребительские товары | MJX BMW Vision            |