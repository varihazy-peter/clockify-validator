package com.vari.clockify.check.checker;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.vari.clockify.check.domain.UserIdDate;
import com.vari.clockify.check.domain.document.DaySummary;
import com.vari.clockify.check.domain.document.TimeEntry;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Component
public class UserIdDateEvaluator {
    TimeEntryDaySummaryDAO dao;
    TimeEntryJudge timeEntryJudge;
    DaySummarizer daySummarizer;

    synchronized public UserIdDateEvaluation check(UserIdDate userIdDate) {
        Instant validatedAt = Instant.now();

        log.info("for {}-{}; start validate at ", userIdDate, validatedAt);
        List<TimeEntry> timeEntries = this.dao.findTimeEntries(userIdDate);
        if (timeEntries == null || timeEntries.isEmpty()) {
            log.info("for {}-{}; No timeEntries found", userIdDate, validatedAt);
            this.dao.deleteDaySummary(userIdDate);
            return new UserIdDateEvaluation(userIdDate, validatedAt, null, Set.of(), Map.of());
        }

        log.info("for {}-{}; time entry validation started", userIdDate, validatedAt);
        List<TimeEntry> validatedTimeEntries = this.timeEntryJudge.process(userIdDate, validatedAt, timeEntries);
        log.info("for {}-{}; time entry validation done", userIdDate, validatedAt);
        DaySummary daySummary = this.daySummarizer.process(userIdDate, validatedAt, validatedTimeEntries)
                .map(this.dao::saveDaySummary)
                .orElse(null);
        List<TimeEntry> savedTimeEntries = this.dao.saveTimeEntries(validatedTimeEntries);
        return this.result(userIdDate, validatedAt, timeEntries, daySummary, savedTimeEntries);
    }

    private UserIdDateEvaluation result(UserIdDate userIdDate,
            Instant validatedAt,
            List<TimeEntry> timeEntries,
            DaySummary daySummary,
            List<TimeEntry> savedTimeEntries) {
        Set<String> timeEntryIds = timeEntries.stream().map(TimeEntry::getId).collect(Collectors.toUnmodifiableSet());
        Map<String, List<String>> violations = savedTimeEntries //
                .stream()
                .filter(te -> te.getValidationData().getConstraintViolationMessages() != null
                        && !te.getValidationData().getConstraintViolationMessages().isEmpty())
                .collect(Collectors.toUnmodifiableMap(TimeEntry::getId,
                        te -> te.getValidationData().getConstraintViolationMessages()));
        String duration = daySummary == null ? null : daySummary.getDuration();
        return new UserIdDateEvaluation(userIdDate, validatedAt, duration, timeEntryIds, violations);
    }

}
