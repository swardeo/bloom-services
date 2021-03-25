package transform;

import static software.amazon.awssdk.services.dynamodb.model.AttributeValue.builder;

import java.util.HashMap;
import java.util.Map;
import model.Debt;
import model.Subject;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class DebtTransformer {

    private final AdjustmentsTransformer adjustmentsTransformer;
    private final OneTimePaymentsTransformer oneTimePaymentsTransformer;

    public DebtTransformer() {
        this(new AdjustmentsTransformer(), new OneTimePaymentsTransformer());
    }

    DebtTransformer(
            AdjustmentsTransformer adjustmentsTransformer,
            OneTimePaymentsTransformer oneTimePaymentsTransformer) {
        this.adjustmentsTransformer = adjustmentsTransformer;
        this.oneTimePaymentsTransformer = oneTimePaymentsTransformer;
    }

    public Map<String, AttributeValue> toAttributeMap(Debt debt, Subject subject) {
        Map<String, AttributeValue> debtItem = new HashMap<>();

        debtItem.put("PK", builder().s("USER#" + subject.getSubject()).build());
        debtItem.put("SK", builder().s("DEBT#" + debt.getName().getName()).build());
        debtItem.put("StartAmount", builder().s(debt.getStartAmount().toString()).build());
        debtItem.put("MonthlyAmount", builder().s(debt.getMonthlyAmount().toString()).build());
        debtItem.put("StartDate", builder().s(debt.getStartDate().toString()).build());
        debtItem.put("YearlyRate", builder().s(debt.getYearlyRate().toString()).build());
        debtItem.put(
                "Adjustments",
                adjustmentsTransformer.toAdjustmentsAttribute(debt.getAdjustments()));
        debtItem.put(
                "OneTimePayments",
                oneTimePaymentsTransformer.toOneTimePaymentsAttribute(debt.getOneTimePayments()));

        return debtItem;
    }
}
