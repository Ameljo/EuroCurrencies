package com.currency.EuroCurrency.models;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "currency_day",indexes = {
        @Index(name = "currency_date_index", columnList = "currencyDate")
})
public class CurrencyDay {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private Date currencyDate;

    @OneToMany(mappedBy = "day", cascade = CascadeType.ALL)
    private List<Currency> currencyList;

    public Date getCurrencyDate() {
        return currencyDate;
    }

    public void setCurrencyDate(Date currencyDate) {
        this.currencyDate = currencyDate;
    }

    public List<Currency> getCurrencyList() {
        return currencyList;
    }

    public void setCurrencyList(List<Currency> currencyList) {
        this.currencyList = currencyList;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
