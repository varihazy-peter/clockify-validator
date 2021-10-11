package com.vari.clockify.check.confpr;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.NotEmpty;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import lombok.NonNull;
import lombok.Value;

@ConfigurationProperties(prefix = "clockify")
@ConstructorBinding
@Value
public class ClockifyConfpr {
    @NotEmpty
    List<Tag> tags;

    @Value
    public static class Tag {
        @NonNull
        String id;
        String name;
    }
}
