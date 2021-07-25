package com.vari.clockify.validator.checker;

import java.util.Objects;
import java.util.function.Predicate;

import com.vari.clockify.validator.domain.RestrictionViolation;
import com.vari.clockify.validator.domain.TimeEntry;

public interface PredicateChecker extends Checker {
    Predicate<TimeEntry> getOkPredicate();

    @Override
    default RestrictionViolation check(TimeEntry timeEntry) {
        return Objects.requireNonNull(this.getOkPredicate(), "predicate cannot null").test(timeEntry) ? null
                : new RestrictionViolation(this.getRestriction(), null);
    }

}
