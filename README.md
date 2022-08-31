Code Exercise Project

It's a simple api using Spring MVC, Spring Data JPA, PostgreSQL, JUnit, and Spring Boot.

There are two postgres databases configured:

    1. currencies: contains all the currencies and their exchange rates
    2. currenciesTest: same as above, but it is used only for tests.
        it is cleaned everytime the tests are run.

Tests don't have 100% coverage, because it was a little bet time consuming. 
But I tried to do a test for every type of test needed to show you how I would 
do the remaining tests.