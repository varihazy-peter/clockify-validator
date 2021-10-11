package com.vari.clockify.check.domain.repository;

import org.springframework.data.domain.Pageable;

import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository;
import com.vari.clockify.check.domain.document.TimeEntry;

import reactor.core.publisher.Flux;

public interface TimeEntryRepository extends FirestoreReactiveRepository<TimeEntry> {
    Flux<TimeEntry> findAllBy(Pageable pageable);

    Flux<TimeEntry> findByUserIdAndValidationDataDate(String userId, String date);

    Flux<TimeEntry> findByValidationDataValidatedAtIsNull(Pageable pageable);
}
