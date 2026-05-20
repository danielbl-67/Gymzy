package com.example.gymzy.general.api.openfoodfast;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Interfaz de red de Retrofit que define los endpoints para la API de OpenFoodFacts.
 * Proporciona metodos para realizar consultas de busqueda y mapear las respuestas HTTP.
 */
public interface openfoodapi {

    /**
     * Realiza una peticion GET asincrona o sincrona para buscar productos alimenticios
     * filtrados por un termino de busqueda especifico.
     *
     * @param nombre Termino de busqueda (nombre del alimento o ingrediente) que se enviara a la API.
     * @return Un objeto {@link Call} contenedor de la respuesta mapeada en la clase {@link alimento.Response}.
     */
    @GET("cgi/search.pl?search_simple=1&action=process&json=1")
    Call<alimento.Response> buscarAlimento(@Query("search_terms") String nombre);
}