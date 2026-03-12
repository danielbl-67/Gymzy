package com.example.gymzy.general.Api.MuscleWiki;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MuscleWikiAPI {

    // URL base de ejemplo (donde se alojan los JSON de la comunidad)
    private static final String BASE_URL = "https://raw.githubusercontent.com/musclewiki/exercises/main/data/";

    public interface MuscleCallback {
        void onSuccess(List<EjercicioMuscle> ejercicios);
        void onError(String error);
    }

    public static void fetchEjercicios(String genero, String musculo, MuscleCallback callback) {
        OkHttpClient client = new OkHttpClient();

        // Ejemplo de ruta: data/male/chest.json
        String finalUrl = BASE_URL + genero + "/" + musculo + ".json";

        Request request = new Request.Builder()
                .url(finalUrl)
                .build();

        new Thread(() -> {
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String json = response.body().string();
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<EjercicioMuscle>>(){}.getType();
                    List<EjercicioMuscle> lista = gson.fromJson(json, listType);

                    callback.onSuccess(lista);
                } else {
                    callback.onError("Error al obtener datos: " + response.code());
                }
            } catch (IOException e) {
                callback.onError(e.getMessage());
            }
        }).start();
    }
}