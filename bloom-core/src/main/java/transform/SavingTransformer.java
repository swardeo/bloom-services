package transform;

import static software.amazon.awssdk.services.dynamodb.model.AttributeValue.builder;

import com.amazonaws.services.lambda.runtime.CognitoIdentity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Adjustment;
import model.OneTimePayment;
import model.Saving;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class SavingTransformer {

    public SavingTransformer() {}

    public Map<String, AttributeValue> toAttributeMap(Saving saving, CognitoIdentity identity) {
        Map<String, AttributeValue> savingItem = new HashMap<>();

        savingItem.put("PK", builder().s("USER#" + identity.getIdentityId()).build());
        savingItem.put("SK", builder().s("SAVING#" + saving.getName().getName()).build());
        savingItem.put("StartAmount", builder().s(saving.getStartAmount().toString()).build());
        savingItem.put("MonthlyAmount", builder().s(saving.getMonthlyAmount().toString()).build());
        savingItem.put("StartDate", builder().s(saving.getStartDate().toString()).build());
        savingItem.put("EndDate", builder().s(saving.getEndDate().toString()).build());
        savingItem.put("YearlyRate", builder().s(saving.getYearlyRate().toString()).build());

        if (null != saving.getAdjustments()) {
            savingItem.put("Adjustments", createAdjustmentsAttribute(saving.getAdjustments()));
        }

        if (null != saving.getOneTimePayments()) {
            savingItem.put(
                    "OneTimePayments", createOneTimePaymentsAttribute(saving.getOneTimePayments()));
        }

        return savingItem;
    }

    private static AttributeValue createAdjustmentsAttribute(List<Adjustment> adjustments) {
        List<AttributeValue> attributeList = new ArrayList<>();
        for (Adjustment adjustment : adjustments) {
            Map<String, AttributeValue> attribute =
                    Map.of(
                            "Amount", builder().s(adjustment.getAmount().toString()).build(),
                            "DateFrom", builder().s(adjustment.getDateFrom().toString()).build(),
                            "Rate", builder().s(adjustment.getRate().toString()).build());

            attributeList.add(builder().m(attribute).build());
        }
        return builder().l(attributeList).build();
    }

    private static AttributeValue createOneTimePaymentsAttribute(
            List<OneTimePayment> oneTimePayments) {
        List<AttributeValue> attributeList = new ArrayList<>();
        for (OneTimePayment oneTimePayment : oneTimePayments) {
            Map<String, AttributeValue> attribute =
                    Map.of(
                            "Amount", builder().s(oneTimePayment.getAmount().toString()).build(),
                            "Date", builder().s(oneTimePayment.getDate().toString()).build());
            attributeList.add(builder().m(attribute).build());
        }
        return builder().l(attributeList).build();
    }
}
