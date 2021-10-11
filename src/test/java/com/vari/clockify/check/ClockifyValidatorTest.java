package com.vari.clockify.check;

import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.vari.clockify.check.confpr.ClockifyConfpr;
import com.vari.clockify.check.domain.document.TimeEntry;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@RequiredArgsConstructor
@EnabledIfEnvironmentVariable(named = "ENV", matches = "dev")
class ClockifyValidatorTest {

    @Autowired
    ValidatorFactory validatorFactory;
    @Autowired
    ClockifyConfpr clockifyConfpr;
    String id = "611130ef968d612a35cb7221";

    /*
     * 
     * TimeEntry( documentId=611130ef968d612a35cb7221, id=611130ef968d612a35cb7221,
     * description=BPM Sync, projectId=60225e7f333b766e7514537f,
     * userId=5f9ed7d33a1dc508bbb0e0ec, workspaceId=5f9ecf053a1dc508bbb0caa9,
     * billable=false, timeInterval=TimeEntry.TimeInterval(duration=PT35M46S,
     * start=2021-08-09T13:43:10Z, end=2021-08-09T14:18:56Z), isLocked=false,
     * tags=[TimeEntry.Tag(id=5fa02d96bc67ab6d1e88e741, name=Call / Meeting,
     * workspaceId=5f9ecf053a1dc508bbb0caa9, archived=false)],
     * task=TimeEntry.Task(id=605848e1bcbc69562bbfac07, name=- No related Jira
     * issue, projectId=60225e7f333b766e7514537f,
     * workspaceId=5f9ecf053a1dc508bbb0caa9, billable=true),
     * validationData=TimeEntry.ValidationData(receivedAt=2021-08-09T14:18:58.
     * 120760Z, validatedAt=null, date=2021-08-09, daySummaryDate=null))
     *
     */
    private final TimeEntry.Tag callTag = new TimeEntry.Tag("5fa02d96bc67ab6d1e88e741",
            "Call / Meeting",
            "5f9ecf053a1dc508bbb0caa9",
            false);
    private final TimeEntry.Tag qaTag = new TimeEntry.Tag("5fc60e500120537e18b7479d",
            "QA",
            "5f9ecf053a1dc508bbb0caa9",
            false);

    private final TimeEntry timeEntry() {
        return new TimeEntry(id,
                id,
                "BPM Sync",
                "60225e7f333b766e7514537f",
                "5f9ed7d33a1dc508bbb0e0ec",
                "5f9ecf053a1dc508bbb0caa9",
                false,
                new TimeEntry.TimeInterval("PT35M46S", "2021-08-09T13:43:10Z", "2021-08-09T14:18:56Z"),
                false,
                List.of(callTag, qaTag),
                new TimeEntry.Task("605848e1bcbc69562bbfac07",
                        "- No related Jira issue",
                        "60225e7f333b766e7514537f",
                        "5f9ecf053a1dc508bbb0caa9",
                        true),
                new TimeEntry.ValidationData("2021-08-09T14:18:58.120760Z", null, null, "2021-08-09", null));
    }

    @Test
    void test() {
        TimeEntry timeEntry = timeEntry();
        log.info("timeEntry {}", timeEntry);
        Validator validator = validatorFactory.getValidator();
        Set<ConstraintViolation<TimeEntry>> validation = validator.validate(timeEntry);
        Assertions.assertNotNull(validation);
        validation.forEach(cv -> log.info("{}", cv));
    }

}
