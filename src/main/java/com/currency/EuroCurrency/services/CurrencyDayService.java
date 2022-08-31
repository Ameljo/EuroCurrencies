package com.currency.EuroCurrency.services;

import com.currency.EuroCurrency.models.Currency;
import com.currency.EuroCurrency.models.CurrencyDay;
import com.currency.EuroCurrency.repositories.CurrencyDayRepository;
import com.currency.EuroCurrency.utils.CsvUtils;
import org.apache.commons.csv.CSVParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class CurrencyDayService {
    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    CurrencyDayRepository currencyDayRepository;

    public CurrencyDay getCurrencyDay(Date currencyDate) {
        return currencyDayRepository.findByCurrencyDate(currencyDate);
    }

    public CurrencyDay saveOne (CurrencyDay currencyDay) {
        return currencyDayRepository.save(currencyDay);
    }

    public List<CurrencyDay> saveAll (List<CurrencyDay> currencyDays) {
        List<CurrencyDay> onlyNonExistingDays = currencyDays.stream().filter(currencyDay -> !currencyDayRepository.existsByCurrencyDate(currencyDay.getCurrencyDate())).collect(Collectors.toList());
        return currencyDayRepository.saveAll(onlyNonExistingDays);
    }

    public boolean dayExistsInDB(Date day) {
        return currencyDayRepository.existsByCurrencyDate(day);
    }

    public void loadCurrenciesIntoDBBatch() throws IOException, ParseException {
        List<CurrencyDay> currencyDays = CsvUtils.readCsvContent();
        this.saveAll(currencyDays);
    }

//TODO: Method can be used with a cron job to load currencies and avoid loading csv everytime maybe.
    /**
     * @return true if the latest row (CurrencuDay.currencyDate)  in db is older than today
     */
    public boolean isUpdateNeeded() {
        CurrencyDay currencyDay = currencyDayRepository.findTop1ByOrderByCurrencyDateDesc();
        Calendar today = Calendar.getInstance();
        Calendar specifiedDate  = Calendar.getInstance();
        specifiedDate.setTime(currencyDay.getCurrencyDate());

        return !(today.get(Calendar.DAY_OF_MONTH) == specifiedDate.get(Calendar.DAY_OF_MONTH)
                &&  today.get(Calendar.MONTH) == specifiedDate.get(Calendar.MONTH)
                &&  today.get(Calendar.YEAR) == specifiedDate.get(Calendar.YEAR));
    }
}
