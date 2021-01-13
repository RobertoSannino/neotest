package it.larus.test.neotest.util;

import java.util.HashMap;
import java.util.Map;

public class Relationship {

    private String id;

    private String type;

    private Map<String, Object> properties;

    private Node startNode;

    private Node endNode;

    public Relationship() {

    }

    public Relationship(String id) {
        this.id = id;
    }

    public Relationship(String id, String type, Map<String, Object> properties, Node startNode, Node endNode) {
        this.id = id;
        this.type = type;
        this.properties = properties;
        this.startNode = startNode;
        this.endNode = endNode;
    }

    public Relationship(String id, String type, Map<String, Object> properties) {
        this.id = id;
        this.type = type;
        this.properties = properties;
    }

    public Relationship(String type, Map<String, Object> properties) {
        this.type = type;
        this.properties = properties;
    }

    public Relationship(String type, Map<String, Object> properties, Node startNode, Node endNode) {
        this.type = type;
        this.properties = properties;
        this.startNode = startNode;
        this.endNode = endNode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public Node getStartNode() {
        return startNode;
    }

    public void setStartNode(Node startNode) {
        this.startNode = startNode;
    }

    public Node getEndNode() {
        return endNode;
    }

    public void setEndNode(Node endNode) {
        this.endNode = endNode;
    }

    public Map<String, Object> asMap(){
        Map<String, Object> map = new HashMap<>();
        map.put("id", this.id);
        map.put("type", this.type);
        map.put("properties", this.properties);
        return map;
    }

    @Override
    public String toString() {
        return "Relationship{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", properties=" + properties +
                ", startNode=" + startNode +
                ", endNode=" + endNode +
                '}';
    }
}
