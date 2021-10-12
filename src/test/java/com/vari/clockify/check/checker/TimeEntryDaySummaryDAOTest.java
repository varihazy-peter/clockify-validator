package com.vari.clockify.check.checker;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.threeten.bp.Instant;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.MoreCollectors;
import com.google.common.io.Resources;
import com.vari.clockify.check.AbstractInterationTest;
import com.vari.clockify.check.domain.UserIdDate;
import com.vari.clockify.check.domain.document.DaySummary;
import com.vari.clockify.check.domain.document.TimeEntry;
import com.vari.clockify.check.domain.repository.DaySummaryRepository;
import com.vari.clockify.check.domain.repository.TimeEntryRepository;

class TimeEntryDaySummaryDAOTest extends AbstractInterationTest {

    @Autowired
    TimeEntryDaySummaryDAO dao;
    @Autowired
    TimeEntryRepository timeEntryRepository;
    @Autowired
    DaySummaryRepository daySummaryRepository;
    List<TimeEntry> timeEntries;

    @BeforeEach
    void loadTimeEntries(@Autowired ObjectMapper objectMapper) throws IOException {
        byte[] json = Resources.toByteArray(Resources.getResource("time-entries.json"));
        this.timeEntries = objectMapper.readerForListOf(TimeEntry.class).readValue(json);
    }

    @AfterEach
    private void setdown() {
        this.timeEntryRepository.deleteAll().and(this.daySummaryRepository.deleteAll()).block();
    }

    @Test
    void firstNotValidated() {
        assertThat(this.dao.saveTimeEntries(timeEntries)).isNotNull();
        TimeEntry timeEntry = dao.firstNotValidated().orElseThrow();
        assertThat(timeEntry).isNotNull();
        assertThat(timeEntry.getId()).isNotNull().isEqualTo("615ff07d7002f4130d9f71a2");
    }

    Set<String> timeEntryIds = Set.of( //
            "6160239d0908333f1c152a64",
            "61604a9b7002f4130da635ae",
            "61603aa490029368f0653d2b",
            "616024bc0908333f1c153b63",
            "615ff0ca0759435f6bfca2a3",
            "61603f901bdc8418c2ca8ed1",
            "615ff07d7002f4130d9f71a2",
            "615ff0b890029368f0605e15");
    LocalDate ld20211008 = LocalDate.of(2021, 10, 8);
    String ld20211008s = ld20211008.toString();
    UserIdDate userIdDate = new UserIdDate("5f9ed7381b69c27d1628360b", ld20211008);

    @Test
    void findTimeEntries() {
        assertThat(dao.findTimeEntries(userIdDate)).isEmpty();
        this.dao.saveTimeEntries(timeEntries);
        List<TimeEntry> timeEntries = dao.findTimeEntries(new UserIdDate("5f9ed7381b69c27d1628360b", ld20211008));
        assertThat(timeEntries).isNotNull().hasSize(8).allMatch(te -> te.getValidationData().date().equals(ld20211008));

        Set<String> ids = timeEntries.stream().map(TimeEntry::getId).collect(Collectors.toUnmodifiableSet());
        assertThat(ids).isNotNull().isEqualTo(timeEntryIds);
    }

    @Test
    void daySummary() {
        DaySummary daySummary = this.dao.saveDaySummary(new DaySummary("useId-" + ld20211008s,
                "useId",
                ld20211008s,
                "PT24H",
                Instant.ofEpochSecond(19931119l).toString(),
                List.of("te1", "te2"),
                List.of("te2")));
        assertThat(this.daySummaryRepository.count().block()).isEqualTo(1);
        DaySummary findAll = this.daySummaryRepository.findAll().toStream().collect(MoreCollectors.onlyElement());
        assertThat(findAll).isEqualTo(daySummary);
        this.dao.deleteDaySummary(UserIdDate.from(daySummary));
        assertThat(this.daySummaryRepository.count().block()).isEqualTo(0);
    }

}
