package com.vari.clockify.validator.domain;

import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository;

public interface TimeEntryRepository extends FirestoreReactiveRepository<TimeEntry> {
}
