package com.vari.clockify.check.checker;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.stereotype.Service;

import com.vari.clockify.check.domain.UserIdDate;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class ClockifyChecker {
    TimeEntryDaySummaryDAO dao;
    UserIdDateEvaluator evaluator;

    synchronized public Map<UserIdDate, UserIdDateEvaluation> checkAll() {
        final ConcurrentMap<UserIdDate, UserIdDateEvaluation> done = new ConcurrentHashMap<>();
        Set<UserIdDate> notValidatedOnes;
        while (!(notValidatedOnes = this.firstNotValidatedUserIdDates()).isEmpty()) {
            for (UserIdDate userIdDate : notValidatedOnes) {
                if (done.containsKey(userIdDate)) {
                    throw new IllegalStateException("Already validated UserIdDate: " + userIdDate);
                }
                UserIdDateEvaluation result = this.evaluator.check(userIdDate);
                if (done.putIfAbsent(userIdDate, result) != null) {
                    throw new IllegalStateException("Already validated UserIdDate: " + userIdDate);
                }
            }
        }
        log.info("no more validateable");
        return done;
    }

    Set<UserIdDate> firstNotValidatedUserIdDates() {
        return this.dao.firstNotValidated().map(UserIdDate::effected).orElseGet(Set::of);
    }

}
