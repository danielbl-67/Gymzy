package com.example.gymzy.general.api.musclewiki;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Cliente de red encargado de realizar las solicitudes HTTP asíncronas a los repositorios de MuscleWiki.
 * Descarga los archivos de datos en formato JSON correspondientes al género y grupo muscular indicados,
 * procesando y mapeando las respuestas mediante la librería Gson.
 */
public class musclewikiapi {

    // URL base de ejemplo (donde se alojan los JSON de la comunidad)
    private static final String BASE_URL = "https://raw.githubusercontent.com/musclewiki/exercises/main/data/";

    /**
     * Interfaz de comunicación (Callback) para interceptar los flujos de respuesta de la solicitud de red.
     */
    public interface MuscleCallback {
        /**
         * Evento disparado cuando la consulta de red finaliza con éxito y los datos se han parseado correctamente.
         *
         * @param ejercicios Lista de objetos {@link ejerciciomuscle} recuperada del servidor.
         */
        void onSuccess(List<ejerciciomuscle> ejercicios);

        /**
         * Evento disparado cuando ocurre un problema crítico en la petición, caída del servidor o error de mapeo.
         *
         * @param error Mensaje descriptivo con el detalle técnico de la falla.
         */
        void onError(String error);
    }

    /**
     * Ejecuta una petición HTTP asíncrona en un hilo secundario para descargar el catálogo de ejercicios.
     * Construye dinámicamente la URL en base al género (male/female) y al músculo mapeado en formato string.
     *
     * @param genero   Cadena de texto que especifica el género del modelo anatómico (ej: "male", "female").
     * @param musculo  Clave en inglés que identifica el grupo muscular objetivo (ej: "chest", "biceps").
     * @param callback Instancia del escuchador encargado de procesar el éxito o fracaso de la transacción.
     */
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
                    Type listType = new TypeToken<List<ejerciciomuscle>>(){}.getType();
                    List<ejerciciomuscle> lista = gson.fromJson(json, listType);

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