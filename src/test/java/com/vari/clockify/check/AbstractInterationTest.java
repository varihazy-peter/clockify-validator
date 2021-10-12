package com.vari.clockify.check;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.FirestoreEmulatorContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.NoCredentialsProvider;

import io.vavr.control.Try;

@SpringBootTest
@ActiveProfiles("firestore-emulator")
@Import(AbstractInterationTest.EmulatorConfiguration.class)
public abstract class AbstractInterationTest {

    @Container
    private static final FirestoreEmulatorContainer firestoreEmulator = Try.success("gcr.io/google.com/cloudsdktool/cloud-sdk:316.0.0-emulators")
            .map(DockerImageName::parse)
            .map(FirestoreEmulatorContainer::new)
            .andThen(GenericContainer::start)
            .get();

    @DynamicPropertySource
    public static void emulatorProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.cloud.gcp.firestore.host-port", firestoreEmulator::getEmulatorEndpoint);
    }

    @TestConfiguration
    public static class EmulatorConfiguration {
        @Bean
        CredentialsProvider googleCredentials() {
            return NoCredentialsProvider.create();
        }
    }

}
