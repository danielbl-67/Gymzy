package com.example.gymzy.general.api.musclewiki;

import java.util.List;

public class musclewikiresponse {
    // Algunas versiones de la API envuelven la lista en un objeto llamado "exercises"
    private List<ejerciciomuscle> exercises;

    public List<ejerciciomuscle> getExercises() {
        return exercises;
    }
}