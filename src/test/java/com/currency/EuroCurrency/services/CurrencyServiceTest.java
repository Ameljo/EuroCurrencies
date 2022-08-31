package com.currency.EuroCurrency.services;

import com.currency.EuroCurrency.exceptions.CurrencyNotExistException;
import com.currency.EuroCurrency.models.Currency;
import com.currency.EuroCurrency.models.CurrencyDay;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
class CurrencyServiceTest {

    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private CurrencyDayService currencyDayService;

    @Test
    void canSaveCurrencyObject() {
        Currency currency = new Currency();
        currency.setCurrency("USD");
        currency.setValue(116.98);

        CurrencyDay day = new CurrencyDay();
        day.setCurrencyDate(new Date());
        currency.setDay(day);

        Assert.assertNotNull(currencyService.saveOne(currency));
    }


    @Test
    public void getCurrenciesBetweenDates() {
        Currency currency = new Currency();
        currency.setCurrency("USD");
        currency.setValue(116.98);

        CurrencyDay day = new CurrencyDay();
        day.setCurrencyDate(new Date("05/06/2048"));
        currency.setDay(day);
        currencyService.saveOne(currency);
        currency.setId(null);
        day.setCurrencyDate(new Date("05/09/2049"));
        day.setId(null);
        currency.setDay(day);
        currencyService.saveOne(currency);
        currency.setId(null);
        day.setId(null);
        day.setCurrencyDate(new Date("05/11/2050"));
        currency.setDay(day);
        currencyService.saveOne(currency);

        List<Currency> currencies = currencyService.getCurrenciesBetweenDates("USD",new Date("05/05/2048"), new Date("05/10/2049"));

        Assert.assertEquals(2, currencies.size());
    }

    @Test
    public void getHighestCurrencyValueBetweenDates() {
        Currency currency = new Currency();
        currency.setCurrency("USD");
        currency.setValue(116.98);

        CurrencyDay day = new CurrencyDay();
        day.setCurrencyDate(new Date("05/06/2051"));
        currency.setDay(day);
        currencyService.saveOne(currency);
        currency.setId(null);
        day.setCurrencyDate(new Date("05/09/2052"));
        day.setId(null);
        currency.setDay(day);
        currency.setValue(119.98);
        currencyService.saveOne(currency);
        currency.setId(null);
        day.setId(null);
        day.setCurrencyDate(new Date("05/11/2053"));
        currency.setDay(day);
        currency.setValue(130.98);
        currencyService.saveOne(currency);

        Double highestCurrency = currencyService.getHighestCurrencyValueBetweenDates("USD",new Date("05/05/2051"), new Date("05/10/2052"));
        Assertions.assertEquals(119.98, (double) highestCurrency);
    }

    @Test
    public void getAvgCurrencyValueBetweenDates() {
        Currency currency = new Currency();
        currency.setCurrency("USD");
        currency.setValue(116.98);

        CurrencyDay day = new CurrencyDay();
        day.setCurrencyDate(new Date("05/06/2045"));
        currency.setDay(day);
        currencyService.saveOne(currency);
        currency.setId(null);
        day.setCurrencyDate(new Date("05/09/2046"));
        day.setId(null);
        currency.setDay(day);
        currency.setValue(119.98);
        currencyService.saveOne(currency);
        currency.setId(null);
        day.setId(null);
        day.setCurrencyDate(new Date("05/11/2047"));
        currency.setDay(day);
        currency.setValue(130.98);
        currencyService.saveOne(currency);

        Double avgCurrency = currencyService.getAvgCurrencyValueBetweenDates("USD",new Date("05/05/2045"), new Date("05/10/2047"));
        Assertions.assertEquals(118.48, (double) avgCurrency);
    }

    @Test
    @Transactional
    public void convertCurrencyWhenCurrencyExists() {
        Currency currency = new Currency();
        currency.setCurrency("USD");
        currency.setValue(1.007);

        CurrencyDay day = new CurrencyDay();
        day.setCurrencyDate(new Date("05/06/2045"));
        day = currencyDayService.saveOne(day);
        currency.setDay(day);
        currencyService.saveOne(currency);
        Currency currencyAud = new Currency();
        currencyAud.setCurrency("AUD");
        currencyAud.setValue(1.4333);
        currencyAud.setDay(day);
        currencyService.saveOne(currencyAud);

        try {
            Double convertedCurrency = currencyService.convertCurrency("USD", "AUD", new Date("05/06/2045"), 100);
            Assertions.assertEquals(70.25744784762435, (double) convertedCurrency);
        } catch (CurrencyNotExistException e) {
            e.printStackTrace();
        }

    }
}