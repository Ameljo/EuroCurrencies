package com.currency.EuroCurrency.repositories;

import com.currency.EuroCurrency.models.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface CurrencyRepository extends JpaRepository<Currency, Long> {

    public List<Currency> findByCurrencyAndDay_currencyDateIsBetween(String currency, Date startDate, Date endDate);

    public Currency findByCurrencyAndDay_currencyDate(String currency, Date currencyDate);

    @Query(value = "select max(value) from currency c inner join currency_day cd on c.currency_day_id = cd.id " +
            "where c.currency = ?1 AND cd.currency_date between ?2 and ?3", nativeQuery = true)
    public Double getHighestCurrencyValueBetweenDates(String currency, Date startDate, Date endDate);

    @Query(value = "select avg(value) from currency c inner join currency_day cd on c.currency_day_id = cd.id " +
            "where c.value > 0 AND c.currency = ?1 AND cd.currency_date between ?2 and ?3", nativeQuery = true)
    public Double getAvgCurrencyValueBetweenDates(String currency, Date startDate, Date endDate);
}
