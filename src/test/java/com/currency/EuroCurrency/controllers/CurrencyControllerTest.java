package com.currency.EuroCurrency.controllers;

import com.currency.EuroCurrency.models.Currency;
import com.currency.EuroCurrency.models.CurrencyDay;
import com.currency.EuroCurrency.services.CurrencyDayService;
import com.currency.EuroCurrency.services.CurrencyService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Date;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(CurrencyController.class)
public class CurrencyControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CurrencyService currencyService;

    @MockBean
    CurrencyDayService currencyDayService;

    @Test
    public void loadCurrencies() {
    }

    @Test
    public void getCurrenciesByDateMissingDate() throws Exception {
        CurrencyDay currencyDays = new CurrencyDay();
        given(currencyDayService.getCurrencyDay(new Date("21/05/2020"))).willReturn(currencyDays);
        mvc.perform(get("/currencies/date")
                .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("Date not provided! Please provide a date in the format dd/mm/yyyy"));
    }

    @Test
    public void getCurrenciesByDateBadDateFormat() throws Exception {
        CurrencyDay currencyDays = new CurrencyDay();
        given(currencyDayService.getCurrencyDay(new Date("21/05/2020"))).willReturn(currencyDays);
        mvc.perform(get("/currencies/date?date=21-05-2020")
                .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Bad Date Format! Please provide a date in the format dd/mm/yyyy"));
    }

    @Test
    public void getCurrenciesByDateNoCurrencyFound() throws Exception {
        CurrencyDay currencyDay = new CurrencyDay();
        currencyDay.setCurrencyDate(new Date("21/05/2020"));
        currencyDay.setCurrencyList(new ArrayList<>());
        currencyDay.getCurrencyList().add(Mockito.mock(Currency.class));
        given(currencyDayService.getCurrencyDay(Mockito.any())).willReturn(currencyDay);
        mvc.perform(get("/currencies/date?date=21/05/2020")
                .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(content().string("Currencies for 05/09/2021:\n" +
                        "\tnull: N/A,\n "));
    }

    @Test
    public void getCurrenciesBetweenDates() {
    }

    @Test
    public void getHighestCurrencieValueBetweenDates() {
    }

    @Test
    public void getAvgCurrencieValueBetweenDates() {
    }

    @Test
    public void convertCurrencies() {
    }
}