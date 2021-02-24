package transform;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import model.Adjustment;
import model.Amount;
import model.Date;
import model.Debt;
import model.Name;
import model.OneTimePayment;
import model.Rate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

class DebtsDynamoTransformerTest {

    AdjustmentsTransformer adjustmentsTransformer;
    OneTimePaymentsTransformer oneTimePaymentsTransformer;
    DebtsDynamoTransformer sut;

    Name name;
    Amount startAmount;
    Amount monthlyAmount;
    Date startDate;
    Rate yearlyRate;
    Adjustment adjustment1;
    Adjustment adjustment2;
    OneTimePayment oneTimePayment1;
    OneTimePayment oneTimePayment2;

    @BeforeEach
    void beforeEach() {
        adjustmentsTransformer = new AdjustmentsTransformer();
        oneTimePaymentsTransformer = new OneTimePaymentsTransformer();
        sut = new DebtsDynamoTransformer(adjustmentsTransformer, oneTimePaymentsTransformer);

        name = new Name("this is debt");
        startAmount = new Amount("206.78");
        monthlyAmount = new Amount("15.00");
        startDate = new Date("2012-03");
        yearlyRate = new Rate("2");
        adjustment1 = new Adjustment(new Amount("12.66"), new Date("2015-11"), new Rate("1.25"));
        adjustment2 = new Adjustment(new Amount("14.26"), new Date("2018-09"), new Rate("1.75"));
        oneTimePayment1 = new OneTimePayment(new Amount("12.66"), new Date("2015-11"));
        oneTimePayment2 = new OneTimePayment(new Amount("14.26"), new Date("2018-09"));
    }

    @Test
    void acceptsQueryRequestWhenInvoked() {
        // given
        QueryResponse response = QueryResponse.builder().build();

        // when
        sut.toDebtsList(response);

        // then
        // no exception
    }

    @Test
    void returnsDebtsListWhenInvoked() {
        // given
        QueryResponse response = QueryResponse.builder().build();

        // when
        List<Debt> actual = sut.toDebtsList(response);

        // then
        assertThat(actual).hasSize(0);
    }

    @Test
    void listContainsProvidedDebtWhenInvoked() {
        // given
        Map<String, AttributeValue> debt = basicDebt();
        QueryResponse response = QueryResponse.builder().items(debt).build();

        Debt expected =
                Debt.newBuilder()
                        .withName(name)
                        .withStartAmount(startAmount)
                        .withMonthlyAmount(monthlyAmount)
                        .withStartDate(startDate)
                        .withYearlyRate(yearlyRate)
                        .build();

        // when
        List<Debt> actual = sut.toDebtsList(response);

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(List.of(expected));
    }

    @Test
    void listContainsMultipleDebtsWhenInvoked() {
        // given
        Map<String, AttributeValue> debt = basicDebt();
        QueryResponse response = QueryResponse.builder().items(debt, debt).build();

        Debt expected =
                Debt.newBuilder()
                        .withName(name)
                        .withStartAmount(startAmount)
                        .withMonthlyAmount(monthlyAmount)
                        .withStartDate(startDate)
                        .withYearlyRate(yearlyRate)
                        .build();

        // when
        List<Debt> actual = sut.toDebtsList(response);

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(List.of(expected, expected));
    }

    @Test
    void listContainsDebtWithAdjustmentsWhenInvoked() {
        // given
        Map<String, AttributeValue> debt = debtWithAdjustments();
        QueryResponse response = QueryResponse.builder().items(debt).build();
        List<Adjustment> adjustments = List.of(adjustment1, adjustment2);

        Debt expected =
                Debt.newBuilder()
                        .withName(name)
                        .withStartAmount(startAmount)
                        .withMonthlyAmount(monthlyAmount)
                        .withStartDate(startDate)
                        .withYearlyRate(yearlyRate)
                        .withAdjustments(adjustments)
                        .build();

        // when
        List<Debt> actual = sut.toDebtsList(response);

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(List.of(expected));
    }

    @Test
    void listContainsDebtWithOneTimePaymentsWhenInvoked() {
        // given
        Map<String, AttributeValue> debt = debtWithOneTimePayments();
        QueryResponse response = QueryResponse.builder().items(debt).build();
        List<OneTimePayment> oneTimePayments = List.of(oneTimePayment1, oneTimePayment2);

        Debt expected =
                Debt.newBuilder()
                        .withName(name)
                        .withStartAmount(startAmount)
                        .withMonthlyAmount(monthlyAmount)
                        .withStartDate(startDate)
                        .withYearlyRate(yearlyRate)
                        .withOneTimePayments(oneTimePayments)
                        .build();

        // when
        List<Debt> actual = sut.toDebtsList(response);

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(List.of(expected));
    }

    Map<String, AttributeValue> basicDebt() {
        return Map.of(
                "SK",
                AttributeValue.builder().s("DEBT#" + name.getName()).build(),
                "StartAmount",
                AttributeValue.builder().s(startAmount.toString()).build(),
                "MonthlyAmount",
                AttributeValue.builder().s(monthlyAmount.toString()).build(),
                "StartDate",
                AttributeValue.builder().s(startDate.toString()).build(),
                "YearlyRate",
                AttributeValue.builder().s(yearlyRate.toString()).build(),
                "Adjustments",
                AttributeValue.builder().l(List.of()).build(),
                "OneTimePayments",
                AttributeValue.builder().l(List.of()).build());
    }

    Map<String, AttributeValue> debtWithAdjustments() {
        return Map.of(
                "SK",
                AttributeValue.builder().s("DEBT#" + name.getName()).build(),
                "StartAmount",
                AttributeValue.builder().s(startAmount.toString()).build(),
                "MonthlyAmount",
                AttributeValue.builder().s(monthlyAmount.toString()).build(),
                "StartDate",
                AttributeValue.builder().s(startDate.toString()).build(),
                "YearlyRate",
                AttributeValue.builder().s(yearlyRate.toString()).build(),
                "Adjustments",
                AttributeValue.builder()
                        .l(List.of(fromAdjustment(adjustment1), fromAdjustment(adjustment2)))
                        .build(),
                "OneTimePayments",
                AttributeValue.builder().l(List.of()).build());
    }

    Map<String, AttributeValue> debtWithOneTimePayments() {
        return Map.of(
                "SK",
                AttributeValue.builder().s("DEBT#" + name.getName()).build(),
                "StartAmount",
                AttributeValue.builder().s(startAmount.toString()).build(),
                "MonthlyAmount",
                AttributeValue.builder().s(monthlyAmount.toString()).build(),
                "StartDate",
                AttributeValue.builder().s(startDate.toString()).build(),
                "YearlyRate",
                AttributeValue.builder().s(yearlyRate.toString()).build(),
                "Adjustments",
                AttributeValue.builder().l(List.of()).build(),
                "OneTimePayments",
                AttributeValue.builder()
                        .l(
                                List.of(
                                        fromOneTimePayment(oneTimePayment1),
                                        fromOneTimePayment(oneTimePayment2)))
                        .build());
    }

    static AttributeValue fromAdjustment(Adjustment adjustment) {
        return AttributeValue.builder()
                .m(
                        Map.of(
                                "Amount",
                                AttributeValue.builder()
                                        .s(adjustment.getAmount().toString())
                                        .build(),
                                "DateFrom",
                                AttributeValue.builder()
                                        .s(adjustment.getDateFrom().toString())
                                        .build(),
                                "Rate",
                                AttributeValue.builder()
                                        .s(adjustment.getRate().toString())
                                        .build()))
                .build();
    }

    static AttributeValue fromOneTimePayment(OneTimePayment oneTimePayment) {
        return AttributeValue.builder()
                .m(
                        Map.of(
                                "Amount",
                                AttributeValue.builder()
                                        .s(oneTimePayment.getAmount().toString())
                                        .build(),
                                "Date",
                                AttributeValue.builder()
                                        .s(oneTimePayment.getDate().toString())
                                        .build()))
                .build();
    }
}
