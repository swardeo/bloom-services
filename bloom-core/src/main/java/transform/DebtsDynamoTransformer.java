package transform;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import model.Amount;
import model.Date;
import model.Debt;
import model.Debt.Builder;
import model.Name;
import model.Rate;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

public class DebtsDynamoTransformer {

    private final AdjustmentsTransformer adjustmentsTransformer;
    private final OneTimePaymentsTransformer oneTimePaymentsTransformer;

    public DebtsDynamoTransformer() {
        this(new AdjustmentsTransformer(), new OneTimePaymentsTransformer());
    }

    DebtsDynamoTransformer(
            AdjustmentsTransformer adjustmentsTransformer,
            OneTimePaymentsTransformer oneTimePaymentsTransformer) {
        this.adjustmentsTransformer = adjustmentsTransformer;
        this.oneTimePaymentsTransformer = oneTimePaymentsTransformer;
    }

    public List<Debt> toDebtsList(QueryResponse response) {
        List<Debt> debts = new ArrayList<>();
        for (Map<String, AttributeValue> attributeValueMap : response.items()) {
            Debt debt = toDebt(attributeValueMap);
            debts.add(debt);
        }

        return debts;
    }

    private Debt toDebt(Map<String, AttributeValue> attributeValueMap) {
        Builder builder = Debt.newBuilder();

        builder.withName(new Name(attributeValueMap.get("SK").s().split("#")[1]));
        builder.withStartAmount(new Amount(attributeValueMap.get("StartAmount").s()));
        builder.withMonthlyAmount(new Amount(attributeValueMap.get("MonthlyAmount").s()));
        builder.withStartDate(new Date(attributeValueMap.get("StartDate").s()));
        builder.withYearlyRate(new Rate(attributeValueMap.get("YearlyRate").s()));
        builder.withAdjustments(
                adjustmentsTransformer.toAdjustmentsList(attributeValueMap.get("Adjustments")));
        builder.withOneTimePayments(
                oneTimePaymentsTransformer.toOneTimePaymentsList(
                        attributeValueMap.get("OneTimePayments")));

        return builder.build();
    }
}
