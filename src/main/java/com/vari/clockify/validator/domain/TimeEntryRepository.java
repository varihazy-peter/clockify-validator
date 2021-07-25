package com.vari.clockify.validator.domain;

import org.springframework.data.domain.Pageable;

import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository;

import reactor.core.publisher.Flux;

public interface TimeEntryRepository extends FirestoreReactiveRepository<TimeEntry> {
    Flux<TimeEntry> findByDateIsNull(Pageable pageable);
}
