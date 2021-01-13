package it.larus.test.neotest.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Node {

    private String id;
    private List<String> labels;
    private Map<String, Object> properties;
    private Integer degree;

    @JsonCreator
    public Node(@JsonProperty("id") String id,
                @JsonProperty("labels") List<String> labels,
                @JsonProperty("properties") Map<String, Object> properties,
                @JsonProperty("degree") Integer degree){
        this.id = id;
        this.labels = labels;
        this.properties = properties;
        this.degree = degree;
    }

    public Node() {
        this.properties = new HashMap<>();
        this.labels = new ArrayList<>();
    }

    public Node(String id) {
        this.id = id;
        this.properties = new HashMap<>();
        this.labels = new ArrayList<>();
    }

    public Node(String id, List<String> labels, Map<String, Object> properties) {
        this.id = id;
        this.labels = labels;
        this.properties = properties;
    }

    public Node(List<String> labels, Map<String, Object> properties) {
        this.labels = labels;
        this.properties = properties;
    }

    public Node(Map<String, Object> nodeMap) {
        this.id = nodeMap.get("id").toString();
        this.labels = (List<String>) nodeMap.get("labels");
        this.properties = (Map<String, Object>) nodeMap.get("properties");
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public Integer getDegree() {
        return degree;
    }

    public void setDegree(Integer degree) {
        this.degree = degree;
    }

    public Map<String, Object> asMap() {
        Map<String, Object> map = new HashMap<>();
        if (this.id != null) {
            map.put("id", this.id);
        }
        if (this.properties != null) {
            map.put("properties", this.properties);
        }
        map.put("labels", this.labels);

        return map;
    }

    @Override
    public String toString() {
        return "Node{" +
                "id=" + id +
                ", labels=" + labels +
                ", properties=" + properties +
                '}';
    }
}
