package com.vari.clockify.check.checker;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.vari.clockify.check.domain.UserIdDate;
import com.vari.clockify.check.domain.document.DaySummary;
import com.vari.clockify.check.domain.document.TimeEntry;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
class DaySummarizer {

    public Optional<DaySummary> process(UserIdDate userIdDate, Instant validatedAt, List<TimeEntry> timeEntries) {
        if (timeEntries == null || timeEntries.isEmpty()) {
            return Optional.empty();
        }
        Duration duration = timeEntries.stream()
                .map(this::durationOf)
                .filter(Objects::nonNull)
                .reduce(Duration.ZERO, Duration::plus);
        return Optional.of(this.daySummary(userIdDate, validatedAt, timeEntries, duration));
    }

    private Duration durationOf(TimeEntry timeEntry) {
        TimeEntry.TimeInterval timeInterval = timeEntry.getTimeInterval();
        if (timeInterval == null) {
            log.warn("TimeEntry without timeInterval {}", timeEntry);
            return null;
        }
        Duration duration = timeInterval.duration();
        if (duration == null) {
            log.warn("TimeEntry without timeInterval.duration {}", timeEntry);
            return null;
        }
        return duration;
    }

    private DaySummary daySummary(UserIdDate userIdDate,
            Instant validatedAt,
            List<TimeEntry> timeEntries,
            Duration duration) {
        List<String> calculatedTimeEntryIds = timeEntries.stream()
                .map(TimeEntry::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toUnmodifiableList());
        List<String> failedTimeEntryIds = timeEntries.stream()
                .filter(this::isFailed)
                .map(TimeEntry::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toUnmodifiableList());

        return new DaySummary(userIdDate.toString(),
                userIdDate.getUserId(),
                userIdDate.dateToString(),
                duration.toString(),
                validatedAt.toString(),
                calculatedTimeEntryIds,
                failedTimeEntryIds);
    }

    private boolean isFailed(TimeEntry timeEntry) {
        return timeEntry == null //
                || timeEntry.getValidationData() == null //
                || (timeEntry.getValidationData().getConstraintViolationMessages() != null
                        && !timeEntry.getValidationData().getConstraintViolationMessages().isEmpty());
    }

}
