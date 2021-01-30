package transform;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.amazonaws.services.lambda.runtime.CognitoIdentity;
import java.util.List;
import java.util.Map;
import model.Adjustment;
import model.Amount;
import model.Date;
import model.Name;
import model.OneTimePayment;
import model.Rate;
import model.Saving;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

class SavingTransformerTest {

    SavingTransformer sut;

    CognitoIdentity mockCognitoIdentity;
    Saving.Builder savingBuilder;
    Name savingName;
    Amount savingStartAmount;
    Amount savingMonthlyAmount;
    Date savingStartDate;
    Date savingEndDate;
    Rate savingYearlyRate;

    @BeforeEach
    void beforeEach() {
        mockCognitoIdentity = mock(CognitoIdentity.class);

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

        when(mockCognitoIdentity.getIdentityId()).thenReturn("eu-west-2:74sr7f7-j234fd-4385ds");

        sut = new SavingTransformer();
    }

    @Test
    void mapContainsRequiredSavingAttributesWhenInvoked() {
        // given
        Saving saving = savingBuilder.build();

        // when
        Map<String, AttributeValue> actual = sut.toAttributeMap(saving, mockCognitoIdentity);

        // then
        assertThat(actual.get("PK").s()).isEqualTo("USER#" + mockCognitoIdentity.getIdentityId());
        assertThat(actual.get("SK").s()).isEqualTo("SAVING#" + savingName.getName());
        assertThat(actual.get("StartAmount").s()).isEqualTo(savingStartAmount.toString());
        assertThat(actual.get("MonthlyAmount").s()).isEqualTo(savingMonthlyAmount.toString());
        assertThat(actual.get("StartDate").s()).isEqualTo(savingStartDate.toString());
        assertThat(actual.get("EndDate").s()).isEqualTo(savingEndDate.toString());
        assertThat(actual.get("YearlyRate").s()).isEqualTo(savingYearlyRate.toString());
        assertThat(actual).doesNotContainKey("Adjustments");
        assertThat(actual).doesNotContainKey("OneTimePayments");
    }

    @Test
    void mapOptionallyContainsAdjustmentsAttributeWhenInvoked() {
        // given
        Adjustment adjustment1 =
                new Adjustment(new Amount("12.66"), new Date("2015-11"), new Rate("1.25"));
        Adjustment adjustment2 =
                new Adjustment(new Amount("14.26"), new Date("2018-09"), new Rate("1.75"));

        Saving saving = savingBuilder.withAdjustments(asList(adjustment1, adjustment2)).build();

        Map<String, AttributeValue> expected1 =
                Map.of(
                        "Amount",
                        AttributeValue.builder().s(adjustment1.getAmount().toString()).build(),
                        "DateFrom",
                        AttributeValue.builder().s(adjustment1.getDateFrom().toString()).build(),
                        "Rate",
                        AttributeValue.builder().s(adjustment1.getRate().toString()).build());
        Map<String, AttributeValue> expected2 =
                Map.of(
                        "Amount",
                        AttributeValue.builder().s(adjustment2.getAmount().toString()).build(),
                        "DateFrom",
                        AttributeValue.builder().s(adjustment2.getDateFrom().toString()).build(),
                        "Rate",
                        AttributeValue.builder().s(adjustment2.getRate().toString()).build());
        // when
        Map<String, AttributeValue> actual = sut.toAttributeMap(saving, mockCognitoIdentity);

        // then
        List<AttributeValue> adjustments = actual.get("Adjustments").l();

        assertThat(adjustments).hasSize(2);
        assertThat(adjustments)
                .containsOnly(
                        AttributeValue.builder().m(expected1).build(),
                        AttributeValue.builder().m(expected2).build());
    }

    @Test
    void mapOptionallyContainsOneTimePaymentAttributeWhenInvoked() {
        // given
        OneTimePayment oneTimePayment1 =
                new OneTimePayment(new Amount("12.66"), new Date("2015-11"));
        OneTimePayment oneTimePayment2 =
                new OneTimePayment(new Amount("14.26"), new Date("2018-09"));

        Saving saving =
                savingBuilder.withOneTimePayments(asList(oneTimePayment1, oneTimePayment2)).build();

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
        Map<String, AttributeValue> actual = sut.toAttributeMap(saving, mockCognitoIdentity);

        // then
        List<AttributeValue> oneTimePayments = actual.get("OneTimePayments").l();

        assertThat(oneTimePayments).hasSize(2);
        assertThat(oneTimePayments)
                .containsOnly(
                        AttributeValue.builder().m(expected1).build(),
                        AttributeValue.builder().m(expected2).build());
    }
}
