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
import model.Name;
import model.OneTimePayment;
import model.Rate;
import model.Saving;
import model.Subject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

class SavingTransformerTest {

    AdjustmentsTransformer adjustmentsTransformer;
    OneTimePaymentsTransformer oneTimePaymentsTransformer;
    SavingTransformer sut;

    Subject mockSubject;
    Saving.Builder savingBuilder;
    Name savingName;
    Amount savingStartAmount;
    Amount savingMonthlyAmount;
    Date savingStartDate;
    Date savingEndDate;
    Rate savingYearlyRate;

    @BeforeEach
    void beforeEach() {
        adjustmentsTransformer = mock(AdjustmentsTransformer.class);
        oneTimePaymentsTransformer = mock(OneTimePaymentsTransformer.class);
        mockSubject = mock(Subject.class);

        savingName = new Name("MySaving");
        savingStartAmount = new Amount("206.78");
        savingMonthlyAmount = new Amount("15.00");
        savingStartDate = new Date("2012-03");
        savingEndDate = new Date("2025-08");
        savingYearlyRate = new Rate("2");

        savingBuilder =
                Saving.newBuilder()
                        .withName(savingName)
                        .withStartAmount(savingStartAmount)
                        .withMonthlyAmount(savingMonthlyAmount)
                        .withStartDate(savingStartDate)
                        .withEndDate(savingEndDate)
                        .withYearlyRate(savingYearlyRate);

        when(mockSubject.getSubject()).thenReturn("74sr7f7-j234fd-4385ds");

        sut = new SavingTransformer(adjustmentsTransformer, oneTimePaymentsTransformer);
    }

    @Test
    void mapContainsRequiredSavingAttributesWhenInvoked() {
        // given
        when(adjustmentsTransformer.toAdjustmentsAttribute(any()))
                .thenReturn(builder().l(List.of()).build());
        when(oneTimePaymentsTransformer.toOneTimePaymentsAttribute(any()))
                .thenReturn(builder().l(List.of()).build());
        Saving saving = savingBuilder.build();

        // when
        Map<String, AttributeValue> actual = sut.toAttributeMap(saving);

        // then
        assertThat(actual.get(":startAmount").s()).isEqualTo(savingStartAmount.toString());
        assertThat(actual.get(":monthlyAmount").s()).isEqualTo(savingMonthlyAmount.toString());
        assertThat(actual.get(":startDate").s()).isEqualTo(savingStartDate.toString());
        assertThat(actual.get(":endDate").s()).isEqualTo(savingEndDate.toString());
        assertThat(actual.get(":yearlyRate").s()).isEqualTo(savingYearlyRate.toString());
        assertThat(actual.get(":adjustments").l()).hasSize(0);
        assertThat(actual.get(":oneTimePayments").l()).hasSize(0);
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

        Saving saving = savingBuilder.withAdjustments(adjustments).build();

        // when
        Map<String, AttributeValue> actual = sut.toAttributeMap(saving);

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

        Saving saving = savingBuilder.withOneTimePayments(oneTimePayments).build();

        // when
        Map<String, AttributeValue> actual = sut.toAttributeMap(saving);

        // then
        verify(oneTimePaymentsTransformer).toOneTimePaymentsAttribute(oneTimePayments);
        assertThat(actual.get(":oneTimePayments").l()).containsOnly(expected);
    }

    @Test
    void keyMapConstructsCorrectlyWhenInvoked() {
        // given

        // when
        Map<String, AttributeValue> actual = sut.toKey(savingName, mockSubject);

        // then
        assertThat(actual.get("PK").s()).isEqualTo("USER#" + mockSubject.getSubject());
        assertThat(actual.get("SK").s()).isEqualTo("SAVING#" + savingName.getName());
    }
}
