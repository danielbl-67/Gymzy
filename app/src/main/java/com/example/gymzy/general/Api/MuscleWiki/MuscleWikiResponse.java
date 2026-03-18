package com.example.gymzy.general.Api.MuscleWiki;

import java.util.List;

public class MuscleWikiResponse {
    // Algunas versiones de la API envuelven la lista en un objeto llamado "exercises"
    private List<EjercicioMuscle> exercises;

    public List<EjercicioMuscle> getExercises() {
        return exercises;
    }
}