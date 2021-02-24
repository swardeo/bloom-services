package util;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import model.Adjustment;
import model.OneTimePayment;

public class RangeValidator {

    public static void validateAdjustmentDates(
            YearMonth startDate,
            YearMonth endDate,
            List<Adjustment> adjustments,
            String exceptionMessage) {
        List<Adjustment> sortedAdjustments = new ArrayList<>(adjustments);
        sortedAdjustments.sort(Comparator.comparing(a -> a.getDateFrom().getDate()));

        YearMonth firstAdjustmentDate = sortedAdjustments.get(0).getDateFrom().getDate();
        YearMonth lastAdjustmentDate =
                sortedAdjustments.get(sortedAdjustments.size() - 1).getDateFrom().getDate();

        if (1 > firstAdjustmentDate.compareTo(startDate)
                || 1 > endDate.compareTo(lastAdjustmentDate)) {
            throw new IllegalArgumentException(exceptionMessage);
        }
    }

    public static void validateOneTimePaymentDates(
            YearMonth startDate,
            YearMonth endDate,
            List<OneTimePayment> oneTimePayments,
            String exceptionMessage) {
        List<OneTimePayment> sortedOneTimePayments = new ArrayList<>(oneTimePayments);
        sortedOneTimePayments.sort(Comparator.comparing(o -> o.getDate().getDate()));

        YearMonth firstOneTimePaymentDate = sortedOneTimePayments.get(0).getDate().getDate();
        YearMonth lastOneTimePaymentDate =
                sortedOneTimePayments.get(sortedOneTimePayments.size() - 1).getDate().getDate();

        if (1 > firstOneTimePaymentDate.compareTo(startDate)
                || 1 > endDate.compareTo(lastOneTimePaymentDate)) {
            throw new IllegalArgumentException(exceptionMessage);
        }
    }
}
