package transform;

import static software.amazon.awssdk.services.dynamodb.model.AttributeValue.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import model.Amount;
import model.Date;
import model.OneTimePayment;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class OneTimePaymentsTransformer {

    public OneTimePaymentsTransformer() {}

    public AttributeValue toOneTimePaymentsAttribute(List<OneTimePayment> oneTimePayments) {
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

    public List<OneTimePayment> toOneTimePaymentsList(AttributeValue oneTimePaymentsAttribute) {
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
