package com.example.gymzy.general.api.musclewiki;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class ejerciciomuscle {
    private String name;
    private List<String> instructions;
    private List<String> images;
    @SerializedName("primaryMuscles")
    private List<String> primaryMuscles;

    public String getName() { return name; }

    public String getTarget() {
        return (primaryMuscles != null && !primaryMuscles.isEmpty()) ? primaryMuscles.get(0) : "";
    }

    public String getStepsFormatted() {
        if (instructions == null || instructions.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (String s : instructions) {
            sb.append("• ").append(s).append("\n\n");
        }
        return sb.toString();
    }

    public String getVideoUrl() {
        if (images != null && !images.isEmpty()) return images.get(0);
        return null;
    }

    public void setVideoUrl(String url) {
        if (images == null) images = new java.util.ArrayList<>();
        if (images.isEmpty()) images.add(url);
        else images.set(0, url);
    }
}