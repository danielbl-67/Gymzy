package com.example.gymzy.general.api.openfoodfast;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Modelo de datos unificado para parsear las respuestas JSON de la API de OpenFoodFacts.
 * Contiene estructuras internas anidadas para mapear productos y sus respectivas tablas nutricionales.
 */
public class alimento {

    /**
     * Contenedor principal de la respuesta de busqueda de la API.
     */
    public static class Response {
        /** Lista de productos devueltos que coinciden con el criterio de busqueda. */
        @SerializedName("products")
        public List<Product> products;
    }

    /**
     * Representacion individual de un producto alimenticio.
     */
    public static class Product {
        /** Nombre comercial o descriptivo del producto. */
        @SerializedName("product_name")
        public String nombre;

        /** Objeto con el desglose de nutrientes del producto. */
        @SerializedName("nutriments")
        public Nutrients nutrientes;
    }

    /**
     * Desglose detallado de las propiedades nutricionales del producto calculado por cada 100 gramos.
     */
    public static class Nutrients {
        /** Cantidad de energia medida en kilocalorias (kcal). */
        @SerializedName("energy-kcal_100g")
        public double kcal;

        /** Cantidad de proteinas medidas en gramos (g). */
        @SerializedName("proteins_100g")
        public double proteina;

        /** Cantidad de carbohidratos medidos en gramos (g). */
        @SerializedName("carbohydrates_100g")
        public double carbohidratos;

        /** Cantidad de grasas totales medidas en gramos (g). */
        @SerializedName("fat_100g")
        public double grasa;
    }
}