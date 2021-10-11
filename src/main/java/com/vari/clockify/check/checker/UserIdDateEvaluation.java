package com.vari.clockify.check.checker;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vari.clockify.check.domain.UserIdDate;

import lombok.NonNull;
import lombok.Value;

@Value
public class UserIdDateEvaluation {
    @NonNull
    UserIdDate userIdDate;
    @NonNull
    Instant validatedAt;
    String duration;
    @NonNull
    Set<String> timeEntryIds;
    @NonNull
    Map<String, List<String>> violations;
    public boolean hasViolations() {
        return ! this.violations.isEmpty();
    }
}
