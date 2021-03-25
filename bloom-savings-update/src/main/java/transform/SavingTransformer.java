package transform;

import static software.amazon.awssdk.services.dynamodb.model.AttributeValue.builder;

import java.util.HashMap;
import java.util.Map;
import model.Name;
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

    public Map<String, AttributeValue> toAttributeMap(Saving saving) {
        Map<String, AttributeValue> savingItem = new HashMap<>();

        savingItem.put(":startAmount", builder().s(saving.getStartAmount().toString()).build());
        savingItem.put(":monthlyAmount", builder().s(saving.getMonthlyAmount().toString()).build());
        savingItem.put(":startDate", builder().s(saving.getStartDate().toString()).build());
        savingItem.put(":endDate", builder().s(saving.getEndDate().toString()).build());
        savingItem.put(":yearlyRate", builder().s(saving.getYearlyRate().toString()).build());
        savingItem.put(
                ":adjustments",
                adjustmentsTransformer.toAdjustmentsAttribute(saving.getAdjustments()));
        savingItem.put(
                ":oneTimePayments",
                oneTimePaymentsTransformer.toOneTimePaymentsAttribute(saving.getOneTimePayments()));

        return savingItem;
    }

    public Map<String, AttributeValue> toKey(Name name, Subject subject) {
        Map<String, AttributeValue> key = new HashMap<>();

        key.put("PK", builder().s("USER#" + subject.getSubject()).build());
        key.put("SK", builder().s("SAVING#" + name.getName()).build());

        return key;
    }
}
