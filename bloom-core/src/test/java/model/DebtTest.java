package model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.shouldHaveThrown;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mock;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import model.Debt.Builder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

class DebtTest {

    Name name;
    Amount startAmount;
    Amount monthlyAmount;
    Date startDate;
    Rate yearlyRate;
    List<Adjustment> adjustments;
    List<OneTimePayment> oneTimePayments;

    @BeforeEach
    void beforeEach() {
        name = new Name("a Debt");
        startAmount = new Amount("289.02");
        monthlyAmount = new Amount("12.13");
        startDate = new Date("2015-02");
        yearlyRate = new Rate("3.00");
        adjustments =
                List.of(new Adjustment(mock(Amount.class), new Date("2016-06"), mock(Rate.class)));
        oneTimePayments = List.of(new OneTimePayment(mock(Amount.class), new Date("2016-03")));
    }

    @Test
    void returnsBuilderWhenNewBuilderInvoked() {
        // given
        Builder builder;

        // when
        builder = Debt.newBuilder();

        // then
        // no exception
    }

    @Test
    void builderAcceptsNameWhenInvoked() {
        // given
        Builder builder = Debt.newBuilder();

        // when
        builder.withName(name);

        // then
        // no exception
    }

    @Test
    void builderAcceptsStartAmountWhenInvoked() {
        // given
        Builder builder = Debt.newBuilder();

        // when
        builder.withStartAmount(startAmount);

        // then
        // no exception
    }

    @Test
    void builderAcceptsMonthlyAmountWhenInvoked() {
        // given
        Builder builder = Debt.newBuilder();

        // when
        builder.withMonthlyAmount(monthlyAmount);

        // then
        // no exception
    }

    @Test
    void builderAcceptsStartDateWhenInvoked() {
        // given
        Builder builder = Debt.newBuilder();

        // when
        builder.withStartDate(startDate);

        // then
        // no exception
    }

    @Test
    void builderAcceptsYearlyRateWhenInvoked() {
        // given
        Builder builder = Debt.newBuilder();

        // when
        builder.withYearlyRate(yearlyRate);

        // then
        // no exception
    }

    @Test
    void builderAcceptsAdjustmentsWhenInvoked() {
        // given
        Builder builder = Debt.newBuilder();

        // when
        builder.withAdjustments(adjustments);

        // then
        // no exception
    }

    @Test
    void builderAcceptsOneTimePaymentsWhenInvoked() {
        // given
        Builder builder = Debt.newBuilder();

        // when
        builder.withOneTimePayments(oneTimePayments);

        // then
        // no exception
    }

    @Test
    void builderConstructsCorrectDebtWhenBuilt() {
        // given
        Builder builder =
                Debt.newBuilder()
                        .withName(name)
                        .withStartAmount(startAmount)
                        .withMonthlyAmount(monthlyAmount)
                        .withStartDate(startDate)
                        .withYearlyRate(yearlyRate)
                        .withAdjustments(adjustments)
                        .withOneTimePayments(oneTimePayments);

        // when
        Debt actual = builder.build();

        // then
        assertThat(actual.getName()).isEqualTo(name);
        assertThat(actual.getStartAmount()).isEqualTo(startAmount);
        assertThat(actual.getMonthlyAmount()).isEqualTo(monthlyAmount);
        assertThat(actual.getStartDate()).isEqualTo(startDate);
        assertThat(actual.getYearlyRate()).isEqualTo(yearlyRate);
        assertThat(actual.getAdjustments()).isEqualTo(adjustments);
        assertThat(actual.getOneTimePayments()).isEqualTo(oneTimePayments);
    }

    @Test
    void adjustmentsIsAListWhenNotAddedToBuilder() {
        // given
        Builder builder =
                Debt.newBuilder()
                        .withName(name)
                        .withStartAmount(startAmount)
                        .withMonthlyAmount(monthlyAmount)
                        .withStartDate(startDate)
                        .withYearlyRate(yearlyRate);

        // when
        Debt actual = builder.build();

        // then
        assertThat(actual.getAdjustments()).hasSize(0);
    }

    @Test
    void oneTimePaymentsIsAListWhenNotAddedToBuilder() {
        // given
        Builder builder =
                Debt.newBuilder()
                        .withName(name)
                        .withStartAmount(startAmount)
                        .withMonthlyAmount(monthlyAmount)
                        .withStartDate(startDate)
                        .withYearlyRate(yearlyRate);

        // when
        Debt actual = builder.build();

        // then
        assertThat(actual.getOneTimePayments()).hasSize(0);
    }

    @ParameterizedTest
    @ValueSource(strings = {"-0.01", "-5", "-10.25"})
    void throwsExceptionWhenStartAmountInvalid(String amount) {
        startAmount = new Amount(amount);

        // given
        Builder builder =
                Debt.newBuilder()
                        .withName(name)
                        .withStartAmount(startAmount)
                        .withMonthlyAmount(monthlyAmount)
                        .withStartDate(startDate)
                        .withYearlyRate(yearlyRate);

        try {
            // when
            builder.build();
            shouldHaveThrown(IllegalArgumentException.class);

        } catch (IllegalArgumentException actual) {
            // then
            assertThat(actual).hasMessage("startAmount cannot be negative");
        }
    }

    @Test
    void noExceptionWhenStartAmountZero() {
        // given
        Builder builder =
                Debt.newBuilder()
                        .withName(name)
                        .withStartAmount(new Amount("0.00"))
                        .withMonthlyAmount(monthlyAmount)
                        .withStartDate(startDate)
                        .withYearlyRate(yearlyRate);

        // when
        builder.build();

        // then
        // no exception
    }

    @ParameterizedTest
    @MethodSource("builtExceptionProvider")
    void throwsExceptionWhenNullParametersProvided(Builder builder, String exceptionMessage) {
        // given

        try {
            // when
            builder.build();
            shouldHaveThrown(IllegalArgumentException.class);

        } catch (IllegalArgumentException actual) {
            // then
            assertThat(actual).hasMessage(exceptionMessage);
        }
    }

    static Stream<Arguments> builtExceptionProvider() {
        return Stream.of(
                arguments(
                        Debt.newBuilder()
                                .withName(null)
                                .withStartAmount(mock(Amount.class))
                                .withMonthlyAmount(mock(Amount.class))
                                .withStartDate(mock(Date.class))
                                .withYearlyRate(mock(Rate.class)),
                        "name cannot be null"),
                arguments(
                        Debt.newBuilder()
                                .withName(mock(Name.class))
                                .withStartAmount(null)
                                .withMonthlyAmount(mock(Amount.class))
                                .withStartDate(mock(Date.class))
                                .withYearlyRate(mock(Rate.class)),
                        "startAmount cannot be null"),
                arguments(
                        Debt.newBuilder()
                                .withName(mock(Name.class))
                                .withStartAmount(mock(Amount.class))
                                .withMonthlyAmount(null)
                                .withStartDate(mock(Date.class))
                                .withYearlyRate(mock(Rate.class)),
                        "monthlyAmount cannot be null"),
                arguments(
                        Debt.newBuilder()
                                .withName(mock(Name.class))
                                .withStartAmount(mock(Amount.class))
                                .withMonthlyAmount(mock(Amount.class))
                                .withStartDate(null)
                                .withYearlyRate(mock(Rate.class)),
                        "startDate cannot be null"),
                arguments(
                        Debt.newBuilder()
                                .withName(mock(Name.class))
                                .withStartAmount(mock(Amount.class))
                                .withMonthlyAmount(mock(Amount.class))
                                .withStartDate(mock(Date.class))
                                .withYearlyRate(null),
                        "yearlyRate cannot be null"));
    }

    @Test
    void adjustmentsCannotBeModifiedWhenConstructed() {
        // given
        adjustments = new ArrayList<>(adjustments);

        Builder builder =
                Debt.newBuilder()
                        .withName(name)
                        .withStartAmount(startAmount)
                        .withMonthlyAmount(monthlyAmount)
                        .withStartDate(startDate)
                        .withYearlyRate(yearlyRate)
                        .withAdjustments(adjustments);

        Debt actual = builder.build();

        Adjustment adjustment =
                new Adjustment(mock(Amount.class), mock(Date.class), mock(Rate.class));

        // when
        adjustments.add(adjustment);

        // then
        assertThat(actual.getAdjustments()).hasSize(1);
    }

    @Test
    void oneTimePaymentsCannotBeModifiedWhenConstructed() {
        // given
        oneTimePayments = new ArrayList<>(oneTimePayments);

        Builder builder =
                Debt.newBuilder()
                        .withName(name)
                        .withStartAmount(startAmount)
                        .withMonthlyAmount(monthlyAmount)
                        .withStartDate(startDate)
                        .withYearlyRate(yearlyRate)
                        .withOneTimePayments(oneTimePayments);

        Debt actual = builder.build();

        OneTimePayment oneTimePayment = new OneTimePayment(mock(Amount.class), mock(Date.class));

        // when
        oneTimePayments.add(oneTimePayment);

        // then
        assertThat(actual.getOneTimePayments()).hasSize(1);
    }

    @ParameterizedTest
    @MethodSource("adjustmentsDateProvider")
    void throwsExceptionWhenAdjustmentDateOutsideRange(List<Adjustment> adjustments) {
        // given
        startDate = new Date("2000-09");

        Builder builder =
                Debt.newBuilder()
                        .withName(name)
                        .withStartAmount(startAmount)
                        .withMonthlyAmount(monthlyAmount)
                        .withStartDate(startDate)
                        .withYearlyRate(yearlyRate)
                        .withAdjustments(adjustments);

        try {
            // when
            builder.build();
            shouldHaveThrown(IllegalArgumentException.class);

        } catch (IllegalArgumentException actual) {
            // then
            assertThat(actual)
                    .hasMessage("adjustment dates should be in range (startDate, 2050-12)");
        }
    }

    static Stream<List<Adjustment>> adjustmentsDateProvider() {
        return Stream.of(
                List.of(new Adjustment(mock(Amount.class), new Date("2000-08"), mock(Rate.class))),
                List.of(new Adjustment(mock(Amount.class), new Date("2000-09"), mock(Rate.class))),
                List.of(new Adjustment(mock(Amount.class), new Date("2050-12"), mock(Rate.class))),
                List.of(
                        new Adjustment(mock(Amount.class), new Date("2007-06"), mock(Rate.class)),
                        new Adjustment(mock(Amount.class), new Date("1999-09"), mock(Rate.class)),
                        new Adjustment(mock(Amount.class), new Date("2011-07"), mock(Rate.class))),
                List.of(
                        new Adjustment(mock(Amount.class), new Date("2007-06"), mock(Rate.class)),
                        new Adjustment(mock(Amount.class), new Date("2050-12"), mock(Rate.class)),
                        new Adjustment(mock(Amount.class), new Date("2003-09"), mock(Rate.class))));
    }

    @ParameterizedTest
    @MethodSource("oneTimePaymentDateProvider")
    void throwsExceptionWhenOneTimePaymentDateOutsideRange(List<OneTimePayment> oneTimePayments) {
        // given
        startDate = new Date("2000-09");

        Builder builder =
                Debt.newBuilder()
                        .withName(name)
                        .withStartAmount(startAmount)
                        .withMonthlyAmount(monthlyAmount)
                        .withStartDate(startDate)
                        .withYearlyRate(yearlyRate)
                        .withOneTimePayments(oneTimePayments);

        try {
            // when
            builder.build();
            shouldHaveThrown(IllegalArgumentException.class);

        } catch (IllegalArgumentException actual) {
            // then
            assertThat(actual)
                    .hasMessage("oneTimePayment dates should be in range (startDate, 2050-12)");
        }
    }

    static Stream<List<OneTimePayment>> oneTimePaymentDateProvider() {
        return Stream.of(
                List.of(new OneTimePayment(mock(Amount.class), new Date("2000-08"))),
                List.of(new OneTimePayment(mock(Amount.class), new Date("2000-09"))),
                List.of(new OneTimePayment(mock(Amount.class), new Date("2050-12"))),
                List.of(
                        new OneTimePayment(mock(Amount.class), new Date("2007-06")),
                        new OneTimePayment(mock(Amount.class), new Date("1999-09")),
                        new OneTimePayment(mock(Amount.class), new Date("2011-07"))),
                List.of(
                        new OneTimePayment(mock(Amount.class), new Date("2007-06")),
                        new OneTimePayment(mock(Amount.class), new Date("2050-12")),
                        new OneTimePayment(mock(Amount.class), new Date("2016-07"))));
    }

    @Test
    void noExceptionThrownWhenAdjustmentsEmpty() {
        // given
        List<Adjustment> adjustments = new ArrayList<>();

        Builder builder =
                Debt.newBuilder()
                        .withName(name)
                        .withStartAmount(startAmount)
                        .withMonthlyAmount(monthlyAmount)
                        .withStartDate(startDate)
                        .withYearlyRate(yearlyRate)
                        .withAdjustments(adjustments);

        // when
        builder.build();

        // then
        // no exception
    }

    @Test
    void noExceptionThrownWhenOneTimePaymentsEmpty() {
        // given
        List<OneTimePayment> oneTimePayments = new ArrayList<>();

        Builder builder =
                Debt.newBuilder()
                        .withName(name)
                        .withStartAmount(startAmount)
                        .withMonthlyAmount(monthlyAmount)
                        .withStartDate(startDate)
                        .withYearlyRate(yearlyRate)
                        .withOneTimePayments(oneTimePayments);

        // when
        builder.build();

        // then
        // no exception
    }

    @ParameterizedTest
    @MethodSource("debtRequestProvider")
    void debtDeserializesCorrectlyWhenInvoked(String fromJson, Debt expected)
            throws JsonProcessingException {
        // given
        ObjectMapper mapper = new ObjectMapper();

        // when
        Debt actual = mapper.readValue(fromJson, Debt.class);

        // then
        assertThat(actual.getName().getName()).isEqualTo(expected.getName().getName());
        assertThat(actual.getStartAmount().getAmount())
                .isEqualTo(expected.getStartAmount().getAmount());
        assertThat(actual.getMonthlyAmount().getAmount())
                .isEqualTo(expected.getMonthlyAmount().getAmount());
        assertThat(actual.getStartDate().getDate()).isEqualTo(expected.getStartDate().getDate());
        assertThat(actual.getYearlyRate().getRate()).isEqualTo(expected.getYearlyRate().getRate());
        assertThat(actual.getAdjustments())
                .usingRecursiveComparison()
                .isEqualTo(expected.getAdjustments());
        assertThat(actual.getOneTimePayments())
                .usingRecursiveComparison()
                .isEqualTo(expected.getOneTimePayments());
    }

    static Stream<Arguments> debtRequestProvider() {
        return Stream.of(
                arguments(
                        "{\"name\":\"debtName\",\"startAmount\":\"192.77\",\"monthlyAmount\":\"15.00\",\"startDate\":\"2013-05\",\"yearlyRate\":\"1.50\",\"adjustments\":[],\"oneTimePayments\":[]}",
                        Debt.newBuilder()
                                .withName(new Name("debtName"))
                                .withStartAmount(new Amount("192.77"))
                                .withMonthlyAmount(new Amount("15.00"))
                                .withStartDate(new Date("2013-05"))
                                .withYearlyRate(new Rate("1.50"))
                                .build()),
                arguments(
                        "{\"name\":\"debtName\",\"startAmount\":\"192.77\",\"monthlyAmount\":\"15.00\",\"startDate\":\"2013-05\",\"yearlyRate\":\"1.50\",\"adjustments\":[{\"amount\":\"20.00\",\"dateFrom\":\"2016-01\",\"rate\":\"1.75\"}],\"oneTimePayments\":[]}",
                        Debt.newBuilder()
                                .withName(new Name("debtName"))
                                .withStartAmount(new Amount("192.77"))
                                .withMonthlyAmount(new Amount("15.00"))
                                .withStartDate(new Date("2013-05"))
                                .withYearlyRate(new Rate("1.50"))
                                .withAdjustments(
                                        List.of(
                                                new Adjustment(
                                                        new Amount("20.00"),
                                                        new Date("2016-01"),
                                                        new Rate("1.75"))))
                                .build()),
                arguments(
                        "{\"name\":\"debtName\",\"startAmount\":\"192.77\",\"monthlyAmount\":\"15.00\",\"startDate\":\"2013-05\",\"yearlyRate\":\"1.50\",\"adjustments\":[],\"oneTimePayments\":[{\"amount\":\"25.00\",\"date\":\"2017-03\"}]}",
                        Debt.newBuilder()
                                .withName(new Name("debtName"))
                                .withStartAmount(new Amount("192.77"))
                                .withMonthlyAmount(new Amount("15.00"))
                                .withStartDate(new Date("2013-05"))
                                .withYearlyRate(new Rate("1.50"))
                                .withOneTimePayments(
                                        List.of(
                                                new OneTimePayment(
                                                        new Amount("25.00"), new Date("2017-03"))))
                                .build()),
                arguments(
                        "{\"name\":\"debtName\",\"startAmount\":\"192.77\",\"monthlyAmount\":\"15.00\",\"startDate\":\"2013-05\",\"yearlyRate\":\"1.50\",\"adjustments\":[{\"amount\":\"20.00\",\"dateFrom\":\"2015-01\",\"rate\":\"1.75\"},{\"amount\":\"27.50\",\"dateFrom\":\"2016-09\",\"rate\":\"2.00\"},{\"amount\":\"12.75\",\"dateFrom\":\"2019-03\",\"rate\":\"-0.75\"}],\"oneTimePayments\":[{\"amount\":\"100.00\",\"date\":\"2014-03\"},{\"amount\":\"35.57\",\"date\":\"2018-08\"},{\"amount\":\"95.28\",\"date\":\"2019-12\"}]}",
                        Debt.newBuilder()
                                .withName(new Name("debtName"))
                                .withStartAmount(new Amount("192.77"))
                                .withMonthlyAmount(new Amount("15.00"))
                                .withStartDate(new Date("2013-05"))
                                .withYearlyRate(new Rate("1.50"))
                                .withAdjustments(
                                        List.of(
                                                new Adjustment(
                                                        new Amount("20.00"),
                                                        new Date("2015-01"),
                                                        new Rate("1.75")),
                                                new Adjustment(
                                                        new Amount("27.50"),
                                                        new Date("2016-09"),
                                                        new Rate("2.00")),
                                                new Adjustment(
                                                        new Amount("12.75"),
                                                        new Date("2019-03"),
                                                        new Rate("-0.75"))))
                                .withOneTimePayments(
                                        List.of(
                                                new OneTimePayment(
                                                        new Amount("100.00"), new Date("2014-03")),
                                                new OneTimePayment(
                                                        new Amount("35.57"), new Date("2018-08")),
                                                new OneTimePayment(
                                                        new Amount("95.28"), new Date("2019-12"))))
                                .build()));
    }

    @ParameterizedTest
    @MethodSource("debtResponseProvider")
    void debtSerializesCorrectlyWhenInvoked(Builder builder, String expected)
            throws JsonProcessingException {
        // given
        ObjectMapper mapper = new ObjectMapper();

        // when
        Debt sut = builder.build();

        // then
        JsonNode tree1 = mapper.valueToTree(sut);
        JsonNode tree2 = mapper.readTree(expected);

        assertThat(tree1).isEqualTo(tree2);
    }

    static Stream<Arguments> debtResponseProvider() {
        return Stream.of(
                arguments(
                        Debt.newBuilder()
                                .withName(new Name("debtName"))
                                .withStartAmount(new Amount("192.77"))
                                .withMonthlyAmount(new Amount("15.00"))
                                .withStartDate(new Date("2013-05"))
                                .withYearlyRate(new Rate("1.50")),
                        "{\"name\":\"debtName\",\"startAmount\":\"192.77\",\"monthlyAmount\":\"15.00\",\"startDate\":\"2013-05\",\"yearlyRate\":\"1.50\",\"adjustments\":[],\"oneTimePayments\":[]}"),
                arguments(
                        Debt.newBuilder()
                                .withName(new Name("debtName"))
                                .withStartAmount(new Amount("192.77"))
                                .withMonthlyAmount(new Amount("15.00"))
                                .withStartDate(new Date("2013-05"))
                                .withYearlyRate(new Rate("1.50"))
                                .withAdjustments(
                                        List.of(
                                                new Adjustment(
                                                        new Amount("20.00"),
                                                        new Date("2016-01"),
                                                        new Rate("1.75")))),
                        "{\"name\":\"debtName\",\"startAmount\":\"192.77\",\"monthlyAmount\":\"15.00\",\"startDate\":\"2013-05\",\"yearlyRate\":\"1.50\",\"adjustments\":[{\"amount\":\"20.00\",\"dateFrom\":\"2016-01\",\"rate\":\"1.75\"}],\"oneTimePayments\":[]}"),
                arguments(
                        Debt.newBuilder()
                                .withName(new Name("debtName"))
                                .withStartAmount(new Amount("192.77"))
                                .withMonthlyAmount(new Amount("15.00"))
                                .withStartDate(new Date("2013-05"))
                                .withYearlyRate(new Rate("1.50"))
                                .withOneTimePayments(
                                        List.of(
                                                new OneTimePayment(
                                                        new Amount("25.00"), new Date("2017-03")))),
                        "{\"name\":\"debtName\",\"startAmount\":\"192.77\",\"monthlyAmount\":\"15.00\",\"startDate\":\"2013-05\",\"yearlyRate\":\"1.50\",\"adjustments\":[],\"oneTimePayments\":[{\"amount\":\"25.00\",\"date\":\"2017-03\"}]}"),
                arguments(
                        Debt.newBuilder()
                                .withName(new Name("debtName"))
                                .withStartAmount(new Amount("192.77"))
                                .withMonthlyAmount(new Amount("15.00"))
                                .withStartDate(new Date("2013-05"))
                                .withYearlyRate(new Rate("1.50"))
                                .withAdjustments(
                                        List.of(
                                                new Adjustment(
                                                        new Amount("20.00"),
                                                        new Date("2015-01"),
                                                        new Rate("1.75")),
                                                new Adjustment(
                                                        new Amount("27.50"),
                                                        new Date("2016-09"),
                                                        new Rate("2.00")),
                                                new Adjustment(
                                                        new Amount("12.75"),
                                                        new Date("2019-03"),
                                                        new Rate("-0.75"))))
                                .withOneTimePayments(
                                        List.of(
                                                new OneTimePayment(
                                                        new Amount("100.00"), new Date("2014-03")),
                                                new OneTimePayment(
                                                        new Amount("35.57"), new Date("2018-08")),
                                                new OneTimePayment(
                                                        new Amount("95.28"), new Date("2019-12")))),
                        "{\"name\":\"debtName\",\"startAmount\":\"192.77\",\"monthlyAmount\":\"15.00\",\"startDate\":\"2013-05\",\"yearlyRate\":\"1.50\",\"adjustments\":[{\"amount\":\"20.00\",\"dateFrom\":\"2015-01\",\"rate\":\"1.75\"},{\"amount\":\"27.50\",\"dateFrom\":\"2016-09\",\"rate\":\"2.00\"},{\"amount\":\"12.75\",\"dateFrom\":\"2019-03\",\"rate\":\"-0.75\"}],\"oneTimePayments\":[{\"amount\":\"100.00\",\"date\":\"2014-03\"},{\"amount\":\"35.57\",\"date\":\"2018-08\"},{\"amount\":\"95.28\",\"date\":\"2019-12\"}]}"));
    }
}
