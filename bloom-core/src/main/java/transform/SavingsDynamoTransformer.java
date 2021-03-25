package transform;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import model.Amount;
import model.Date;
import model.Name;
import model.Rate;
import model.Saving;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

public class SavingsDynamoTransformer {

    private final AdjustmentsTransformer adjustmentsTransformer;
    private final OneTimePaymentsTransformer oneTimePaymentsTransformer;

    public SavingsDynamoTransformer() {
        this(new AdjustmentsTransformer(), new OneTimePaymentsTransformer());
    }

    SavingsDynamoTransformer(
            AdjustmentsTransformer adjustmentsTransformer,
            OneTimePaymentsTransformer oneTimePaymentsTransformer) {
        this.adjustmentsTransformer = adjustmentsTransformer;
        this.oneTimePaymentsTransformer = oneTimePaymentsTransformer;
    }

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
        builder.withAdjustments(
                adjustmentsTransformer.toAdjustmentsList(attributeValueMap.get("Adjustments")));
        builder.withOneTimePayments(
                oneTimePaymentsTransformer.toOneTimePaymentsList(
                        attributeValueMap.get("OneTimePayments")));

        return builder.build();
    }
}
