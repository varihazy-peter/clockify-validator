package com.vari.clockify.check.domain.document;

import java.util.List;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.IgnoreExtraProperties;
import com.google.cloud.spring.data.firestore.Document;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Document(collectionName = DaySummary.COLLECTION_NAME)
@IgnoreExtraProperties
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = false)
@NoArgsConstructor
@AllArgsConstructor
public class DaySummary {
    public final static String COLLECTION_NAME = "DaySummary";
    @DocumentId
    String documentId;
    String userId;
    String date;
    String duration;
    String calculatedAt;
    List<String> calculatedTimeEntryIds;
    List<String> failedTimeEntryIds;
}
