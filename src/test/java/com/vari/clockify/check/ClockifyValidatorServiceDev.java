package com.vari.clockify.check;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.vari.clockify.check.checker.ClockifyChecker;

@SpringBootTest
@EnabledIfEnvironmentVariable(named = "ENV", matches = "dev")
class ClockifyValidatorServiceDev {

    @Autowired
    ClockifyChecker clockifyChecker;

    @Test
    void test() {
        clockifyChecker.checkAll();
    }

}
