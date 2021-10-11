package com.vari.clockify.check.checker;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.ValidatorFactory;

import org.springframework.stereotype.Component;

import com.vari.clockify.check.domain.UserIdDate;
import com.vari.clockify.check.domain.document.TimeEntry;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Component
class TimeEntryJudge {
    ValidatorFactory validatorFactory;

    public List<TimeEntry> process(UserIdDate userIdDate, Instant validatedAt, List<TimeEntry> timeEntries) {
        return timeEntries.stream()
                .map(te -> this.validate(userIdDate, validatedAt, te))
                .collect(Collectors.toUnmodifiableList());
    }

    private TimeEntry validate(UserIdDate userIdDate, Instant validatedAt, TimeEntry timeEntry) {
        log.debug("for {}-{}; start validate {}", userIdDate, validatedAt, timeEntry);
        Set<ConstraintViolation<TimeEntry>> constraintViolations = validatorFactory.getValidator().validate(timeEntry);
        List<String> cvms = constraintViolations.stream()
                .map(ConstraintViolation::getMessage)
                .distinct()
                .sorted()
                .collect(Collectors.toUnmodifiableList());
        List<String> constraintViolationMessages = (cvms == null || cvms.isEmpty()) ? null : cvms;
        timeEntry.getValidationData()
                .setValidatedAt(validatedAt.toString())
                .setConstraintViolationMessages(constraintViolationMessages)
                .setDaySummaryDate(userIdDate.dateToString());
        return timeEntry;
    }
}
