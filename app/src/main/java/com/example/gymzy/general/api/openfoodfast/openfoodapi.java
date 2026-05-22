package com.example.gymzy.general.api.openfoodfast;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Interfaz de red de Retrofit optimizada para la API de OpenFoodFacts.
 * Filtra los campos requeridos y apunta al subdominio regional de España para acelerar la respuesta.
 */
public interface openfoodapi {

    /**
     * Realiza una petición GET optimizada restringiendo los campos devueltos por el servidor.
     *
     * @param nombre Término de búsqueda del ingrediente.
     * @return Objeto Call con la respuesta minimizada.
     */
    @GET("https://es.openfoodfacts.org/cgi/search.pl?action=process&json=1&fields=product_name,nutriments")
    Call<alimento.Response> buscarAlimento(@Query("search_terms") String nombre);
}