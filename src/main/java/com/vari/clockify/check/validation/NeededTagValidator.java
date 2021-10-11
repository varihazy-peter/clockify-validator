package com.vari.clockify.check.validation;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vari.clockify.check.confpr.ClockifyConfpr;
import com.vari.clockify.check.domain.document.TimeEntry;
import com.vari.clockify.check.domain.document.TimeEntry.Tag;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component
public class NeededTagValidator implements ConstraintValidator<NeededTag, Collection<? extends Tag>> {
    private final Supplier<Set<String>> collectionFactory = () -> new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

    @Autowired
    NeededTagValidator(ClockifyConfpr clockifyConfpr) {
        this.ids = clockifyConfpr.getTags()
                .stream()
                .map(ClockifyConfpr.Tag::getId)
                .collect(Collectors.toCollection(collectionFactory));
        this.names = clockifyConfpr.getTags()
                .stream()
                .map(ClockifyConfpr.Tag::getName)
                .collect(Collectors.toCollection(collectionFactory));
    }

    Set<String> ids;
    Set<String> names;

    @Override
    public boolean isValid(Collection<? extends Tag> value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true;
        }
        Set<TimeEntry.Tag> neededTags = value.stream().filter(this::isTagNeeded).collect(Collectors.toSet());
        if (neededTags.size() == 1) {
            return true;
        }
        if (neededTags.size() == 0) {
            return false;
        }
        Set<String> tags = neededTags.stream()
                .map(t -> String.format("Tag#%s:(%s)", t.getId(), t.getName()))
                .collect(Collectors.toUnmodifiableSet());
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate("Too many tags: " + tags)
                .addPropertyNode("tags")
                .addConstraintViolation();
        return false;
    }

    private boolean isTagNeeded(TimeEntry.Tag tag) {
        return this.ids.contains(tag.getId()) || this.names.contains(tag.getName());
    }
}
