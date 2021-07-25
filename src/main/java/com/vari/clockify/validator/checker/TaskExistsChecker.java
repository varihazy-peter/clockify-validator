package com.vari.clockify.validator.checker;

import java.util.function.Predicate;

import org.apache.logging.log4j.util.Strings;

import com.vari.clockify.validator.domain.Restriction;
import com.vari.clockify.validator.domain.TimeEntry;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TaskExistsChecker implements PredicateChecker {

    Restriction restriction = Restriction.TASK_EXISTS;

    Predicate<TimeEntry> okPredicate = timeEntry -> //
    timeEntry.getTask() != null && timeEntry.getTask().getId() != null && !Strings.isBlank(timeEntry.getTask().getId());

}
