package com.currency.EuroCurrency.repositories;

import com.currency.EuroCurrency.models.CurrencyDay;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

public interface CurrencyDayRepository extends JpaRepository<CurrencyDay, Long> {

    public boolean existsByCurrencyDate(Date currencyDate);

    public CurrencyDay findByCurrencyDate(Date currencyDate);

    public CurrencyDay findTop1ByOrderByCurrencyDateDesc();
}
