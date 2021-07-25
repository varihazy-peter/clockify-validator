package com.vari.clockify.validator.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = false)
@NoArgsConstructor
@AllArgsConstructor
public class RestrictionViolation {
    Restriction restriction;
    String detail;
}
