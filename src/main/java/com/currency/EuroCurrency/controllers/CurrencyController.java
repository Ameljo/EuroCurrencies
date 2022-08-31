package com.currency.EuroCurrency.controllers;

import com.currency.EuroCurrency.exceptions.CurrencyNotExistException;
import com.currency.EuroCurrency.models.Currency;
import com.currency.EuroCurrency.models.CurrencyDay;
import com.currency.EuroCurrency.services.CurrencyDayService;
import com.currency.EuroCurrency.services.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;


/**
 *
 */
@RestController
@RequestMapping(path = "/currencies")
public class CurrencyController {

    private static final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");


    @Autowired
    private CurrencyDayService currencyDayService;
    @Autowired
    private CurrencyService currencyService;

    /**
     * Will try to contact currency server and load currencies into DB.
     * Will load only currencies that are not already in DB.
     * Slow. A better solution would be to use a cron job to update currencies every day.
     * @return Confirmation message.
     */
    @GetMapping(
            path = "/loadCurrencies")
    public String loadCurrencies()
    {
        try {
           currencyDayService.loadCurrenciesIntoDBBatch();
            return "Currencies loaded successfully!";
        } catch (IOException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,  "Failed to load data from the server!");
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,  "Failed to load data from the server!");
        } catch (ParseException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,  "Failed to load data from the server!");
        }
    }

    /**
     * @param date Date in format dd/MM/yyyy.
     * @return Will return all currency values in the DB for the specified date.
     * Formatted in simple text
     */
    @GetMapping(
            path = "/currenciesByDate")
    public String getCurrenciesByDate(@RequestParam Optional<String> date)
    {
        if (!date.isPresent())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,  "Date not provided! Please provide a date in the format dd/mm/yyyy");
        try {
            Date dateToFind = formatter.parse(date.get());
            CurrencyDay currencyDay = currencyDayService.getCurrencyDay(dateToFind);
            if (currencyDay == null)
                return "No currencies found for the specified date!";
            StringBuilder sb = new StringBuilder();
            sb.append("Currencies for " + formatter.format(currencyDay.getCurrencyDate()) + ":\n");
            for (Currency currency : currencyDay.getCurrencyList()) {
                if (currency.getValue() > 0) {
                    sb.append("\t" + currency.getCurrency() + ": " + currency.getValue() + ",\n");
                } else {
                    sb.append("\t" + currency.getCurrency() + ": N/A,\n");
                }
            }
            return sb.toString();
        } catch (ParseException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,  "Bad Date Format! Please provide a date in the format dd/mm/yyyy");
        }
    }

    /**
     * @param currency Currency to search for.
     * @param startDate Start date in format dd/MM/yyyy.
     * @param endDate   End date in format dd/MM/yyyy.
     * @return Will return all the currency values for the specified currency in the specified date range.
     * Formatted in simple text format
     */
    @GetMapping(
            path = "/betweenDates")
    public String getCurrencyBetweenDates(@RequestParam Optional<String> currency, @RequestParam Optional<String> startDate,  @RequestParam Optional<String> endDate)
    {
        //TODO: 04/05/202f will still pass thorugh and converted to 04/05/202. Some regex may be needed for more validation
        if (!startDate.isPresent())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,  "'startDate' not provided! Please provide a start date in the format dd/mm/yyyy");
        if (!endDate.isPresent())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,  "'endDate' not provided! Please provide a end date in the format dd/mm/yyyy");
        if (!currency.isPresent())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,  "'currency' not provided! Please provide a currency");
        try {
            Date start = formatter.parse(startDate.get());
            Date end = formatter.parse(endDate.get());
            if(start.after(end))
                return "Start date cannot be after End date";
            String currencyToFind = currency.get();
            List<Currency> currencyList = currencyService.getCurrenciesBetweenDates(currencyToFind, start, end);
            if (currencyList.isEmpty())
                return "No currency rates found for " + currencyToFind + " between " + startDate.get() + " and " + endDate.get();
            StringBuilder sb = new StringBuilder();
            sb.append(currencyToFind + " currency rate between " + startDate.get() + " and " + endDate.get() + ":\n");
            for (Currency currencyItem : currencyList) {
                if (currencyItem.getValue() > 0) {
                    sb.append("\t" + formatter.format(currencyItem.getDay().getCurrencyDate()) + ": "
                            + currencyItem.getValue() + ",\n");
                } else {
                    sb.append("\t" + formatter.format(currencyItem.getDay().getCurrencyDate()) + ": "
                            + "N/A,\n");
                }
            }
            sb.append("\n");
            return sb.toString();
        } catch (ParseException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,  "Bad Date Format! Please provide a date in the format dd/mm/yyyy");
        }
    }

    /**
     * @param currency Currency to search for.
     * @param startDate Start date in format dd/MM/yyyy.
     * @param endDate   End date in format dd/MM/yyyy.
     * @return Will return the highest currency value for the specified currency in the specified date range.
     * Formatted in simple text format
     */
    @GetMapping(
            path = "/highestBetweenDates")
    public String getHighestCurrencyValueBetweenDates(@RequestParam Optional<String> currency, @RequestParam Optional<String> startDate,  @RequestParam Optional<String> endDate)
    {
        if (!startDate.isPresent())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,  "'startDate' not provided! Please provide a start date in the format dd/mm/yyyy");
        if (!endDate.isPresent())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,  "'endDate' not provided! Please provide a end date in the format dd/mm/yyyy");
        if (!currency.isPresent())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,  "'currency' not provided! Please provide a currency");
        try {
            Date start = formatter.parse(startDate.get());
            Date end = formatter.parse(endDate.get());
            if(start.after(end))
                return "Start date cannot be after End date";
            String currencyToFind = currency.get();
            StringBuilder sb = new StringBuilder();

            Double highestValue = currencyService.getHighestCurrencyValueBetweenDates(currencyToFind, start, end);
            if (highestValue == null)
                return "No currency rates found for " + currencyToFind + " between " + startDate.get() + " and " + endDate.get();
            sb.append("Highest " + currencyToFind + " currency rate between " + startDate.get() + " and " + endDate.get() + ": " + highestValue );

            return sb.toString();
        } catch (ParseException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,  "Bad Date Format! Please provide a date in the format dd/mm/yyyy");
        }
    }

    /**
     * @param currency Currency to search for.
     * @param startDate Start date in format dd/MM/yyyy.
     * @param endDate  End date in format dd/MM/yyyy.
     * @return Will return the average currency value for the specified currency in the specified date range.
     * Formatted in simple text format
     */
    @GetMapping(
            path = "/avgBetweenDates")
    public String getAvgCurrencyValueBetweenDates(@RequestParam Optional<String> currency, @RequestParam Optional<String> startDate,  @RequestParam Optional<String> endDate)
    {
        if (!startDate.isPresent())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,  "'startDate' not provided! Please provide a start date in the format dd/mm/yyyy");
        if (!endDate.isPresent())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,  "'endDate' not provided! Please provide a end date in the format dd/mm/yyyy");
        if (!currency.isPresent())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,  "'currency' not provided! Please provide a currency");
        try {
            Date start = formatter.parse(startDate.get());
            Date end = formatter.parse(endDate.get());
            if(start.after(end))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,  "Start date cannot be after End date");
            String currencyToFind = currency.get();
            StringBuilder sb = new StringBuilder();
            sb.append("Average " + currencyToFind + " between " + startDate.get() + " and " + endDate.get() + ": ");
            Double avgValue = currencyService.getAvgCurrencyValueBetweenDates(currencyToFind, start, end);
            if (avgValue == null)
                return "No currency rates found for " + currencyToFind + " between " + startDate.get() + " and " + endDate.get();
            sb.append(avgValue);

            return sb.toString();
        } catch (ParseException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,  "Bad Date Format! Please provide a date in the format dd/mm/yyyy");
        }
    }

    /**
     * @param sourceCurrency Source Currency to convert.
     * @param targetCurrency Target Currency to be converted into.
     * @param date          Date in format dd/MM/yyyy.
     * @param amount       Amount to convert.
     * @return Will return the converted amount.
     * Formatted in simple text format
     */
    @GetMapping(
            path = "/convert")
    public String convertCurrencies(@RequestParam(required = true) Optional<String> sourceCurrency, @RequestParam Optional<String> targetCurrency,
                                                   @RequestParam Optional<String> date, @RequestParam Optional<Double> amount)
    {
        if (!sourceCurrency.isPresent())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,  "'sourceCurrency' not provided! Please provide a currency");
        if (!targetCurrency.isPresent())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,  "'targetCurrency' not provided! Please provide a currency");
        if (!date.isPresent())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,  "'date' not provided! Please provide a end date in the format dd/mm/yyyy");
        if (!amount.isPresent())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,  "'amount' not provided! Please provide an amount");

        try {
            Date convertDate = formatter.parse(date.get());
            StringBuilder sb = new StringBuilder();
            try {
                sb.append("Converted " + amount.get() + " " + sourceCurrency.get() + " to " + targetCurrency.get() + " on " + date.get() + ": ");
                sb.append(currencyService.convertCurrency(sourceCurrency.get(), targetCurrency.get(), convertDate, amount.get()));
            } catch (CurrencyNotExistException e) {
                return e.getMessage();
            }

            return sb.toString();
        } catch (ParseException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,  "Bad Date Format! Please provide a date in the format dd/mm/yyyy");
        }
    }

}