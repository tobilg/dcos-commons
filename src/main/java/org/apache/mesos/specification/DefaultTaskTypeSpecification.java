package org.apache.mesos.specification;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.mesos.Protos;

import java.util.Collection;

/**
 * This class provides a default implementation of the TaskTypeSpecification interface.
 */
public class DefaultTaskTypeSpecification implements TaskTypeSpecification {
    private final int count;
    private final String name;
    private final Protos.CommandInfo command;
    private final Collection<ResourceSpecification> resources;
    private final Collection<VolumeSpecification> volumes;
    private final Collection<String> servicePorts;

    public DefaultTaskTypeSpecification(
            int count,
            String name,
            Protos.CommandInfo command,
            Collection<ResourceSpecification> resources,
            Collection<VolumeSpecification> volumes,
            Collection<String> servicePorts) {
        this.count = count;
        this.name = name;
        this.command = command;
        this.resources = resources;
        this.volumes = volumes;
        this.servicePorts = servicePorts;
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Protos.CommandInfo getCommand(int id) {
        return command;
    }

    @Override
    public Collection<ResourceSpecification> getResources() {
        return resources;
    }

    @Override
    public Collection<VolumeSpecification> getVolumes() {
        return volumes;
    }

    @Override
    public Collection<String> getServicePorts() {
        return servicePorts;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
