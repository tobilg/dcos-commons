package org.apache.mesos.specification;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.mesos.Protos;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Optional;

/**
 * This class provides a default implementation of the ResourceSpecification interface.
 */
public class DefaultResourceSpecification implements ResourceSpecification {
    private final String name;
    private final Protos.Value value;
    private final String role;
    private final String principal;
    private final String envKey;

    @JsonCreator
    public DefaultResourceSpecification(
            @JsonProperty("name") String name,
            @JsonProperty("value") Protos.Value value,
            @JsonProperty("role") String role,
            @JsonProperty("principal") String principal,
            @JsonProperty("env_key") String envKey) {
        this.name = name;
        this.value = value;
        this.role = role;
        this.principal = principal;
        this.envKey = envKey;
    }

    @Override
    public Protos.Value getValue() {
        return value;
    }

    @Override
    public String getRole() {
        return role;
    }

    @Override
    public String getPrincipal() {
        return principal;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public Optional<String> getEnvKey() {
        return Optional.of(envKey);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
