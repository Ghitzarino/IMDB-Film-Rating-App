package classes;

import interfaces.SortableElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Actor implements SortableElement {
    private String name;
    private List<Map<String, String>> performances;
    private String biography;

    // Default Constructor
    public Actor() {
        this.performances = new ArrayList<>();
    }

    // Parameterized Constructor
    public Actor(String name, List<Map<String, String>> performances, String biography) {
        this.name = name;
        this.performances = performances;
        this.biography = biography;
    }

    // Getters and Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Map<String, String>> getPerformances() {
        return performances;
    }

    public void setPerformances(List<Map<String, String>> performances) {
        this.performances = performances;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();
        ret.append("Actor: ").append(name).append("\n     -biography: ");
        if (biography != null) {
            ret.append(biography);
        } else {
            ret.append("N/A");
        }

        ret.append("\n     -performances: ");
        if (performances != null && !performances.isEmpty()) {
            for (Map<String, String> map : performances) {
                if (map.get("title") != null) {
                    ret.append(map.get("title"));
                    if (!map.equals(performances.getLast())) {
                        ret.append(", ");
                    }
                }
            }
        } else {
            ret.append("N/A");
        }

        ret.append("\n");
        return ret.toString();
    }
}