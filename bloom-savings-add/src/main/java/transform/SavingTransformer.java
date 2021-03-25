package transform;

import static software.amazon.awssdk.services.dynamodb.model.AttributeValue.builder;

import java.util.HashMap;
import java.util.Map;
import model.Saving;
import model.Subject;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class SavingTransformer {

    private final AdjustmentsTransformer adjustmentsTransformer;
    private final OneTimePaymentsTransformer oneTimePaymentsTransformer;

    public SavingTransformer() {
        this(new AdjustmentsTransformer(), new OneTimePaymentsTransformer());
    }

    SavingTransformer(
            AdjustmentsTransformer adjustmentsTransformer,
            OneTimePaymentsTransformer oneTimePaymentsTransformer) {
        this.adjustmentsTransformer = adjustmentsTransformer;
        this.oneTimePaymentsTransformer = oneTimePaymentsTransformer;
    }

    public Map<String, AttributeValue> toAttributeMap(Saving saving, Subject subject) {
        Map<String, AttributeValue> savingItem = new HashMap<>();

        savingItem.put("PK", builder().s("USER#" + subject.getSubject()).build());
        savingItem.put("SK", builder().s("SAVING#" + saving.getName().getName()).build());
        savingItem.put("StartAmount", builder().s(saving.getStartAmount().toString()).build());
        savingItem.put("MonthlyAmount", builder().s(saving.getMonthlyAmount().toString()).build());
        savingItem.put("StartDate", builder().s(saving.getStartDate().toString()).build());
        savingItem.put("EndDate", builder().s(saving.getEndDate().toString()).build());
        savingItem.put("YearlyRate", builder().s(saving.getYearlyRate().toString()).build());
        savingItem.put(
                "Adjustments",
                adjustmentsTransformer.toAdjustmentsAttribute(saving.getAdjustments()));
        savingItem.put(
                "OneTimePayments",
                oneTimePaymentsTransformer.toOneTimePaymentsAttribute(saving.getOneTimePayments()));

        return savingItem;
    }
}
