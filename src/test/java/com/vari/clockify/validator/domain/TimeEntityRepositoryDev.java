package com.vari.clockify.validator.domain;

import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

import com.google.cloud.firestore.Firestore;

import reactor.core.publisher.Flux;

@SpringBootTest
@EnabledIfEnvironmentVariable(named = "ENV", matches = "dev")
class TimeEntityRepositoryDev {

    @Autowired
    Firestore firestore;
    @Autowired
    TimeEntryRepository timeEntryRepository;

    @Test
    void loadEmployeesWithResourceLoader() throws Exception {
        firestore.collection(TimeEntry.COLLECTION_NAME).listDocuments().forEach(null);
        Flux<TimeEntry> tes = this.timeEntryRepository.findByDateIsNull(PageRequest.of(0, 1));
        var r = tes.collect(Collectors.toUnmodifiableList()).block();
    }
}
