package com.example.gymzy.general.api;

import com.example.gymzy.general.api.openfoodfast.openfoodapi;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Cliente singleton encargado de centralizar y proveer la instancia de Retrofit.
 * Configura la URL base de OpenFoodFacts y el conversor de datos JSON (Gson).
 */
public class retrofitclient {
    private static Retrofit retrofit = null;

    /**
     * Inicializa de forma perezosa (lazy) la instancia de Retrofit si es nula
     * y retorna la implementacion de la interfaz de endpoints openfoodapi.
     *
     * @return Instancia configurada del servicio {@link openfoodapi}.
     */
    public static openfoodapi getApi() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://world.openfoodfacts.org/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(openfoodapi.class);
    }
}