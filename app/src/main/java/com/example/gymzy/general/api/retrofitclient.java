package com.example.gymzy.general.api;
import com.example.gymzy.general.api.openfoodfast.openfoodapi;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class retrofitclient {
    private static Retrofit retrofit = null;

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