package com.vari.clockify.validator.checker;

import java.util.function.Predicate;

import com.vari.clockify.validator.domain.Restriction;
import com.vari.clockify.validator.domain.TimeEntry;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TagExistsChecker implements PredicateChecker {

    Restriction restriction = Restriction.TAG_EXISTS;

    Predicate<TimeEntry> okPredicate = timeEntry -> //
    timeEntry.getTags() != null && !timeEntry.getTags().isEmpty();

}
