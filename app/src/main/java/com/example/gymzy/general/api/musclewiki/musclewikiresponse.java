package com.example.gymzy.general.api.musclewiki;

import java.util.List;

/**
 * Modelo de datos envoltorio (Wrapper) utilizado para parsear las respuestas JSON de la API.
 * Se encarga de mapear la estructura cuando el servidor devuelve un objeto contenedor
 * en lugar de un arreglo directo en la raíz de la respuesta.
 */
public class musclewikiresponse {

    // Algunas versiones de la API envuelven la lista en un objeto llamado "exercises"
    private List<ejerciciomuscle> exercises;

    /**
     * Recupera la lista de ejercicios anidada dentro del objeto de respuesta.
     *
     * @return Colección de objetos {@link ejerciciomuscle}.
     */
    public List<ejerciciomuscle> getExercises() {
        return exercises;
    }
}