package transform;

import static org.assertj.core.api.Assertions.assertThat;
import static software.amazon.awssdk.services.dynamodb.model.AttributeValue.builder;

import java.util.List;
import java.util.Map;
import model.Adjustment;
import model.Amount;
import model.Date;
import model.Rate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

class AdjustmentsTransformerTest {

    AdjustmentsTransformer sut;

    @BeforeEach
    void beforeEach() {
        sut = new AdjustmentsTransformer();
    }

    @Test
    void returnsEmptyAttributeWhenEmptyListProvided() {
        // given
        List<Adjustment> adjustments = List.of();

        // when
        AttributeValue actual = sut.toAdjustmentsAttribute(adjustments);

        // then
        assertThat(actual.l()).isEmpty();
    }

    @Test
    void returnsMappedAttributeWhenListProvided() {
        // given
        Adjustment adjustment1 =
                new Adjustment(new Amount("12.66"), new Date("2015-11"), new Rate("1.25"));
        Adjustment adjustment2 =
                new Adjustment(new Amount("14.26"), new Date("2018-09"), new Rate("1.75"));
        List<Adjustment> adjustments = List.of(adjustment1, adjustment2);

        Map<String, AttributeValue> expected1 =
                Map.of(
                        "Amount",
                        builder().s(adjustment1.getAmount().toString()).build(),
                        "DateFrom",
                        builder().s(adjustment1.getDateFrom().toString()).build(),
                        "Rate",
                        builder().s(adjustment1.getRate().toString()).build());
        Map<String, AttributeValue> expected2 =
                Map.of(
                        "Amount",
                        builder().s(adjustment2.getAmount().toString()).build(),
                        "DateFrom",
                        builder().s(adjustment2.getDateFrom().toString()).build(),
                        "Rate",
                        builder().s(adjustment2.getRate().toString()).build());

        // when
        AttributeValue actual = sut.toAdjustmentsAttribute(adjustments);

        // then
        assertThat(actual.l()).hasSize(2);
        assertThat(actual.l())
                .containsOnly(builder().m(expected1).build(), builder().m(expected2).build());
    }

    @Test
    void returnsEmptyListWhenEmptyAttributeProvided() {
        // given
        AttributeValue adjustments = builder().l(List.of()).build();

        // when
        List<Adjustment> actual = sut.toAdjustmentsList(adjustments);

        // then
        assertThat(actual).isEmpty();
    }

    @Test
    void returnsListWhenMappedAttributeProvided() {
        // given
        Adjustment adjustment1 =
                new Adjustment(new Amount("12.66"), new Date("2015-11"), new Rate("1.25"));
        Adjustment adjustment2 =
                new Adjustment(new Amount("14.26"), new Date("2018-09"), new Rate("1.75"));

        AttributeValue adjustmentAttribute1 =
                AttributeValue.builder()
                        .m(
                                Map.of(
                                        "Amount",
                                        AttributeValue.builder()
                                                .s(adjustment1.getAmount().toString())
                                                .build(),
                                        "DateFrom",
                                        AttributeValue.builder()
                                                .s(adjustment1.getDateFrom().toString())
                                                .build(),
                                        "Rate",
                                        AttributeValue.builder()
                                                .s(adjustment1.getRate().toString())
                                                .build()))
                        .build();

        AttributeValue adjustmentAttribute2 =
                AttributeValue.builder()
                        .m(
                                Map.of(
                                        "Amount",
                                        AttributeValue.builder()
                                                .s(adjustment2.getAmount().toString())
                                                .build(),
                                        "DateFrom",
                                        AttributeValue.builder()
                                                .s(adjustment2.getDateFrom().toString())
                                                .build(),
                                        "Rate",
                                        AttributeValue.builder()
                                                .s(adjustment2.getRate().toString())
                                                .build()))
                        .build();

        AttributeValue adjustments =
                builder().l(adjustmentAttribute1, adjustmentAttribute2).build();

        // when
        List<Adjustment> actual = sut.toAdjustmentsList(adjustments);

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(List.of(adjustment1, adjustment2));
    }
}
