package com.currency.EuroCurrency.services;

import com.currency.EuroCurrency.exceptions.CurrencyNotExistException;
import com.currency.EuroCurrency.models.Currency;
import com.currency.EuroCurrency.repositories.CurrencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CurrencyService {

    @Autowired
    private CurrencyRepository currencyRepository;

    public Currency saveOne(Currency currency) {
        return currencyRepository.save(currency);
    }

    public List<Currency> getCurrenciesBetweenDates(String currency, Date startDate, Date endDate) {
        return currencyRepository.findByCurrencyAndDay_currencyDateIsBetween(currency, startDate, endDate);
    }

    public Double getHighestCurrencyValueBetweenDates(String currency, Date startDate, Date endDate) {
        return currencyRepository.getHighestCurrencyValueBetweenDates(currency, startDate, endDate);
    }

    public Double getAvgCurrencyValueBetweenDates(String currency, Date startDate, Date endDate) {
        return currencyRepository.getAvgCurrencyValueBetweenDates(currency, startDate, endDate);
    }

    public Double convertCurrency(String sourceCurrency, String targetCurrency, Date date, double amount) throws CurrencyNotExistException {
        Currency currency = currencyRepository.findByCurrencyAndDay_currencyDate(sourceCurrency, date);
        Currency currencyToConvertInto = currencyRepository.findByCurrencyAndDay_currencyDate(targetCurrency, date);
        if (currency == null)
            throw new CurrencyNotExistException("N/A rate found for currency " + sourceCurrency + " on the given date!");
        if (currencyToConvertInto == null)

            throw new CurrencyNotExistException("N/A rate found for currency " + targetCurrency + " on the given date!");
        Double convertValue = (currency.getValue() * amount) / currencyToConvertInto.getValue();
        return convertValue;
    }
}
