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
        Map<String, AttributeValue> savingItem = new HashMap<>();

        savingItem.put("PK", builder().s("USER#" + subject.getSubject()).build());
        savingItem.put("SK", builder().s("DEBT#" + debt.getName().getName()).build());
        savingItem.put("StartAmount", builder().s(debt.getStartAmount().toString()).build());
        savingItem.put("MonthlyAmount", builder().s(debt.getMonthlyAmount().toString()).build());
        savingItem.put("StartDate", builder().s(debt.getStartDate().toString()).build());
        savingItem.put("YearlyRate", builder().s(debt.getYearlyRate().toString()).build());
        savingItem.put(
                "Adjustments",
                adjustmentsTransformer.createAdjustmentsAttribute(debt.getAdjustments()));
        savingItem.put(
                "OneTimePayments",
                oneTimePaymentsTransformer.createOneTimePaymentsAttribute(
                        debt.getOneTimePayments()));

        return savingItem;
    }
}
