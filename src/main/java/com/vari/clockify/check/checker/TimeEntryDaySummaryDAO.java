package com.vari.clockify.check.checker;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;

import com.vari.clockify.check.domain.UserIdDate;
import com.vari.clockify.check.domain.document.DaySummary;
import com.vari.clockify.check.domain.document.TimeEntry;
import com.vari.clockify.check.domain.repository.DaySummaryRepository;
import com.vari.clockify.check.domain.repository.TimeEntryRepository;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Component
class TimeEntryDaySummaryDAO {
    @NonNull
    TimeEntryRepository timeEntryRepository;
    @NonNull
    DaySummaryRepository daySummaryRepository;

    Pageable pageable = PageRequest.of(0, 1, Direction.ASC, "id");

    Optional<TimeEntry> firstNotValidated() {
        log.debug("firstNotValidatedUserIdDates");
        List<TimeEntry> timeEntries = this.timeEntryRepository.findByValidationDataValidatedAtIsNull(pageable)
                .collectList()
                .block();
        if (timeEntries == null || timeEntries.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(timeEntries.get(0));
    }

    List<TimeEntry> findTimeEntries(@NonNull UserIdDate userIdDate) {
        log.debug("findTimeEntries by {}", userIdDate);
        return this.timeEntryRepository
                .findByUserIdAndValidationDataDate(userIdDate.getUserId(), userIdDate.dateToString())
                .collectList()
                .block();
    }

    boolean deleteDaySummary(@NonNull UserIdDate userIdDate) {
        log.info("deleteDaySummary by {}", userIdDate);
        this.daySummaryRepository.deleteById(userIdDate.toString());
        return true;
    }

    DaySummary saveDaySummary(@NonNull DaySummary daySummary) {
        log.info("saveDaySummary {}", daySummary);
        return daySummaryRepository.save(daySummary).block();
    }

    List<TimeEntry> saveTimeEntries(@NonNull List<TimeEntry> timeEntries) {
        if (timeEntries.isEmpty()) {
            return timeEntries;
        }
        log.info("saveTimeEntries ({})", timeEntries.size());
        if (log.isInfoEnabled()) {
            timeEntries.forEach(te -> log.info("saveTimeEntry ({})", te));
        }
        return timeEntryRepository.saveAll(timeEntries).collectList().block();
    }
}
