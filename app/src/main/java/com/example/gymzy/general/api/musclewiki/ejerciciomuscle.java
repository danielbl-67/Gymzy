package com.example.gymzy.general.api.musclewiki;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class ejerciciomuscle {
    private String name;

    @SerializedName("primaryMuscles")
    private List<String> primaryMuscles;

    private List<String> instructions;

    @SerializedName("images")
    private List<String> images;

    public ejerciciomuscle() {}

    public String getName() {
        return name;
    }

    /**
     * Obtiene el primer músculo objetivo de la lista para poder realizar los filtros.
     */
    public String getTarget() {
        return (primaryMuscles != null && !primaryMuscles.isEmpty()) ? primaryMuscles.get(0) : "";
    }

    /**
     * Inyecta la URL absoluta construida para la imagen o el GIF explicativo.
     * Evita errores de desbordamiento de memoria o punteros nulos inicializando la lista si viene vacía.
     */
    public void setVideoUrl(String url) {
        if (images == null) {
            images = new ArrayList<>();
        }

        if (images.isEmpty()) {
            images.add(url);
        } else {
            images.set(0, url);
        }
    }

    /**
     * Da formato de lista con viñetas limpias a los pasos del ejercicio.
     */
    public String getStepsFormatted() {
        if (instructions == null || instructions.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (String s : instructions) {
            sb.append("• ").append(s).append("\n\n");
        }
        return sb.toString();
    }

    /**
     * Recupera el enlace del recurso visual de la primera posición.
     */
    public String getVideoUrl() {
        if (images != null && !images.isEmpty()) {
            return images.get(0);
        }
        return null;
    }
}