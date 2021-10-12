package com.vari.clockify.check.checker;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import com.vari.clockify.check.AbstractInterationTest;
import com.vari.clockify.check.domain.document.TimeEntry;
import com.vari.clockify.check.domain.repository.TimeEntryRepository;

class TimeEntryDaySummaryDAOTest extends AbstractInterationTest {

    @Autowired
    TimeEntryDaySummaryDAO dao;
    @Autowired
    TimeEntryRepository timeEntryRepository;

    @BeforeEach
    void loadTimeEntries(@Autowired ObjectMapper objectMapper) throws IOException {
        byte[] json = Resources.toByteArray(Resources.getResource("time-entries.json"));
        List<TimeEntry> timeEntries = objectMapper.readerForListOf(TimeEntry.class).readValue(json);
        this.timeEntryRepository.saveAll(timeEntries);
    }

    @Test
    void test() {
        dao.firstNotValidated();
    }

}
