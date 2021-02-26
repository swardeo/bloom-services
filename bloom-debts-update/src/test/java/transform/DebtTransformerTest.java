package transform;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static software.amazon.awssdk.services.dynamodb.model.AttributeValue.builder;

import java.util.List;
import java.util.Map;
import model.Adjustment;
import model.Amount;
import model.Date;
import model.Debt;
import model.Name;
import model.OneTimePayment;
import model.Rate;
import model.Subject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

class DebtTransformerTest {

    AdjustmentsTransformer adjustmentsTransformer;
    OneTimePaymentsTransformer oneTimePaymentsTransformer;
    DebtTransformer sut;

    Subject mockSubject;
    Debt.Builder builder;
    Name name;
    Amount startAmount;
    Amount monthlyAmount;
    Date startDate;
    Rate yearlyRate;

    @BeforeEach
    void beforeEach() {
        adjustmentsTransformer = mock(AdjustmentsTransformer.class);
        oneTimePaymentsTransformer = mock(OneTimePaymentsTransformer.class);
        sut = new DebtTransformer(adjustmentsTransformer, oneTimePaymentsTransformer);

        mockSubject = mock(Subject.class);

        name = new Name("new debt");
        startAmount = new Amount("206.78");
        monthlyAmount = new Amount("15.00");
        startDate = new Date("2012-03");
        yearlyRate = new Rate("2");

        builder =
                Debt.newBuilder()
                        .withName(name)
                        .withStartAmount(startAmount)
                        .withMonthlyAmount(monthlyAmount)
                        .withStartDate(startDate)
                        .withYearlyRate(yearlyRate);

        when(mockSubject.getSubject()).thenReturn("74sr7f7-j234fd-4385ds");
    }

    @Test
    void mapContainsRequiredAttributesWhenInvoked() {
        // given
        when(adjustmentsTransformer.toAdjustmentsAttribute(any()))
                .thenReturn(builder().l(List.of()).build());
        when(oneTimePaymentsTransformer.toOneTimePaymentsAttribute(any()))
                .thenReturn(builder().l(List.of()).build());
        Debt debt = builder.build();

        // when
        Map<String, AttributeValue> actual = sut.toAttributeMap(debt);

        // then
        assertThat(actual.get(":startAmount").s()).isEqualTo(startAmount.toString());
        assertThat(actual.get(":monthlyAmount").s()).isEqualTo(monthlyAmount.toString());
        assertThat(actual.get(":startDate").s()).isEqualTo(startDate.toString());
        assertThat(actual.get(":yearlyRate").s()).isEqualTo(yearlyRate.toString());
        assertThat(actual.get(":adjustments").l()).isEmpty();
        assertThat(actual.get(":oneTimePayments").l()).isEmpty();
    }

    @Test
    void delegatesAdjustmentMappingToTransformer() {
        // given
        Adjustment adjustment =
                new Adjustment(new Amount("12.66"), new Date("2015-11"), new Rate("1.25"));
        List<Adjustment> adjustments = List.of(adjustment);

        AttributeValue expected = builder().build();
        when(adjustmentsTransformer.toAdjustmentsAttribute(adjustments))
                .thenReturn(builder().l(expected).build());

        Debt debt = builder.withAdjustments(adjustments).build();

        // when
        Map<String, AttributeValue> actual = sut.toAttributeMap(debt);

        // then
        verify(adjustmentsTransformer).toAdjustmentsAttribute(adjustments);
        assertThat(actual.get(":adjustments").l()).containsOnly(expected);
    }

    @Test
    void delegatesOneTimePaymentMappingToTransformer() {
        // given
        OneTimePayment oneTimePayment =
                new OneTimePayment(new Amount("12.66"), new Date("2015-11"));
        List<OneTimePayment> oneTimePayments = List.of(oneTimePayment);

        AttributeValue expected = builder().build();
        when(oneTimePaymentsTransformer.toOneTimePaymentsAttribute(oneTimePayments))
                .thenReturn(builder().l(expected).build());

        Debt debt = builder.withOneTimePayments(oneTimePayments).build();

        // when
        Map<String, AttributeValue> actual = sut.toAttributeMap(debt);

        // then
        verify(oneTimePaymentsTransformer).toOneTimePaymentsAttribute(oneTimePayments);
        assertThat(actual.get(":oneTimePayments").l()).containsOnly(expected);
    }

    @Test
    void keyMapConstructsCorrectlyWhenInvoked() {
        // given

        // when
        Map<String, AttributeValue> actual = sut.toKey(name, mockSubject);

        // then
        assertThat(actual.get("PK").s()).isEqualTo("USER#" + mockSubject.getSubject());
        assertThat(actual.get("SK").s()).isEqualTo("DEBT#" + name.getName());
    }
}
