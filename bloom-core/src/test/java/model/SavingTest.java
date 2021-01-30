package model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.shouldHaveThrown;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mock;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import model.Saving.Builder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

class SavingTest {

    Name name;
    Amount startAmount;
    Amount monthlyAmount;
    Date startDate;
    Date endDate;
    Rate yearlyRate;
    List<Adjustment> adjustments;
    List<OneTimePayment> oneTimePayments;

    @BeforeEach
    void beforeEach() {
        name = new Name("MySaving");
        startAmount = new Amount("206.78");
        monthlyAmount = new Amount("15.00");
        startDate = new Date("2012-03");
        endDate = new Date("2015-08");
        yearlyRate = new Rate("2");
        adjustments =
                List.of(new Adjustment(mock(Amount.class), new Date("2013-06"), mock(Rate.class)));
        oneTimePayments = List.of(new OneTimePayment(mock(Amount.class), new Date("2013-06")));
    }

    @Test
    void returnsBuilderWhenNewBuilderInvoked() {
        // given
        Builder builder;

        // when
        builder = Saving.newBuilder();

        // then
        // no exception
    }

    @Test
    void builderAcceptsNameWhenInvoked() {
        // given
        Builder builder = Saving.newBuilder();

        // when
        builder.withName(name);

        // then
        // no exception
    }

    @Test
    void builderAcceptsStartAmountWhenInvoked() {
        // given
        Builder builder = Saving.newBuilder();

        // when
        builder.withStartAmount(startAmount);

        // then
        // no exception
    }

    @Test
    void builderAcceptsMonthlyAmountWhenInvoked() {
        // given
        Builder builder = Saving.newBuilder();

        // when
        builder.withMonthlyAmount(monthlyAmount);

        // then
        // no exception
    }

    @Test
    void builderAcceptsStartDateWhenInvoked() {
        // given
        Builder builder = Saving.newBuilder();

        // when
        builder.withStartDate(startDate);

        // then
        // no exception
    }

    @Test
    void builderAcceptsEndDateWhenInvoked() {
        // given
        Builder builder = Saving.newBuilder();

        // when
        builder.withEndDate(endDate);

        // then
        // no exception
    }

    @Test
    void builderAcceptsYearlyRateWhenInvoked() {
        // given
        Builder builder = Saving.newBuilder();

        // when
        builder.withYearlyRate(yearlyRate);

        // then
        // no exception
    }

    @Test
    void builderAcceptsAdjustmentsWhenInvoked() {
        // given
        Builder builder = Saving.newBuilder();

        // when
        builder.withAdjustments(adjustments);

        // then
        // no exception
    }

    @Test
    void builderAcceptsOneTimePaymentsWhenInvoked() {
        // given
        Builder builder = Saving.newBuilder();

        // when
        builder.withOneTimePayments(oneTimePayments);

        // then
        // no exception
    }

    @Test
    void builderConstructsCorrectSavingWhenBuilt() {
        // given
        Builder builder =
                Saving.newBuilder()
                        .withName(name)
                        .withStartAmount(startAmount)
                        .withMonthlyAmount(monthlyAmount)
                        .withStartDate(startDate)
                        .withEndDate(endDate)
                        .withYearlyRate(yearlyRate)
                        .withAdjustments(adjustments)
                        .withOneTimePayments(oneTimePayments);

        // when
        Saving actual = builder.build();

        // then
        assertThat(actual.getName()).isEqualTo(name);
        assertThat(actual.getStartAmount()).isEqualTo(startAmount);
        assertThat(actual.getMonthlyAmount()).isEqualTo(monthlyAmount);
        assertThat(actual.getStartDate()).isEqualTo(startDate);
        assertThat(actual.getEndDate()).isEqualTo(endDate);
        assertThat(actual.getYearlyRate()).isEqualTo(yearlyRate);
        assertThat(actual.getAdjustments()).isEqualTo(adjustments);
        assertThat(actual.getOneTimePayments()).isEqualTo(oneTimePayments);
    }

    @Test
    void adjustmentsIsAListWhenNotAddedToBuilder() {
        // given
        Builder builder =
                Saving.newBuilder()
                        .withName(name)
                        .withStartAmount(startAmount)
                        .withMonthlyAmount(monthlyAmount)
                        .withStartDate(startDate)
                        .withEndDate(endDate)
                        .withYearlyRate(yearlyRate);

        // when
        Saving actual = builder.build();

        // then
        assertThat(actual.getAdjustments()).hasSize(0);
    }

    @Test
    void oneTimePaymentsIsAListWhenNotAddedToBuilder() {
        // given
        Builder builder =
                Saving.newBuilder()
                        .withName(name)
                        .withStartAmount(startAmount)
                        .withMonthlyAmount(monthlyAmount)
                        .withStartDate(startDate)
                        .withEndDate(endDate)
                        .withYearlyRate(yearlyRate);

        // when
        Saving actual = builder.build();

        // then
        assertThat(actual.getOneTimePayments()).hasSize(0);
    }

    @ParameterizedTest
    @ValueSource(strings = {"-0.01", "-5", "-10.25"})
    void throwsExceptionWhenStartAmountInvalid(String amount) {
        startAmount = new Amount(amount);

        // given
        Builder builder =
                Saving.newBuilder()
                        .withName(name)
                        .withStartAmount(startAmount)
                        .withMonthlyAmount(monthlyAmount)
                        .withStartDate(startDate)
                        .withEndDate(endDate)
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

    @ParameterizedTest
    @MethodSource("builtExceptionProvider")
    void throwsExceptionWhenNullNameProvided(Builder builder, String exceptionMessage) {
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
                        Saving.newBuilder()
                                .withName(null)
                                .withStartAmount(mock(Amount.class))
                                .withMonthlyAmount(mock(Amount.class))
                                .withStartDate(mock(Date.class))
                                .withEndDate(mock(Date.class))
                                .withYearlyRate(mock(Rate.class)),
                        "name cannot be null"),
                arguments(
                        Saving.newBuilder()
                                .withName(mock(Name.class))
                                .withStartAmount(null)
                                .withMonthlyAmount(mock(Amount.class))
                                .withStartDate(mock(Date.class))
                                .withEndDate(mock(Date.class))
                                .withYearlyRate(mock(Rate.class)),
                        "startAmount cannot be null"),
                arguments(
                        Saving.newBuilder()
                                .withName(mock(Name.class))
                                .withStartAmount(mock(Amount.class))
                                .withMonthlyAmount(null)
                                .withStartDate(mock(Date.class))
                                .withEndDate(mock(Date.class))
                                .withYearlyRate(mock(Rate.class)),
                        "monthlyAmount cannot be null"),
                arguments(
                        Saving.newBuilder()
                                .withName(mock(Name.class))
                                .withStartAmount(mock(Amount.class))
                                .withMonthlyAmount(mock(Amount.class))
                                .withStartDate(null)
                                .withEndDate(mock(Date.class))
                                .withYearlyRate(mock(Rate.class)),
                        "startDate cannot be null"),
                arguments(
                        Saving.newBuilder()
                                .withName(mock(Name.class))
                                .withStartAmount(mock(Amount.class))
                                .withMonthlyAmount(mock(Amount.class))
                                .withStartDate(mock(Date.class))
                                .withEndDate(null)
                                .withYearlyRate(mock(Rate.class)),
                        "endDate cannot be null"),
                arguments(
                        Saving.newBuilder()
                                .withName(mock(Name.class))
                                .withStartAmount(mock(Amount.class))
                                .withMonthlyAmount(mock(Amount.class))
                                .withStartDate(mock(Date.class))
                                .withEndDate(mock(Date.class))
                                .withYearlyRate(null),
                        "yearlyRate cannot be null"));
    }

    @Test
    void adjustmentsCannotBeModifiedWhenConstructed() {
        // given
        adjustments = new ArrayList<>(adjustments);

        Builder builder =
                Saving.newBuilder()
                        .withName(name)
                        .withStartAmount(startAmount)
                        .withMonthlyAmount(monthlyAmount)
                        .withStartDate(startDate)
                        .withEndDate(endDate)
                        .withYearlyRate(yearlyRate)
                        .withAdjustments(adjustments);

        Saving actual = builder.build();

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
                Saving.newBuilder()
                        .withName(name)
                        .withStartAmount(startAmount)
                        .withMonthlyAmount(monthlyAmount)
                        .withStartDate(startDate)
                        .withEndDate(endDate)
                        .withYearlyRate(yearlyRate)
                        .withOneTimePayments(oneTimePayments);

        Saving actual = builder.build();

        OneTimePayment oneTimePayment = new OneTimePayment(mock(Amount.class), mock(Date.class));

        // when
        oneTimePayments.add(oneTimePayment);

        // then
        assertThat(actual.getOneTimePayments()).hasSize(1);
    }

    @Test
    void throwsExceptionWhenStartDateAfterEndDate() {
        // given
        startDate = new Date("2012-09");
        endDate = new Date("2012-08");

        Builder builder =
                Saving.newBuilder()
                        .withName(name)
                        .withStartAmount(startAmount)
                        .withMonthlyAmount(monthlyAmount)
                        .withStartDate(startDate)
                        .withEndDate(endDate)
                        .withYearlyRate(yearlyRate);

        try {
            // when
            builder.build();
            shouldHaveThrown(IllegalArgumentException.class);

        } catch (IllegalArgumentException actual) {
            // then
            assertThat(actual).hasMessage("endDate cannot be before startDate");
        }
    }

    @ParameterizedTest
    @MethodSource("adjustmentsDateProvider")
    void throwsExceptionWhenAdjustmentDateOutsideRange(List<Adjustment> adjustments) {
        // given
        startDate = new Date("2000-09");
        endDate = new Date("2012-08");

        Builder builder =
                Saving.newBuilder()
                        .withName(name)
                        .withStartAmount(startAmount)
                        .withMonthlyAmount(monthlyAmount)
                        .withStartDate(startDate)
                        .withEndDate(endDate)
                        .withYearlyRate(yearlyRate)
                        .withAdjustments(adjustments);

        try {
            // when
            builder.build();
            shouldHaveThrown(IllegalArgumentException.class);

        } catch (IllegalArgumentException actual) {
            // then
            assertThat(actual)
                    .hasMessage("adjustment date should be in range (startDate, endDate)");
        }
    }

    static Stream<List<Adjustment>> adjustmentsDateProvider() {
        return Stream.of(
                List.of(new Adjustment(mock(Amount.class), new Date("2000-08"), mock(Rate.class))),
                List.of(new Adjustment(mock(Amount.class), new Date("2000-09"), mock(Rate.class))),
                List.of(new Adjustment(mock(Amount.class), new Date("2012-08"), mock(Rate.class))),
                List.of(new Adjustment(mock(Amount.class), new Date("2012-09"), mock(Rate.class))),
                List.of(
                        new Adjustment(mock(Amount.class), new Date("2007-06"), mock(Rate.class)),
                        new Adjustment(mock(Amount.class), new Date("1999-09"), mock(Rate.class)),
                        new Adjustment(mock(Amount.class), new Date("2011-07"), mock(Rate.class))),
                List.of(
                        new Adjustment(mock(Amount.class), new Date("2007-06"), mock(Rate.class)),
                        new Adjustment(mock(Amount.class), new Date("2003-09"), mock(Rate.class)),
                        new Adjustment(mock(Amount.class), new Date("2016-07"), mock(Rate.class))));
    }

    @ParameterizedTest
    @MethodSource("oneTimePaymentDateProvider")
    void throwsExceptionWhenOneTimePaymentDateOutsideRange(List<OneTimePayment> oneTimePayments) {
        // given
        startDate = new Date("2000-09");
        endDate = new Date("2012-08");

        Builder builder =
                Saving.newBuilder()
                        .withName(name)
                        .withStartAmount(startAmount)
                        .withMonthlyAmount(monthlyAmount)
                        .withStartDate(startDate)
                        .withEndDate(endDate)
                        .withYearlyRate(yearlyRate)
                        .withOneTimePayments(oneTimePayments);

        try {
            // when
            builder.build();
            shouldHaveThrown(IllegalArgumentException.class);

        } catch (IllegalArgumentException actual) {
            // then
            assertThat(actual)
                    .hasMessage("oneTimePayment date should be in range (startDate, endDate)");
        }
    }

    static Stream<List<OneTimePayment>> oneTimePaymentDateProvider() {
        return Stream.of(
                List.of(new OneTimePayment(mock(Amount.class), new Date("2000-08"))),
                List.of(new OneTimePayment(mock(Amount.class), new Date("2000-09"))),
                List.of(new OneTimePayment(mock(Amount.class), new Date("2012-08"))),
                List.of(new OneTimePayment(mock(Amount.class), new Date("2012-09"))),
                List.of(
                        new OneTimePayment(mock(Amount.class), new Date("2007-06")),
                        new OneTimePayment(mock(Amount.class), new Date("1999-09")),
                        new OneTimePayment(mock(Amount.class), new Date("2011-07"))),
                List.of(
                        new OneTimePayment(mock(Amount.class), new Date("2007-06")),
                        new OneTimePayment(mock(Amount.class), new Date("2003-09")),
                        new OneTimePayment(mock(Amount.class), new Date("2016-07"))));
    }

    @Test
    void noExceptionThrownWhenAdjustmentsEmpty() {
        // given
        List<Adjustment> adjustments = new ArrayList<>();

        Builder builder =
                Saving.newBuilder()
                        .withName(name)
                        .withStartAmount(startAmount)
                        .withMonthlyAmount(monthlyAmount)
                        .withStartDate(startDate)
                        .withEndDate(endDate)
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
                Saving.newBuilder()
                        .withName(name)
                        .withStartAmount(startAmount)
                        .withMonthlyAmount(monthlyAmount)
                        .withStartDate(startDate)
                        .withEndDate(endDate)
                        .withYearlyRate(yearlyRate)
                        .withOneTimePayments(oneTimePayments);

        // when
        builder.build();

        // then
        // no exception
    }

    @ParameterizedTest
    @MethodSource("savingRequestProvider")
    void savingDeserializesCorrectlyWhenInvoked(String fromJson, Saving expected)
            throws JsonProcessingException {
        // given
        ObjectMapper mapper = new ObjectMapper();

        // when
        Saving actual = mapper.readValue(fromJson, Saving.class);

        // then
        assertThat(actual.getName().getName()).isEqualTo(expected.getName().getName());
        assertThat(actual.getStartAmount().getAmount())
                .isEqualTo(expected.getStartAmount().getAmount());
        assertThat(actual.getMonthlyAmount().getAmount())
                .isEqualTo(expected.getMonthlyAmount().getAmount());
        assertThat(actual.getStartDate().getDate()).isEqualTo(expected.getStartDate().getDate());
        assertThat(actual.getEndDate().getDate()).isEqualTo(expected.getEndDate().getDate());
        assertThat(actual.getYearlyRate().getRate()).isEqualTo(expected.getYearlyRate().getRate());
        assertThat(actual.getAdjustments())
                .usingRecursiveComparison()
                .isEqualTo(expected.getAdjustments());
        assertThat(actual.getOneTimePayments())
                .usingRecursiveComparison()
                .isEqualTo(expected.getOneTimePayments());
    }

    static Stream<Arguments> savingRequestProvider() {
        return Stream.of(
                arguments(
                        "{\"name\":\"savingName\",\"startAmount\":\"192.77\",\"monthlyAmount\":\"15.00\",\"startDate\":\"2013-05\",\"endDate\":\"2020-11\",\"yearlyRate\":\"1.50\",\"adjustments\":[],\"oneTimePayments\":[]}",
                        Saving.newBuilder()
                                .withName(new Name("savingName"))
                                .withStartAmount(new Amount("192.77"))
                                .withMonthlyAmount(new Amount("15.00"))
                                .withStartDate(new Date("2013-05"))
                                .withEndDate(new Date("2020-11"))
                                .withYearlyRate(new Rate("1.50"))
                                .build()),
                arguments(
                        "{\"name\":\"savingName\",\"startAmount\":\"192.77\",\"monthlyAmount\":\"15.00\",\"startDate\":\"2013-05\",\"endDate\":\"2020-11\",\"yearlyRate\":\"1.50\",\"adjustments\":[{\"amount\":\"20.00\",\"dateFrom\":\"2016-01\",\"rate\":\"1.75\"}],\"oneTimePayments\":[]}",
                        Saving.newBuilder()
                                .withName(new Name("savingName"))
                                .withStartAmount(new Amount("192.77"))
                                .withMonthlyAmount(new Amount("15.00"))
                                .withStartDate(new Date("2013-05"))
                                .withEndDate(new Date("2020-11"))
                                .withYearlyRate(new Rate("1.50"))
                                .withAdjustments(
                                        List.of(
                                                new Adjustment(
                                                        new Amount("20.00"),
                                                        new Date("2016-01"),
                                                        new Rate("1.75"))))
                                .build()),
                arguments(
                        "{\"name\":\"savingName\",\"startAmount\":\"192.77\",\"monthlyAmount\":\"15.00\",\"startDate\":\"2013-05\",\"endDate\":\"2020-11\",\"yearlyRate\":\"1.50\",\"adjustments\":[],\"oneTimePayments\":[{\"amount\":\"25.00\",\"date\":\"2017-03\"}]}",
                        Saving.newBuilder()
                                .withName(new Name("savingName"))
                                .withStartAmount(new Amount("192.77"))
                                .withMonthlyAmount(new Amount("15.00"))
                                .withStartDate(new Date("2013-05"))
                                .withEndDate(new Date("2020-11"))
                                .withYearlyRate(new Rate("1.50"))
                                .withOneTimePayments(
                                        List.of(
                                                new OneTimePayment(
                                                        new Amount("25.00"), new Date("2017-03"))))
                                .build()),
                arguments(
                        "{\"name\":\"savingName\",\"startAmount\":\"192.77\",\"monthlyAmount\":\"15.00\",\"startDate\":\"2013-05\",\"endDate\":\"2020-11\",\"yearlyRate\":\"1.50\",\"adjustments\":[{\"amount\":\"20.00\",\"dateFrom\":\"2015-01\",\"rate\":\"1.75\"},{\"amount\":\"27.50\",\"dateFrom\":\"2016-09\",\"rate\":\"2.00\"},{\"amount\":\"12.75\",\"dateFrom\":\"2019-03\",\"rate\":\"-0.75\"}],\"oneTimePayments\":[{\"amount\":\"100.00\",\"date\":\"2014-03\"},{\"amount\":\"35.57\",\"date\":\"2018-08\"},{\"amount\":\"95.28\",\"date\":\"2019-12\"}]}",
                        Saving.newBuilder()
                                .withName(new Name("savingName"))
                                .withStartAmount(new Amount("192.77"))
                                .withMonthlyAmount(new Amount("15.00"))
                                .withStartDate(new Date("2013-05"))
                                .withEndDate(new Date("2020-11"))
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
    @MethodSource("savingResponseProvider")
    void savingSerializesCorrectlyWhenInvoked(Builder builder, String expected)
            throws JsonProcessingException {
        // given
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        // when
        Saving sut = builder.build();

        // then
        JsonNode tree1 = mapper.valueToTree(sut);
        JsonNode tree2 = mapper.readTree(expected);

        assertThat(tree1).isEqualTo(tree2);
    }

    static Stream<Arguments> savingResponseProvider() {
        return Stream.of(
                arguments(
                        Saving.newBuilder()
                                .withName(new Name("savingName"))
                                .withStartAmount(new Amount("192.77"))
                                .withMonthlyAmount(new Amount("15.00"))
                                .withStartDate(new Date("2013-05"))
                                .withEndDate(new Date("2020-11"))
                                .withYearlyRate(new Rate("1.50")),
                        "{\"name\":\"savingName\",\"startAmount\":\"192.77\",\"monthlyAmount\":\"15.00\",\"startDate\":\"2013-05\",\"endDate\":\"2020-11\",\"yearlyRate\":\"1.50\",\"adjustments\":[],\"oneTimePayments\":[]}"),
                arguments(
                        Saving.newBuilder()
                                .withName(new Name("savingName"))
                                .withStartAmount(new Amount("192.77"))
                                .withMonthlyAmount(new Amount("15.00"))
                                .withStartDate(new Date("2013-05"))
                                .withEndDate(new Date("2020-11"))
                                .withYearlyRate(new Rate("1.50"))
                                .withAdjustments(
                                        List.of(
                                                new Adjustment(
                                                        new Amount("20.00"),
                                                        new Date("2016-01"),
                                                        new Rate("1.75")))),
                        "{\"name\":\"savingName\",\"startAmount\":\"192.77\",\"monthlyAmount\":\"15.00\",\"startDate\":\"2013-05\",\"endDate\":\"2020-11\",\"yearlyRate\":\"1.50\",\"adjustments\":[{\"amount\":\"20.00\",\"dateFrom\":\"2016-01\",\"rate\":\"1.75\"}],\"oneTimePayments\":[]}"),
                arguments(
                        Saving.newBuilder()
                                .withName(new Name("savingName"))
                                .withStartAmount(new Amount("192.77"))
                                .withMonthlyAmount(new Amount("15.00"))
                                .withStartDate(new Date("2013-05"))
                                .withEndDate(new Date("2020-11"))
                                .withYearlyRate(new Rate("1.50"))
                                .withOneTimePayments(
                                        List.of(
                                                new OneTimePayment(
                                                        new Amount("25.00"), new Date("2017-03")))),
                        "{\"name\":\"savingName\",\"startAmount\":\"192.77\",\"monthlyAmount\":\"15.00\",\"startDate\":\"2013-05\",\"endDate\":\"2020-11\",\"yearlyRate\":\"1.50\",\"adjustments\":[],\"oneTimePayments\":[{\"amount\":\"25.00\",\"date\":\"2017-03\"}]}"),
                arguments(
                        Saving.newBuilder()
                                .withName(new Name("savingName"))
                                .withStartAmount(new Amount("192.77"))
                                .withMonthlyAmount(new Amount("15.00"))
                                .withStartDate(new Date("2013-05"))
                                .withEndDate(new Date("2020-11"))
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
                        "{\"name\":\"savingName\",\"startAmount\":\"192.77\",\"monthlyAmount\":\"15.00\",\"startDate\":\"2013-05\",\"endDate\":\"2020-11\",\"yearlyRate\":\"1.50\",\"adjustments\":[{\"amount\":\"20.00\",\"dateFrom\":\"2015-01\",\"rate\":\"1.75\"},{\"amount\":\"27.50\",\"dateFrom\":\"2016-09\",\"rate\":\"2.00\"},{\"amount\":\"12.75\",\"dateFrom\":\"2019-03\",\"rate\":\"-0.75\"}],\"oneTimePayments\":[{\"amount\":\"100.00\",\"date\":\"2014-03\"},{\"amount\":\"35.57\",\"date\":\"2018-08\"},{\"amount\":\"95.28\",\"date\":\"2019-12\"}]}"));
    }
}
