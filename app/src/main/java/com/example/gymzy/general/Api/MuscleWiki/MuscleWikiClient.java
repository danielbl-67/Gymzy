package com.example.gymzy.general.Api.MuscleWiki;

import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MuscleWikiClient {

    private static final String JSON_URL = "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/dist/exercises.json";
    private static final String IMG_BASE_URL = "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/";

    public interface EjerciciosCallback {
        void onResponse(List<EjercicioMuscle> ejercicios);
        void onFailure(String error);
    }

    public static void getEjerciciosPorMusculo(String musculoABuscar, EjerciciosCallback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(JSON_URL).build();

        Log.d("GYMZY_API", "Iniciando descarga de ejercicios...");

        new Thread(() -> {
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String json = response.body().string();
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<EjercicioMuscle>>(){}.getType();
                    List<EjercicioMuscle> todos = gson.fromJson(json, listType);

                    List<EjercicioMuscle> filtrados = new ArrayList<>();

                    if (todos != null) {
                        for (EjercicioMuscle ej : todos) {
                            // Filtramos: si el músculo del ejercicio contiene lo que buscamos
                            if (ej.getTarget().toLowerCase().contains(musculoABuscar.toLowerCase())) {
                                // Arreglamos la URL de la imagen
                                if (ej.getVideoUrl() != null && !ej.getVideoUrl().startsWith("http")) {
                                    ej.setVideoUrl(IMG_BASE_URL + ej.getVideoUrl());
                                }
                                filtrados.add(ej);
                            }
                        }
                    }
                    Log.d("GYMZY_API", "Filtrados " + filtrados.size() + " ejercicios para: " + musculoABuscar);
                    callback.onResponse(filtrados);

                } else {
                    callback.onFailure("Error " + response.code());
                }
            } catch (IOException e) {
                callback.onFailure(e.getMessage());
            }
        }).start();
    }
}