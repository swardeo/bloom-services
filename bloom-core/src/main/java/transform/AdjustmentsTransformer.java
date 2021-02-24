package transform;

import static software.amazon.awssdk.services.dynamodb.model.AttributeValue.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import model.Adjustment;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class AdjustmentsTransformer {

    public AdjustmentsTransformer() {}

    public AttributeValue createAdjustmentsAttribute(List<Adjustment> adjustments) {
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
}
