package transform;

import static software.amazon.awssdk.services.dynamodb.model.AttributeValue.builder;

import java.util.HashMap;
import java.util.Map;
import model.Debt;
import model.Name;
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

    public Map<String, AttributeValue> toKey(Name name, Subject subject) {
        return Map.of(
                "PK", AttributeValue.builder().s("USER#" + subject.getSubject()).build(),
                "SK", AttributeValue.builder().s("DEBT#" + name.getName()).build());
    }

    public Map<String, AttributeValue> toAttributeMap(Debt debt) {
        Map<String, AttributeValue> debtItem = new HashMap<>();

        debtItem.put(":startAmount", builder().s(debt.getStartAmount().toString()).build());
        debtItem.put(":monthlyAmount", builder().s(debt.getMonthlyAmount().toString()).build());
        debtItem.put(":startDate", builder().s(debt.getStartDate().toString()).build());
        debtItem.put(":yearlyRate", builder().s(debt.getYearlyRate().toString()).build());
        debtItem.put(
                ":adjustments",
                adjustmentsTransformer.toAdjustmentsAttribute(debt.getAdjustments()));
        debtItem.put(
                ":oneTimePayments",
                oneTimePaymentsTransformer.toOneTimePaymentsAttribute(debt.getOneTimePayments()));

        return debtItem;
    }
}
