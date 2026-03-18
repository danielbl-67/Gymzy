package com.example.gymzy.general.Api.openfoodfast;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Alimento {

    public static class Response {
        @SerializedName("products")
        public List<Product> products;
    }

    public static class Product {
        @SerializedName("product_name")
        public String nombre;

        @SerializedName("nutriments")
        public Nutrients nutrientes; // Nombre corregido aquí
    }

    public static class Nutrients { // Nombre corregido aquí
        @SerializedName("energy-kcal_100g")
        public double kcal;

        @SerializedName("proteins_100g")
        public double proteina;

        @SerializedName("carbohydrates_100g")
        public double carbohidratos;

        @SerializedName("fat_100g")
        public double grasa;
    }
}