package com.vari.clockify.check.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.vari.clockify.check.AbstractInterationTest;
import com.vari.clockify.check.domain.document.TimeEntry;
import com.vari.clockify.check.domain.repository.TimeEntryRepository;

import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
class TimeEntryRepositoryTest extends AbstractInterationTest {
    @Autowired
    Firestore firestore;

    @Autowired
    TimeEntryRepository timeEntryRepository;

    @Autowired
    ObjectMapper objectMapper;

    String documentId = "testId";

    @Value("classpath:firestore/time_entry.json")
    Resource timeEntryJson;
    MapType type = TypeFactory.defaultInstance().constructMapType(HashMap.class, String.class, Object.class);

    Map<String, Object> map() {
        return Try.withResources(timeEntryJson::getInputStream)
                .of(objectMapper.readerFor(type)::<Map<String, Object>>readValue)
                .get();
    }

    @Test
    void loadEmployeesWithResourceLoader() throws Exception {
        Map<String, Object> map = this.map();
        firestore.collection(TimeEntry.COLLECTION_NAME).document(documentId).delete().get();
        firestore.collection(TimeEntry.COLLECTION_NAME).document(documentId).create(map).get();
        DocumentSnapshot g = firestore.collection(TimeEntry.COLLECTION_NAME).document(documentId).get().get();
        Map<String, Object> data = g.getData();
        Assertions.assertEquals(map, data);
        TimeEntry timeEntry = timeEntryRepository.findById(documentId).block();
        Assertions.assertEquals(documentId, timeEntry.getId());
        Assertions.assertEquals(documentId, timeEntry.getDocumentId());
        Assertions.assertEquals("description", timeEntry.getDescription());
        Assertions.assertEquals("projectId", timeEntry.getProjectId());
        Assertions.assertEquals("userId", timeEntry.getUserId());
        Assertions.assertEquals("workspaceId", timeEntry.getWorkspaceId());
        Assertions.assertEquals(true, timeEntry.getBillable());
        Assertions.assertEquals(true, timeEntry.getIsLocked());
        Assertions.assertEquals(Duration.parse("PT23M31S"), timeEntry.getTimeInterval().duration());
        Assertions.assertEquals(OffsetDateTime.parse("2021-06-30T22:07:36Z"), timeEntry.getTimeInterval().start());
        Assertions.assertEquals(OffsetDateTime.parse("2021-06-30T22:31:07Z"), timeEntry.getTimeInterval().end());
        assertThat(timeEntry.getTags()).contains( //
                new TimeEntry.Tag("5fb7b5d6086bdf5591cc3494", "Support", "workspaceId", false), //
                new TimeEntry.Tag("60a7942a7452030cc0544f0e", "Development Task", "workspaceId", false));
    }

}
