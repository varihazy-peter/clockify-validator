package com.vari.clockify.validator.domain;

import java.time.Duration;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.IgnoreExtraProperties;
import com.google.cloud.spring.data.firestore.Document;

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
    String id;
    String description;
    String projectId;
    String userId;
    String workspaceId;
    Boolean billable;
    TimeInterval timeInterval;
    Boolean isLocked;

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

    List<Tag> tags;

    @Data
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = false)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Tag {
        String id, name, workspaceId;
        Boolean archived;
    }

    Task task;

    @Data
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = false)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Task {
        String id, name, projectId, workspaceId;
        Boolean billable;
    }

    String validated;
    public LocalDate startDate() {
        return Optional.ofNullable(this.timeInterval).map(TimeInterval::start).map(LocalDate::from).orElseThrow();
    }
}
