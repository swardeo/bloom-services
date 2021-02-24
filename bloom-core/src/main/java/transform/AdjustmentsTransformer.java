package transform;

import static software.amazon.awssdk.services.dynamodb.model.AttributeValue.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import model.Adjustment;
import model.Amount;
import model.Date;
import model.Rate;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class AdjustmentsTransformer {

    public AdjustmentsTransformer() {}

    public AttributeValue toAdjustmentsAttribute(List<Adjustment> adjustments) {
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

    public List<Adjustment> toAdjustmentsList(AttributeValue adjustmentsAttribute) {
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
}
