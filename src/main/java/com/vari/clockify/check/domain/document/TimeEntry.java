package com.vari.clockify.check.domain.document;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.google.cloud.firestore.FieldPath;
import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.IgnoreExtraProperties;
import com.google.cloud.spring.data.firestore.Document;
import com.vari.clockify.check.validation.NeededTag;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Document(collectionName = TimeEntry.COLLECTION_NAME)
@IgnoreExtraProperties
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = false)
@NoArgsConstructor
@AllArgsConstructor
public class TimeEntry {
    public final static String COLLECTION_NAME = "TimeEntries";

    @DocumentId
    String documentId;
    @NotBlank
    String id;
    String description;
    @NotBlank
    String projectId;
    String userId;
    public final static FieldPath USERID_FIELD_PATH = FieldPath.of("userId");
    @NotBlank
    String workspaceId;
    Boolean billable;
    public final static FieldPath TIME_INTERVALSTART_FIELD_PATH = FieldPath.of("timeInterval", "start");

    TimeInterval timeInterval;
    Boolean isLocked;

    @IgnoreExtraProperties
    @Data
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = false)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimeInterval {
        String duration;

        public Duration duration() {
            return duration == null ? null : Duration.parse(duration);
        }

        String start;

        public OffsetDateTime start() {
            return start == null ? null : OffsetDateTime.parse(start);
        }

        String end;

        public OffsetDateTime end() {
            return end == null ? null : OffsetDateTime.parse(end);
        }
    }

    @NotEmpty
    @NeededTag
    List<Tag> tags;

    @IgnoreExtraProperties
    @Data
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = false)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Tag {
        String id, name, workspaceId;
        Boolean archived;
    }

    @NotNull
    Task task;

    @IgnoreExtraProperties
    @Data
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = false)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Task {
        @NotBlank
        String id;
        String name, projectId, workspaceId;
        Boolean billable;
    }

    ValidationData validationData;

    @IgnoreExtraProperties
    @Data
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = false)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValidationData {
        String receivedAt;

        public Instant receivedAt() {
            return receivedAt == null ? null : Instant.parse(receivedAt);
        }

        String validatedAt;

        public Instant validatedAt() {
            return validatedAt == null ? null : Instant.parse(validatedAt);
        }

        List<String> constraintViolationMessages;

        public ValidationData validatedAt(Instant validatedAt) {
            this.validatedAt = validatedAt == null ? null : validatedAt.toString();
            return this;
        }

        String date;

        public LocalDate date() {
            return Optional.ofNullable(this.date).map(LocalDate::parse).orElseThrow();
        }

        String daySummaryDate;

        public LocalDate daySummaryDate() {
            return Optional.ofNullable(this.daySummaryDate).map(LocalDate::parse).orElse(null);
        }

        public ValidationData daySummaryDate(LocalDate daySummaryDate) {
            return this.setDaySummaryDate(daySummaryDate.toString());
        }
    }
}
