package model;

import static java.lang.Integer.parseInt;
import static java.time.YearMonth.parse;
import static util.StringValidator.checkNullOrEmpty;

import com.fasterxml.jackson.annotation.JsonValue;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;

public class Date {

    private final YearMonth date;

    public Date(String date) {
        checkNullOrEmpty(date, "date");
        this.date = parseDate(date);
    }

    private static YearMonth parseDate(String date) {
        try {
            int year = parseInt(date.split("-")[0]);
            int month = parseInt(date.split("-")[1]);
            checkDateValidity(year, month);

            return parse(date);
        } catch (DateTimeParseException | NumberFormatException exception) {
            throw new IllegalArgumentException("date should be formatted like yyyy-MM");
        }
    }

    private static void checkDateValidity(int year, int month) {
        if (!(1969 < year && 2051 > year)) {
            throw new IllegalArgumentException("year should be in range [1970, 2050]");
        } else if (!(0 < month && 13 > month)) {
            throw new IllegalArgumentException("month should be in range [1, 12]");
        }
    }

    public YearMonth getDate() {
        return date;
    }

    @JsonValue
    @Override
    public String toString() {
        return date.toString();
    }
}
