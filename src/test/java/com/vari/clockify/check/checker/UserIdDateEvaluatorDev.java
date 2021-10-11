package com.vari.clockify.check.checker;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.validation.beanvalidation.OptionalValidatorFactoryBean;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.spring.autoconfigure.core.GcpContextAutoConfiguration;
import com.google.cloud.spring.autoconfigure.firestore.GcpFirestoreAutoConfiguration;
import com.google.cloud.spring.data.firestore.repository.config.EnableReactiveFirestoreRepositories;
import com.vari.clockify.check.confpr.ClockifyConfpr;
import com.vari.clockify.check.domain.UserIdDate;
import com.vari.clockify.check.domain.repository.DaySummaryRepository;
import com.vari.clockify.check.domain.repository.TimeEntryRepository;

@SpringBootTest(classes = { UserIdDateEvaluatorDev.CheckerConfig.class })
@EnabledIfEnvironmentVariable(named = "ENV", matches = "dev")
class UserIdDateEvaluatorDev {

    @EnableReactiveFirestoreRepositories(basePackageClasses = UserIdDate.class)
    @ComponentScan(basePackageClasses = { UserIdDateEvaluator.class })
    @Import({ GcpFirestoreAutoConfiguration.class, GcpContextAutoConfiguration.class,
            OptionalValidatorFactoryBean.class, JacksonAutoConfiguration.class })
    @ConfigurationPropertiesScan(basePackageClasses = ClockifyConfpr.class)
    @Configuration
    public static class CheckerConfig {
    }

    UserIdDate userIdDate = new UserIdDate("5f9ed7381b69c27d1628360b", LocalDate.of(2021, 10, 8));

    @Autowired
    UserIdDateEvaluator userIdDateEvaluator;
    @Autowired
    DaySummaryRepository daySummaryRepository;
    @Autowired
    TimeEntryRepository timeEntryRepository;
    @Autowired
    Firestore firestore;

    @Test
    void test() {
    }
}
