package model;

import static util.StringValidator.checkNullOrEmpty;

import com.fasterxml.jackson.annotation.JsonValue;
import java.math.BigDecimal;

public class Amount {

    private final BigDecimal amount;

    public Amount(String amount) {
        checkNullOrEmpty(amount, "amount");
        this.amount = new BigDecimal(amount);
    }

    public BigDecimal getAmount() {
        return amount;
    }

    @JsonValue
    @Override
    public String toString() {
        return amount.toString();
    }
}
