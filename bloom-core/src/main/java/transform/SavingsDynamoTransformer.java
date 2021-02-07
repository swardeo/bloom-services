package transform;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import model.Adjustment;
import model.Amount;
import model.Date;
import model.Name;
import model.OneTimePayment;
import model.Rate;
import model.Saving;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

public class SavingsDynamoTransformer {

    public SavingsDynamoTransformer() {}

    public List<Saving> toSavingsList(QueryResponse response) {
        List<Saving> savingsList = new ArrayList<>();

        for (Map<String, AttributeValue> attributeValueMap : response.items()) {
            Saving saving = toSaving(attributeValueMap);
            savingsList.add(saving);
        }

        return savingsList;
    }

    public Saving toSaving(Map<String, AttributeValue> attributeValueMap) {
        Saving.Builder builder = Saving.newBuilder();

        builder.withName(new Name(attributeValueMap.get("SK").s().split("#")[1]));
        builder.withStartAmount(new Amount(attributeValueMap.get("StartAmount").s()));
        builder.withMonthlyAmount(new Amount(attributeValueMap.get("MonthlyAmount").s()));
        builder.withStartDate(new Date(attributeValueMap.get("StartDate").s()));
        builder.withEndDate(new Date(attributeValueMap.get("EndDate").s()));
        builder.withYearlyRate(new Rate(attributeValueMap.get("YearlyRate").s()));

        List<Adjustment> adjustments = createAdjustmentsList(attributeValueMap.get("Adjustments"));
        builder.withAdjustments(adjustments);

        List<OneTimePayment> oneTimePayments =
                createOneTimePaymentsList(attributeValueMap.get("OneTimePayments"));
        builder.withOneTimePayments(oneTimePayments);

        return builder.build();
    }

    private static List<Adjustment> createAdjustmentsList(AttributeValue adjustmentsAttribute) {
        List<Adjustment> adjustments = new ArrayList<>();
        for (AttributeValue attributeValue : adjustmentsAttribute.l()) {
            Adjustment adjustment =
                    new Adjustment(
                            new Amount(attributeValue.m().get("Amount").s()),
                            new Date(attributeValue.m().get("DateFrom").s()),
                            new Rate(attributeValue.m().get("Rate").s()));
            adjustments.add(adjustment);
        }
        return adjustments;
    }

    private static List<OneTimePayment> createOneTimePaymentsList(
            AttributeValue oneTimePaymentsAttribute) {
        List<OneTimePayment> oneTimePayments = new ArrayList<>();
        for (AttributeValue attributeValue : oneTimePaymentsAttribute.l()) {
            OneTimePayment oneTimePayment =
                    new OneTimePayment(
                            new Amount(attributeValue.m().get("Amount").s()),
                            new Date(attributeValue.m().get("Date").s()));
            oneTimePayments.add(oneTimePayment);
        }
        return oneTimePayments;
    }
}
