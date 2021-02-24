package transform;

import static software.amazon.awssdk.services.dynamodb.model.AttributeValue.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import model.OneTimePayment;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class OneTimePaymentsTransformer {

    public OneTimePaymentsTransformer() {}

    public AttributeValue createOneTimePaymentsAttribute(List<OneTimePayment> oneTimePayments) {
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
