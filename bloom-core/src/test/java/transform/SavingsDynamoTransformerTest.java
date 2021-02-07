package transform;

import static org.assertj.core.api.Assertions.assertThat;

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
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

class SavingsDynamoTransformerTest {

    SavingsDynamoTransformer sut;

    Name name;
    Amount startAmount;
    Amount monthlyAmount;
    Date startDate;
    Date endDate;
    Rate yearlyRate;
    Adjustment adjustment1;
    Adjustment adjustment2;
    OneTimePayment oneTimePayment1;
    OneTimePayment oneTimePayment2;

    Map<String, AttributeValue> baseItem;
    Map<String, AttributeValue> complexItem;

    @BeforeEach
    void beforeEach() {
        sut = new SavingsDynamoTransformer();

        name = new Name("MySaving");
        startAmount = new Amount("206.78");
        monthlyAmount = new Amount("15.00");
        startDate = new Date("2012-03");
        endDate = new Date("2020-08");
        yearlyRate = new Rate("2");

        baseItem =
                Map.of(
                        "SK", AttributeValue.builder().s("SAVING#" + name.getName()).build(),
                        "StartAmount", AttributeValue.builder().s(startAmount.toString()).build(),
                        "MonthlyAmount",
                                AttributeValue.builder().s(monthlyAmount.toString()).build(),
                        "StartDate", AttributeValue.builder().s(startDate.toString()).build(),
                        "EndDate", AttributeValue.builder().s(endDate.toString()).build(),
                        "YearlyRate", AttributeValue.builder().s(yearlyRate.toString()).build(),
                        "Adjustments", AttributeValue.builder().l(List.of()).build(),
                        "OneTimePayments", AttributeValue.builder().l(List.of()).build());

        adjustment1 = new Adjustment(new Amount("12.66"), new Date("2015-11"), new Rate("1.25"));
        adjustment2 = new Adjustment(new Amount("14.26"), new Date("2018-09"), new Rate("1.75"));
        oneTimePayment1 = new OneTimePayment(new Amount("12.66"), new Date("2015-11"));
        oneTimePayment2 = new OneTimePayment(new Amount("14.26"), new Date("2018-09"));

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

        complexItem =
                Map.of(
                        "SK", AttributeValue.builder().s("SAVING#" + name.getName()).build(),
                        "StartAmount", AttributeValue.builder().s(startAmount.toString()).build(),
                        "MonthlyAmount",
                                AttributeValue.builder().s(monthlyAmount.toString()).build(),
                        "StartDate", AttributeValue.builder().s(startDate.toString()).build(),
                        "EndDate", AttributeValue.builder().s(endDate.toString()).build(),
                        "YearlyRate", AttributeValue.builder().s(yearlyRate.toString()).build(),
                        "Adjustments",
                                AttributeValue.builder()
                                        .l(adjustmentAttribute1, adjustmentAttribute2)
                                        .build(),
                        "OneTimePayments",
                                AttributeValue.builder()
                                        .l(oneTimePaymentAttribute1, oneTimePaymentAttribute2)
                                        .build());
    }

    @Test
    void acceptsQueryRequestWhenInvoked() {
        // given
        QueryResponse response = QueryResponse.builder().build();

        // when
        sut.toSavingsList(response);

        // then
        // no exception
    }

    @Test
    void returnsSavingsListWhenInvoked() {
        // given
        QueryResponse response = QueryResponse.builder().build();

        // when
        List<Saving> actual = sut.toSavingsList(response);

        // then
        assertThat(actual).hasSize(0);
    }

    @Test
    void listContainsBasicSavingWhenInvoked() {
        // given
        QueryResponse response = QueryResponse.builder().items(baseItem).build();

        Saving expectedSaving =
                Saving.newBuilder()
                        .withName(name)
                        .withStartAmount(startAmount)
                        .withMonthlyAmount(monthlyAmount)
                        .withStartDate(startDate)
                        .withEndDate(endDate)
                        .withYearlyRate(yearlyRate)
                        .build();

        // when
        List<Saving> actual = sut.toSavingsList(response);

        // then
        assertThat(actual.get(0)).usingRecursiveComparison().isEqualTo(expectedSaving);
    }

    @Test
    void listContainsComplexSavingWhenInvoked() {
        // given
        QueryResponse response = QueryResponse.builder().items(complexItem).build();

        Saving expectedSaving =
                Saving.newBuilder()
                        .withName(name)
                        .withStartAmount(startAmount)
                        .withMonthlyAmount(monthlyAmount)
                        .withStartDate(startDate)
                        .withEndDate(endDate)
                        .withYearlyRate(yearlyRate)
                        .withAdjustments(List.of(adjustment1, adjustment2))
                        .withOneTimePayments(List.of(oneTimePayment1, oneTimePayment2))
                        .build();

        // when
        List<Saving> actual = sut.toSavingsList(response);

        // then
        assertThat(actual.get(0)).usingRecursiveComparison().isEqualTo(expectedSaving);
    }

    @Test
    void listContainsMultipleSavingsWhenInvoked() {
        // given
        QueryResponse response = QueryResponse.builder().items(baseItem, complexItem).build();

        Saving expectedBaseSaving =
                Saving.newBuilder()
                        .withName(name)
                        .withStartAmount(startAmount)
                        .withMonthlyAmount(monthlyAmount)
                        .withStartDate(startDate)
                        .withEndDate(endDate)
                        .withYearlyRate(yearlyRate)
                        .build();

        Saving expectedComplexSaving =
                Saving.newBuilder()
                        .withName(name)
                        .withStartAmount(startAmount)
                        .withMonthlyAmount(monthlyAmount)
                        .withStartDate(startDate)
                        .withEndDate(endDate)
                        .withYearlyRate(yearlyRate)
                        .withAdjustments(List.of(adjustment1, adjustment2))
                        .withOneTimePayments(List.of(oneTimePayment1, oneTimePayment2))
                        .build();

        // when
        List<Saving> actual = sut.toSavingsList(response);

        // then
        assertThat(actual).hasSize(2);
        assertThat(actual.get(0)).usingRecursiveComparison().isEqualTo(expectedBaseSaving);
        assertThat(actual.get(1)).usingRecursiveComparison().isEqualTo(expectedComplexSaving);
    }

    @Test
    void returnsSavingWhenInvoked() {
        // given
        Saving expectedSaving =
                Saving.newBuilder()
                        .withName(name)
                        .withStartAmount(startAmount)
                        .withMonthlyAmount(monthlyAmount)
                        .withStartDate(startDate)
                        .withEndDate(endDate)
                        .withYearlyRate(yearlyRate)
                        .build();

        // when
        Saving actual = sut.toSaving(baseItem);

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expectedSaving);
    }
}
