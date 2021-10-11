package com.vari.clockify.check.checker;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.validation.beanvalidation.OptionalValidatorFactoryBean;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import com.vari.clockify.check.confpr.ClockifyConfpr;
import com.vari.clockify.check.domain.UserIdDate;
import com.vari.clockify.check.domain.document.TimeEntry;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest(classes = { UserIdDateEvaluatorTest.CheckerConfig.class })
class UserIdDateEvaluatorTest {

    @Configuration
    @ComponentScan(basePackageClasses = { UserIdDateEvaluator.class })
    @Import({ OptionalValidatorFactoryBean.class, JacksonAutoConfiguration.class })
    @ConfigurationPropertiesScan(basePackageClasses = ClockifyConfpr.class)
    public static class CheckerConfig {
    }

    @Autowired
    UserIdDateEvaluator userIdDateEvaluator;
    @MockBean
    TimeEntryDaySummaryDAO dao;
    UserIdDate userIdDate = new UserIdDate("5f9ed7381b69c27d1628360b", LocalDate.of(2021, 10, 8));
    Set<String> timeEntryIs = Set.of("6160239d0908333f1c152a64", "61604a9b7002f4130da635ae", "61603aa490029368f0653d2b", "616024bc0908333f1c153b63", "615ff0ca0759435f6bfca2a3", "61603f901bdc8418c2ca8ed1", "615ff07d7002f4130d9f71a2", "615ff0b890029368f0605e15");
    List<TimeEntry> timeEntries;

    @BeforeEach
    void loadTimeEntries(@Autowired ObjectMapper objectMapper) throws IOException {
        byte[] json = Resources.toByteArray(Resources.getResource("time-entries.json"));
        timeEntries = objectMapper.readerForListOf(TimeEntry.class).readValue(json);

        Mockito.when(dao.firstNotValidated()).thenReturn(timeEntries.stream().findFirst()).thenReturn(Optional.empty());
        Mockito.when(dao.findTimeEntries(eq(userIdDate))).thenReturn(timeEntries);
        Mockito.when(dao.saveDaySummary(any())).thenAnswer(a -> a.getArgument(0));
        Mockito.when(dao.saveTimeEntries(any())).thenAnswer(a -> a.getArgument(0));
    }

    @Test
    void test() throws JsonProcessingException {
        UserIdDateEvaluation userIdDateEvaluation = userIdDateEvaluator.check(userIdDate);
        assertThat(userIdDateEvaluation).isNotNull();
        assertThat(userIdDateEvaluation.getUserIdDate()).isEqualTo(this.userIdDate);
        assertThat(Duration.parse(userIdDateEvaluation.getDuration())).isEqualTo(Duration.ZERO.plusHours(7).plusMinutes(37).plusSeconds(32));
        assertThat(userIdDateEvaluation.getTimeEntryIds()).containsExactlyInAnyOrderElementsOf(timeEntryIs);
        assertThat(userIdDateEvaluation.getValidatedAt()).isNotNull();
        assertThat(userIdDateEvaluation.getViolations()).isNotNull();
    }

}
