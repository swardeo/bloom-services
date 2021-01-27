package model;

import static util.ObjectValidator.checkNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OneTimePayment {

    private final Amount amount;
    private final Date date;

    public OneTimePayment(@JsonProperty("amount") Amount amount, @JsonProperty("date") Date date) {
        checkNull(amount, "amount");
        checkNull(date, "date");
        this.amount = amount;
        this.date = date;
    }

    public Amount getAmount() {
        return amount;
    }

    public Date getDate() {
        return date;
    }
}
