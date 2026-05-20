package com.example.gymzy.general.api.musclewiki;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

/**
 * Modelo de datos destinado a mapear las propiedades de un ejercicio devuelto por la API de MuscleWiki.
 * Contiene metodos utilitarios para formatear las instrucciones de ejecucion y gestionar las URLs multimedia.
 */
public class ejerciciomuscle {
    private String name;
    private List<String> instructions;
    private List<String> images;

    @SerializedName("primaryMuscles")
    private List<String> primaryMuscles;

    /**
     * Obtiene el nombre original del ejercicio en ingles.
     *
     * @return Cadena de texto con el nombre del ejercicio.
     */
    public String getName() { return name; }

    /**
     * Recupera el musculo objetivo principal del ejercicio mapeado en la respuesta.
     *
     * @return El nombre del primer musculo de la lista, o una cadena vacia si no posee registros.
     */
    public String getTarget() {
        return (primaryMuscles != null && !primaryMuscles.isEmpty()) ? primaryMuscles.get(0) : "";
    }

    /**
     * Transforma la lista de instrucciones de ejecucion en una unica cadena de texto
     * formateada con viñetas y saltos de linea para su correcta visualizacion en la interfaz.
     *
     * @return Instrucciones formateadas para la UI, o una cadena vacia si no hay datos disponibles.
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
     * Obtiene la direccion URL del recurso multimedia (imagen, GIF o video explicativo) del ejercicio.
     *
     * @return Enlace de la primera posicion del listado de imagenes, o null si la coleccion esta vacia.
     */
    public String getVideoUrl() {
        if (images != null && !images.isEmpty()) return images.get(0);
        return null;
    }

    /**
     * Inyecta de forma segura o sobrescribe la URL del recurso multimedia en el primer indice del listado.
     *
     * @param url Enlace de destino del recurso multimedia del ejercicio.
     */
    public void setVideoUrl(String url) {
        if (images == null) images = new java.util.ArrayList<>();
        if (images.isEmpty()) images.add(url);
        else images.set(0, url);
    }
}