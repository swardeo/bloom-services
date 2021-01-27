package model;

import static util.ObjectValidator.checkNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Adjustment {

    private final Amount amount;
    private final Date dateFrom;
    private final Rate rate;

    public Adjustment(
            @JsonProperty("amount") Amount amount,
            @JsonProperty("dateFrom") Date dateFrom,
            @JsonProperty("rate") Rate rate) {
        checkNull(amount, "amount");
        checkNull(dateFrom, "dateFrom");
        checkNull(rate, "rate");
        this.amount = amount;
        this.dateFrom = dateFrom;
        this.rate = rate;
    }

    public Amount getAmount() {
        return amount;
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public Rate getRate() {
        return rate;
    }
}
