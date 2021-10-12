package com.vari.clockify.check.domain;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

import com.vari.clockify.check.domain.document.DaySummary;
import com.vari.clockify.check.domain.document.TimeEntry;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
public class UserIdDate {
    @NonNull
    String userId;
    @NonNull
    LocalDate date;

    public static UserIdDate from(TimeEntry timeEntry) {
        return new UserIdDate(timeEntry.getUserId(), timeEntry.getValidationData().date());
    }

    public static UserIdDate from(DaySummary daySummary) {
        return new UserIdDate(daySummary.getUserId(), daySummary.getDate());
    }

    public static UserIdDate daySummaryDate(TimeEntry timeEntry) {
        return timeEntry.getUserId() == null //
                || timeEntry.getValidationData() == null //
                || timeEntry.getValidationData().daySummaryDate() == null //
                        ? null
                        : new UserIdDate(timeEntry.getUserId(), timeEntry.getValidationData().daySummaryDate());
    }

    public static Set<UserIdDate> effected(TimeEntry timeEntry) {
        UserIdDate userIdDate = from(timeEntry);
        UserIdDate daySummaryDate = daySummaryDate(timeEntry);
        return daySummaryDate == null || Objects.equals(userIdDate, daySummaryDate) ? Set.of(userIdDate)
                : Set.of(userIdDate, daySummaryDate);
    }

    @Override
    public String toString() {
        return userId + "-" + this.dateToString();
    }

    public String dateToString() {
        return date.toString();
    }

    public UserIdDate(@NonNull String userId, @NonNull String date) {
        this(userId, LocalDate.parse(date));
    }
}
