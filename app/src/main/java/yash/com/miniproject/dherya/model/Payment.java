package yash.com.miniproject.dherya.model;

import java.math.BigDecimal;
import java.util.Calendar;

import yash.com.miniproject.dherya.enums.Currency;

/**
 * Created by Alex on 6/8/2016.
 */
public class Payment {

    private int id;
    private String description;
    BigDecimal amount;
    Currency currency;
    Calendar date;


    public Payment (int id, String description, BigDecimal amount, Currency currency, Calendar date) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.currency = currency;

        this.date = Calendar.getInstance();
        this.date.setTimeZone(date.getTimeZone());
        this.date.setTimeInMillis(date.getTimeInMillis());
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public Calendar getDate() {
        return date;
    }


    @Override
    public String toString() {
        return  "ID=" + id + "\r\n" +
                " description=" + description + "\r\n" +
                " amount=" + amount + "\r\n" +
                " currency=" + currency + "\r\n" +
                " date=" + date;
    }
}
