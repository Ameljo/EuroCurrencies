package com.currency.EuroCurrency.models;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "currency",
    indexes = {
        @Index(name = "currency_index", columnList = "currency")
    })
public class Currency {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String currency;

    private double value;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "currencyDay_id", nullable = false)
    private CurrencyDay day;

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CurrencyDay getDay() {
        return day;
    }

    public void setDay(CurrencyDay day) {
        this.day = day;
    }

}
