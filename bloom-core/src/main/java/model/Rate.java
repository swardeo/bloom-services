package model;

import static util.StringValidator.checkNullOrEmpty;

import com.fasterxml.jackson.annotation.JsonValue;
import java.math.BigDecimal;

public class Rate {

    private final BigDecimal rate;

    public Rate(String rate) {
        checkNullOrEmpty(rate, "rate");
        this.rate = new BigDecimal(rate);
    }

    public BigDecimal getRate() {
        return rate;
    }

    @JsonValue
    @Override
    public String toString() {
        return rate.toString();
    }
}
