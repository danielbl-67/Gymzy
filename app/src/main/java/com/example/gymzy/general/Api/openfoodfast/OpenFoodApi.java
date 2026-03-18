package com.example.gymzy.general.Api.openfoodfast;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenFoodApi {
    @GET("cgi/search.pl?search_simple=1&action=process&json=1")
    Call<Alimento.Response> buscarAlimento(@Query("search_terms") String nombre);
}
