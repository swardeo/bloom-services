package model;

import static java.util.List.copyOf;
import static util.ObjectValidator.checkNull;
import static util.RangeValidator.validateAdjustmentDates;
import static util.RangeValidator.validateOneTimePaymentDates;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = Debt.Builder.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Debt {

    private final Name name;
    private final Amount startAmount;
    private final Amount monthlyAmount;
    private final Date startDate;
    private final Rate yearlyRate;
    private final List<Adjustment> adjustments;
    private final List<OneTimePayment> oneTimePayments;

    public Debt(Builder builder) {
        validateDebt(builder);
        this.name = builder.name;
        this.startAmount = builder.startAmount;
        this.monthlyAmount = builder.monthlyAmount;
        this.startDate = builder.startDate;
        this.yearlyRate = builder.yearlyRate;
        this.adjustments = null != builder.adjustments ? copyOf(builder.adjustments) : List.of();
        this.oneTimePayments =
                null != builder.oneTimePayments ? copyOf(builder.oneTimePayments) : List.of();
    }

    private static void validateDebt(Builder builder) {
        checkNull(builder.name, "name");
        checkNull(builder.startAmount, "startAmount");
        checkNull(builder.monthlyAmount, "monthlyAmount");
        checkNull(builder.startDate, "startDate");
        checkNull(builder.yearlyRate, "yearlyRate");

        if (0 > builder.startAmount.getAmount().compareTo(BigDecimal.ZERO)) {
            throw new IllegalArgumentException("startAmount cannot be negative");
        }
        if (null != builder.adjustments && 0 < builder.adjustments.size()) {
            validateAdjustmentDates(
                    builder.startDate.getDate(),
                    YearMonth.of(2050, 12),
                    builder.adjustments,
                    "adjustment dates should be in range (startDate, 2050-12)");
        }
        if (null != builder.oneTimePayments && 0 < builder.oneTimePayments.size()) {
            validateOneTimePaymentDates(
                    builder.startDate.getDate(),
                    YearMonth.of(2050, 12),
                    builder.oneTimePayments,
                    "oneTimePayment dates should be in range (startDate, 2050-12)");
        }
    }

    public Name getName() {
        return name;
    }

    public Amount getStartAmount() {
        return startAmount;
    }

    public Amount getMonthlyAmount() {
        return monthlyAmount;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Rate getYearlyRate() {
        return yearlyRate;
    }

    public List<Adjustment> getAdjustments() {
        return adjustments;
    }

    public List<OneTimePayment> getOneTimePayments() {
        return oneTimePayments;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @JsonPOJOBuilder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
        private Name name;
        private Amount startAmount;
        private Amount monthlyAmount;
        private Date startDate;
        private Rate yearlyRate;
        private List<Adjustment> adjustments;
        private List<OneTimePayment> oneTimePayments;

        private Builder() {}

        public Builder withName(Name name) {
            this.name = name;
            return this;
        }

        public Builder withStartAmount(Amount startAmount) {
            this.startAmount = startAmount;
            return this;
        }

        public Builder withMonthlyAmount(Amount monthlyAmount) {
            this.monthlyAmount = monthlyAmount;
            return this;
        }

        public Builder withStartDate(Date startDate) {
            this.startDate = startDate;
            return this;
        }

        public Builder withYearlyRate(Rate yearlyRate) {
            this.yearlyRate = yearlyRate;
            return this;
        }

        public Builder withAdjustments(List<Adjustment> adjustments) {
            this.adjustments = adjustments;
            return this;
        }

        public Builder withOneTimePayments(List<OneTimePayment> oneTimePayments) {
            this.oneTimePayments = oneTimePayments;
            return this;
        }

        public Debt build() {
            return new Debt(this);
        }
    }
}
