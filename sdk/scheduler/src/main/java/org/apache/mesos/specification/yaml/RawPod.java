package org.apache.mesos.specification.yaml;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.mesos.util.WriteOnceLinkedHashMap;

import java.util.Collection;
import java.util.LinkedHashMap;

/**
 * Raw YAML pod.
 */
public class RawPod {
    private String name;
    private String placement;
    private Integer count;
    private String strategy;
    private String user;
    private WriteOnceLinkedHashMap<String, RawTask> tasks;
    private Collection<RawResourceSet> resourceSets;

    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    public Collection<RawResourceSet> getResourceSets() {
        return resourceSets;
    }

    @JsonProperty("resource-sets")
    public void setResourceSets(Collection<RawResourceSet> resourceSets) {
        this.resourceSets = resourceSets;
    }

    public String getPlacement() {
        return placement;
    }

    @JsonProperty("placement")
    public void setPlacement(String placement) {
        this.placement = placement;
    }

    public Integer getCount() {
        return count;
    }

    @JsonProperty("count")
    public void setCount(Integer count) {
        this.count = count;
    }

    public String getStrategy() {
        return strategy;
    }

    @JsonProperty("strategy")
    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    public LinkedHashMap<String, RawTask> getTasks() {
        return tasks;
    }

    @JsonProperty("tasks")
    public void setTasks(WriteOnceLinkedHashMap<String, RawTask> tasks) {
        this.tasks = tasks;
    }

    public String getUser() {
        return user;
    }

    @JsonProperty("user")
    public void setUser(String user) {
        this.user = user;
    }
}
