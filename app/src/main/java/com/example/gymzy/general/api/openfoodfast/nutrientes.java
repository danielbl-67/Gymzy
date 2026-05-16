package com.example.gymzy.general.api.openfoodfast;

import com.google.gson.annotations.SerializedName;

public class nutrientes {
    // La API devuelve "ENERC_KCAL", "PROCNT", etc.
    @SerializedName("ENERC_KCAL") public double calorias;
    @SerializedName("PROCNT") public double proteinas;
    @SerializedName("FAT") public double grasas;
    @SerializedName("CHOCDF") public double carbohidratos;
}