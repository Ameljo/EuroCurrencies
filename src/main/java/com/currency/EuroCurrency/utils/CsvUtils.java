package com.currency.EuroCurrency.utils;

import com.currency.EuroCurrency.models.Currency;
import com.currency.EuroCurrency.models.CurrencyDay;
import com.currency.EuroCurrency.services.CurrencyDayService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.IntStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class CsvUtils {

    private static final String EURO_URL = "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-hist.zip";
    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * @return List of CurrencyDay objects with currency data for every day
     * @throws RuntimeException
     * @throws IOException
     */
    public static List<CurrencyDay> readCsvContent() throws RuntimeException, IOException {
        List<CurrencyDay> currencyDays = new ArrayList<>();
        CSVParser parser = getCSVParser();

        // Switch places for key and value in the header map used by the parser
        // to get the correct currency code using the currency index in the csv file
        Map<Integer, String> inverseHeaderMap = new HashMap<>();
        parser.getHeaderMap().forEach((k, v) -> inverseHeaderMap.put(v, k));

        parser.stream().forEach(record -> {
            CurrencyDay currencyDay = new CurrencyDay();
            try {
                currencyDay.setCurrencyDate(formatter.parse(record.get("Date")));
            } catch (ParseException e) {
               throw new RuntimeException(e);
            }
            currencyDay.setCurrencyList(new ArrayList<>());
            // Iterate the row. Using IntStream.range to keep an index for the currency code
            // start at 1 to skip the date column
            IntStream.range(1, record.size()).forEach(index -> {
                Currency currency = new Currency();
                currency.setCurrency(inverseHeaderMap.get(index));
                if (record.get(index) != null && record.get(index).equals("N/A")) {
                    currency.setValue(-1.0);
                }else if (!record.get(index).isEmpty()) {
                    currency.setValue(Double.parseDouble(record.get(index)));
                }
                currency.setDay(currencyDay);
                currencyDay.getCurrencyList().add(currency);
            });
            currencyDays.add(currencyDay);
        });

        return currencyDays;
    }

    /**
     * Will try to contact the server to read the currencies.
     * Will download from the server the zip file and unzip it.
     * then it will create the csvParser.
     * @return CSVParser with the Euro currency rates
     * @throws IOException
     */
    public static CSVParser getCSVParser() throws IOException {
        InputStream csvStream = null;
        InputStream inputStream = new BufferedInputStream(new URL(EURO_URL).openStream());
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        ZipEntry entry = zipInputStream.getNextEntry();
        while (entry != null) {
            if (entry.getName().equals("eurofxref-hist.csv")) {
                csvStream = new BufferedInputStream(zipInputStream);
                break;
            }
            entry = zipInputStream.getNextEntry();
        }

        CSVParser parser = new CSVParser(new InputStreamReader(csvStream, "UTF-8"), CSVFormat.EXCEL.withFirstRecordAsHeader());

        return parser;
    }

}
