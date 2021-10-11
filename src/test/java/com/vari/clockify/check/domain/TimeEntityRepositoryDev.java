package com.vari.clockify.check.domain;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.cloud.spring.data.firestore.FirestoreTemplate;
import com.vari.clockify.check.domain.UserIdDate;
import com.vari.clockify.check.domain.document.TimeEntry;
import com.vari.clockify.check.domain.repository.TimeEntryRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@EnabledIfEnvironmentVariable(named = "ENV", matches = "dev")
class TimeEntityRepositoryDev {

    @Autowired
    FirestoreTemplate firestoreTemplate;
    @Autowired
    TimeEntryRepository timeEntryRepository;

    String userId = "5f998e921f3eb40b669b8c12";
    LocalDate date = LocalDate.of(2021, 7, 5);
    UserIdDate userIdDate = new UserIdDate(userId, date);

    @Autowired
    Firestore firestore;

    @Test
    void set__validated() throws InterruptedException, ExecutionException {
        QuerySnapshot qs = this.firestore.collection(TimeEntry.COLLECTION_NAME).get().get();
        Stream<? extends DocumentSnapshot> stream = qs.getDocuments().stream();
        stream.map(this::restructure).collect(Collectors.toUnmodifiableList()).stream().forEach(this::get);
    }

    private Map.Entry<String, ApiFuture<WriteResult>> restructure(DocumentSnapshot ds) {
        Map<String, Object> data = ds.getData();
        TimeEntry timeEntry = ds.toObject(TimeEntry.class);
        Map<String, String> cal = this.cal(timeEntry);
        data.put("validationData", cal);
        data.put("__validatedAt", FieldValue.delete());
        data.put("__receivedAt", FieldValue.delete());
        return Map.entry(ds.getId(), ds.getReference().update(data));
    }

    private Map<String, String> cal(TimeEntry timeEntry) {
        String date = timeEntry.getTimeInterval().start().toLocalDate().toString();
        Map<String, String> map = new HashMap<>();
        map.put("receivedAt", Instant.now().toString());
        map.put("validatedAt", null);
        map.put("date", date);
        map.put("daySummaryDate", null);
        return map;
    }

    private void get(Map.Entry<String, ApiFuture<WriteResult>> e) {
        String id = e.getKey();
        try {
            WriteResult wr = e.getValue().get();
            log.info("{} updated at {}", id, wr.getUpdateTime());
        } catch (InterruptedException | ExecutionException x) {
            log.error("{} failed", id, x);
        }
    }

}
