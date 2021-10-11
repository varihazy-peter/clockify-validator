package com.vari.clockify.check.domain.repository;

import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository;
import com.vari.clockify.check.domain.document.DaySummary;

import reactor.core.publisher.Flux;

public interface DaySummaryRepository extends FirestoreReactiveRepository<DaySummary> {
    Flux<DaySummary> findByUserIdAndDate(String userId, String date);
}
