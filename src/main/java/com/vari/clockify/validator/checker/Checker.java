package com.vari.clockify.validator.checker;

import com.vari.clockify.validator.domain.Restriction;
import com.vari.clockify.validator.domain.RestrictionViolation;
import com.vari.clockify.validator.domain.TimeEntry;

public interface Checker {
    Restriction getRestriction();
    RestrictionViolation check(TimeEntry timeEntry);
}
