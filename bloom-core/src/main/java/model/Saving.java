package model;

import static java.util.List.copyOf;
import static util.ObjectValidator.checkNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = Saving.Builder.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Saving {

    private final Name name;
    private final Amount startAmount;
    private final Amount monthlyAmount;
    private final Date startDate;
    private final Date endDate;
    private final Rate yearlyRate;
    private final List<Adjustment> adjustments;
    private final List<OneTimePayment> oneTimePayments;

    public Saving(Builder builder) {
        validateSaving(builder);
        this.name = builder.name;
        this.startAmount = builder.startAmount;
        this.monthlyAmount = builder.monthlyAmount;
        this.startDate = builder.startDate;
        this.endDate = builder.endDate;
        this.yearlyRate = builder.yearlyRate;
        this.adjustments = null != builder.adjustments ? copyOf(builder.adjustments) : List.of();
        this.oneTimePayments =
                null != builder.oneTimePayments ? copyOf(builder.oneTimePayments) : List.of();
    }

    private static void validateSaving(Builder builder) {
        checkNull(builder.name, "name");
        checkNull(builder.startAmount, "startAmount");
        checkNull(builder.monthlyAmount, "monthlyAmount");
        checkNull(builder.startDate, "startDate");
        checkNull(builder.endDate, "endDate");
        checkNull(builder.yearlyRate, "yearlyRate");

        if (1 != builder.startAmount.getAmount().compareTo(BigDecimal.ZERO)) {
            throw new IllegalArgumentException("startAmount cannot be negative");
        }
        if (builder.endDate.getDate().isBefore(builder.startDate.getDate())) {
            throw new IllegalArgumentException("endDate cannot be before startDate");
        }
        if (null != builder.adjustments && 0 < builder.adjustments.size()) {
            validateAdjustmentDates(
                    builder.startDate.getDate(), builder.endDate.getDate(), builder.adjustments);
        }
        if (null != builder.oneTimePayments && 0 < builder.oneTimePayments.size()) {
            validateOneTimePaymentDates(
                    builder.startDate.getDate(),
                    builder.endDate.getDate(),
                    builder.oneTimePayments);
        }
    }

    private static void validateAdjustmentDates(
            YearMonth startDate, YearMonth endDate, List<Adjustment> adjustments) {
        List<Adjustment> sortedAdjustments = new ArrayList<>(adjustments);
        sortedAdjustments.sort(Comparator.comparing(a -> a.getDateFrom().getDate()));

        YearMonth firstAdjustmentDate = sortedAdjustments.get(0).getDateFrom().getDate();
        YearMonth lastAdjustmentDate =
                sortedAdjustments.get(sortedAdjustments.size() - 1).getDateFrom().getDate();

        if (1 > firstAdjustmentDate.compareTo(startDate)
                || 1 > endDate.compareTo(lastAdjustmentDate)) {
            throw new IllegalArgumentException(
                    "adjustment date should be in range (startDate, endDate)");
        }
    }

    private static void validateOneTimePaymentDates(
            YearMonth startDate, YearMonth endDate, List<OneTimePayment> oneTimePayments) {
        List<OneTimePayment> sortedOneTimePayments = new ArrayList<>(oneTimePayments);
        sortedOneTimePayments.sort(Comparator.comparing(o -> o.getDate().getDate()));

        YearMonth firstOneTimePaymentDate = sortedOneTimePayments.get(0).getDate().getDate();
        YearMonth lastOneTimePaymentDate =
                sortedOneTimePayments.get(sortedOneTimePayments.size() - 1).getDate().getDate();

        if (1 > firstOneTimePaymentDate.compareTo(startDate)
                || 1 > endDate.compareTo(lastOneTimePaymentDate)) {
            throw new IllegalArgumentException(
                    "oneTimePayment date should be in range (startDate, endDate)");
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

    public Date getEndDate() {
        return endDate;
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
        private Date endDate;
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

        public Builder withEndDate(Date endDate) {
            this.endDate = endDate;
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

        public Saving build() {
            return new Saving(this);
        }
    }
}
