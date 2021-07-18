package com.vari.clockify.validator;

import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.vari.clockify.validator.domain.TimeEntryRepository;

class ClockifyValidatorApplicationTests extends AbstractInterationTest {

    @Autowired
    TimeEntryRepository timeEntryRepository;

    @Test
    void contextLoads() throws InterruptedException, ExecutionException {
        long count = timeEntryRepository.count().block();
        Assertions.assertEquals(0, count);
    }

}
