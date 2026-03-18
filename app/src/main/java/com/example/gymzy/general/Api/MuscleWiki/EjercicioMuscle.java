package com.example.gymzy.general.Api.MuscleWiki;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class EjercicioMuscle {
    private String name;

    @SerializedName("primaryMuscles")
    private List<String> primaryMuscles;

    private List<String> instructions;

    @SerializedName("images")
    private List<String> images;

    public EjercicioMuscle() {}

    public String getName() { return name; }

    // Obtenemos el primer músculo de la lista para filtrar
    public String getTarget() {
        return (primaryMuscles != null && !primaryMuscles.isEmpty()) ? primaryMuscles.get(0) : "";
    }

    public void setVideoUrl(String url) {
        if (images == null) java.util.Collections.singletonList(url);
        else images.set(0, url);
    }

    public String getStepsFormatted() {
        if (instructions == null || instructions.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (String s : instructions) sb.append("• ").append(s).append("\n\n");
        return sb.toString();
    }
    // Dentro de EjercicioMuscle.java

    public String getVideoUrl() {
        if (images != null && !images.isEmpty()) {
            // Importante: No añadas la base aquí si ya la añadimos en el MuscleWikiClient
            return images.get(0);
        }
        return null;
    }
}