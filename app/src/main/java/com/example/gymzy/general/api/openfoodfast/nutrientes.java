package com.example.gymzy.general.api.openfoodfast;

import com.google.gson.annotations.SerializedName;

/**
 * Modelo de datos destinado a mapear las propiedades de los macronutrientes
 * principales devueltos por los servicios o respuestas complementarias de la API.
 * Vincula de forma directa los codigos estandarizados de nutrientes con variables locales.
 */
public class nutrientes {

    /** Cantidad total de energia estimada en kilocalorias (kcal). */
    @SerializedName("ENERC_KCAL")
    public double calorias;

    /** Cantidad total de proteinas presentes, medida en gramos (g). */
    @SerializedName("PROCNT")
    public double proteinas;

    /** Cantidad total de lipidos o grasas, medida en gramos (g). */
    @SerializedName("FAT")
    public double grasas;

    /** Cantidad total de carbohidratos por diferencia, medida en gramos (g). */
    @SerializedName("CHOCDF")
    public double carbohidratos;
}