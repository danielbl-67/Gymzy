package com.example.gymzy.general.api.musclewiki;

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

/**
 * Cliente de red encargado de descargar de forma asincrona la base de datos completa de ejercicios.
 * Realiza el procesamiento del JSON, filtra las rutinas segun el grupo muscular especificado
 * y reconstruye las rutas relativas de las imagenes para transformarlas en URLs absolutas validas.
 */
public class musclewikiclient {

    private static final String JSON_URL = "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/dist/exercises.json";
    private static final String IMG_BASE_URL = "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/";

    /**
     * Interfaz de comunicacion (Callback) encargada de despachar la coleccion filtrada de datos
     * o notificar las incidencias producidas durante la transaccion de red.
     */
    public interface EjerciciosCallback {
        /**
         * Evento disparado cuando la consulta finaliza de forma exitosa y se ha completado el filtrado de datos.
         *
         * @param ejercicios Lista refinada de objetos {@link ejerciciomuscle} que coinciden con el criterio de busqueda.
         */
        void onResponse(List<ejerciciomuscle> ejercicios);

        /**
         * Evento disparado ante cualquier anomalia en la solicitud HTTP, error de servidor o de parseo JSON.
         *
         * @param error Mensaje descriptivo con el detalle tecnico del fallo.
         */
        void onFailure(String error);
    }

    /**
     * Descarga la base de datos global de ejercicios en un hilo secundario de forma asincrona.
     * Evalua secuencialmente el musculo objetivo (Target) o variaciones textuales analíticas en caso
     * de grupos complejos (como la zona del abdomen) y concatena los prefijos absolutos de red.
     *
     * @param musculoABuscar Clave en ingles de la zona muscular elegida para realizar el filtrado.
     * @param callback       Instancia encargada de procesar e interceptar la respuesta de la peticion.
     */
    public static void getEjerciciosPorMusculo(String musculoABuscar, EjerciciosCallback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(JSON_URL).build();

        Log.d("GYMZY_API", "Iniciando descarga de ejercicios...");

        new Thread(() -> {
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String json = response.body().string();
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<ejerciciomuscle>>(){}.getType();
                    List<ejerciciomuscle> todos = gson.fromJson(json, listType);

                    List<ejerciciomuscle> filtrados = new ArrayList<>();

                    if (todos != null) {
                        for (ejerciciomuscle ej : todos) {
                            String target = ej.getTarget().toLowerCase();
                            String nombreEjercicio = ej.getName() != null ? ej.getName().toLowerCase() : "";
                            String busqueda = musculoABuscar.toLowerCase();

                            // ⚡ FILTRO INTELIGENTE: Si busca abdominales ("abs"), barremos variaciones y nombres clave del repositorio
                            boolean coincideAbs = busqueda.equals("abs") &&
                                    (target.contains("abs") ||
                                            target.contains("abdominis") ||
                                            target.contains("obliques") ||
                                            nombreEjercicio.contains("crunch") ||
                                            nombreEjercicio.contains("sit-up") ||
                                            nombreEjercicio.contains("plank"));

                            // Filtro estándar para el resto de grupos musculares anatómicos
                            boolean coincideMusculo = target.contains(busqueda);

                            // Si cumple cualquiera de los criterios de coincidencia, se procesa el ingrediente/ejercicio
                            if (coincideMusculo || coincideAbs) {
                                // Arreglamos la URL de la imagen relativa pasándola a absoluta
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