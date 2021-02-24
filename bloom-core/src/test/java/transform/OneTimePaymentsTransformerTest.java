package transform;

import static org.assertj.core.api.Assertions.assertThat;
import static software.amazon.awssdk.services.dynamodb.model.AttributeValue.builder;

import java.util.List;
import java.util.Map;
import model.Amount;
import model.Date;
import model.OneTimePayment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

class OneTimePaymentsTransformerTest {

    OneTimePaymentsTransformer sut;

    @BeforeEach
    void beforeEach() {
        sut = new OneTimePaymentsTransformer();
    }

    @Test
    void returnsEmptyListWhenNoAdjustments() {
        // given
        List<OneTimePayment> oneTimePayments = List.of();

        // when
        AttributeValue actual = sut.toOneTimePaymentsAttribute(oneTimePayments);

        // then
        assertThat(actual.l()).isEmpty();
    }

    @Test
    void returnsListOfMappedAdjustmentsWhenInvoked() {
        // given
        OneTimePayment oneTimePayment1 =
                new OneTimePayment(new Amount("12.66"), new Date("2015-11"));
        OneTimePayment oneTimePayment2 =
                new OneTimePayment(new Amount("14.26"), new Date("2018-09"));
        List<OneTimePayment> adjustments = List.of(oneTimePayment1, oneTimePayment2);

        Map<String, AttributeValue> expected1 =
                Map.of(
                        "Amount",
                        AttributeValue.builder().s(oneTimePayment1.getAmount().toString()).build(),
                        "Date",
                        AttributeValue.builder().s(oneTimePayment1.getDate().toString()).build());
        Map<String, AttributeValue> expected2 =
                Map.of(
                        "Amount",
                        AttributeValue.builder().s(oneTimePayment2.getAmount().toString()).build(),
                        "Date",
                        AttributeValue.builder().s(oneTimePayment2.getDate().toString()).build());

        // when
        AttributeValue actual = sut.toOneTimePaymentsAttribute(adjustments);

        // then
        assertThat(actual.l()).hasSize(2);
        assertThat(actual.l())
                .containsOnly(
                        AttributeValue.builder().m(expected1).build(),
                        AttributeValue.builder().m(expected2).build());
    }

    @Test
    void returnsEmptyListWhenEmptyAttributeProvided() {
        // given
        AttributeValue oneTimePayments = builder().l(List.of()).build();

        // when
        List<OneTimePayment> actual = sut.toOneTimePaymentsList(oneTimePayments);

        // then
        assertThat(actual).isEmpty();
    }

    @Test
    void returnsListWhenMappedAttributeProvided() {
        // given
        OneTimePayment oneTimePayment1 =
                new OneTimePayment(new Amount("12.66"), new Date("2015-11"));
        OneTimePayment oneTimePayment2 =
                new OneTimePayment(new Amount("14.26"), new Date("2018-09"));

        AttributeValue oneTimePaymentAttribute1 =
                AttributeValue.builder()
                        .m(
                                Map.of(
                                        "Amount",
                                        AttributeValue.builder()
                                                .s(oneTimePayment1.getAmount().toString())
                                                .build(),
                                        "Date",
                                        AttributeValue.builder()
                                                .s(oneTimePayment1.getDate().toString())
                                                .build()))
                        .build();

        AttributeValue oneTimePaymentAttribute2 =
                AttributeValue.builder()
                        .m(
                                Map.of(
                                        "Amount",
                                        AttributeValue.builder()
                                                .s(oneTimePayment2.getAmount().toString())
                                                .build(),
                                        "Date",
                                        AttributeValue.builder()
                                                .s(oneTimePayment2.getDate().toString())
                                                .build()))
                        .build();

        AttributeValue oneTimePayments =
                builder().l(oneTimePaymentAttribute1, oneTimePaymentAttribute2).build();

        // when
        List<OneTimePayment> actual = sut.toOneTimePaymentsList(oneTimePayments);

        // then
        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(List.of(oneTimePayment1, oneTimePayment2));
    }
}
