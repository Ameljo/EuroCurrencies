package com.currency.EuroCurrency.services;

import com.currency.EuroCurrency.models.CurrencyDay;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
//TODO - add tests for CurrencyDayService maybe
@RunWith(SpringRunner.class)
@SpringBootTest
public class CurrencyDayServiceTest {

    @Autowired
    private CurrencyDayService currencyDayService;

    @Test
    public void canFindAnExistingDay(){
        CurrencyDay currencyDay = new CurrencyDay();
        Date now = new Date();
        currencyDay.setCurrencyDate(now);
        currencyDayService.saveOne(currencyDay);

        Assert.assertTrue(currencyDayService.dayExistsInDB(now));
    }

    @Test
    public void canFindAnNonExistingDay(){
        Date then = new Date("05/06/2045");

        Assert.assertFalse(currencyDayService.dayExistsInDB(then));
    }
}