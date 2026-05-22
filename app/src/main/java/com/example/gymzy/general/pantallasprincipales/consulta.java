package com.example.gymzy.general.pantallasprincipales;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymzy.R;
import com.example.gymzy.general.api.openfoodfast.alimento;
import com.example.gymzy.general.api.openfoodfast.ingredienteadapter;
import com.example.gymzy.general.api.retrofitclient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Actividad modular que implementa la calculadora reactiva de calorías de recetas.
 * Consume la API optimizada de OpenFoodFacts mediante Retrofit.
 */
public class consulta extends menuinferior {
    private EditText etIngrediente, etGramos;
    private TextView tvTotalReceta;
    private double totalCaloriasReceta = 0;

    private RecyclerView rvIngredientes;
    private ingredienteadapter adapter;
    private List<String> nombresList = new ArrayList<>();
    private List<Double> caloriasList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = getLayoutInflater().inflate(R.layout.activity_consulta_nutrientes, null);
        setContentView(view);
        allocateActivityTitle("Calculadora de Recetas");

        etIngrediente = findViewById(R.id.etIngrediente);
        etGramos = findViewById(R.id.etGramos);
        tvTotalReceta = findViewById(R.id.tvTotalReceta);
        Button btnAgregar = findViewById(R.id.btnAgregar);
        Button btnBorrar = findViewById(R.id.btnLimpiar);

        rvIngredientes = findViewById(R.id.rvIngredientes);
        rvIngredientes.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ingredienteadapter(nombresList, caloriasList);
        rvIngredientes.setAdapter(adapter);

        btnBorrar.setOnClickListener(v -> {
            totalCaloriasReceta = 0;
            tvTotalReceta.setText("0.00 kcal");
            etIngrediente.setText("");
            etGramos.setText("");
            nombresList.clear();
            caloriasList.clear();
            adapter.notifyDataSetChanged();
            Toast.makeText(consulta.this, "Datos limpios", Toast.LENGTH_SHORT).show();
        });

        btnAgregar.setOnClickListener(v -> {
            String nombre = etIngrediente.getText().toString().trim();
            String gramosStr = etGramos.getText().toString().trim();

            if (!nombre.isEmpty() && !gramosStr.isEmpty()) {
                try {
                    double gramos = Double.parseDouble(gramosStr);
                    buscarYAgregar(nombre, gramos);
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Número de gramos inválido", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Campos incompletos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Realiza una llamada asíncrona HTTP por Retrofit para buscar el alimento.
     * Aplica un filtro de doble prioridad (Coincidencia exacta > Coincidencia parcial más corta)
     * para garantizar la precisión en la selección de ingredientes.
     *
     * @param query  Nombre o término de búsqueda del alimento.
     * @param gramos Peso del ingrediente introducido por el usuario.
     */
    private void buscarYAgregar(String query, double gramos) {
        retrofitclient.getApi().buscarAlimento(query).enqueue(new Callback<alimento.Response>() {
            @Override
            public void onResponse(Call<alimento.Response> call, Response<alimento.Response> response) {
                if (response.isSuccessful() && response.body() != null && response.body().products != null && !response.body().products.isEmpty()) {

                    alimento.Product productoSeleccionado = null;
                    String queryLower = query.toLowerCase().trim();

                    // ⚡ FASE 1: Buscar una coincidencia exacta de texto primero
                    for (alimento.Product p : response.body().products) {
                        if (p.nombre != null && p.nombre.trim().equalsIgnoreCase(query)) {
                            productoSeleccionado = p;
                            break; // Coincidencia perfecta encontrada, salimos
                        }
                    }

                    // ⚡ FASE 2: Si no hubo coincidencia exacta, buscamos la coincidencia parcial MÁS CORTA
                    if (productoSeleccionado == null) {
                        int longitudMinima = Integer.MAX_VALUE;

                        for (alimento.Product p : response.body().products) {
                            if (p.nombre != null) {
                                String nombreProdLower = p.nombre.toLowerCase();
                                if (nombreProdLower.contains(queryLower)) {
                                    // El nombre más corto será siempre el más cercano al término original
                                    if (nombreProdLower.length() < longitudMinima) {
                                        longitudMinima = nombreProdLower.length();
                                        productoSeleccionado = p;
                                    }
                                }
                            }
                        }
                    }

                    // ⚡ FASE 3: Si todo lo anterior falla, recurrimos al primer resultado relevante de la API
                    if (productoSeleccionado == null) {
                        productoSeleccionado = response.body().products.get(0);
                    }

                    // Procesamos los nutrientes del producto seleccionado con seguridad
                    if (productoSeleccionado.nutrientes != null) {
                        alimento.Nutrients n = productoSeleccionado.nutrientes;

                        double calIngrediente = (n.kcal * gramos) / 100;
                        totalCaloriasReceta += calIngrediente;

                        tvTotalReceta.setText(String.format("%.2f kcal", totalCaloriasReceta));

                        nombresList.add(0, productoSeleccionado.nombre + " (" + gramos + "g)");
                        caloriasList.add(0, calIngrediente);
                        adapter.notifyItemInserted(0);
                        rvIngredientes.scrollToPosition(0);

                        etIngrediente.setText("");
                        etGramos.setText("");
                    } else {
                        Toast.makeText(consulta.this, "El alimento seleccionado no contiene una tabla nutricional válida", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(consulta.this, "No se encontraron resultados para la búsqueda", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<alimento.Response> call, Throwable t) {
                Toast.makeText(consulta.this, "Fallo de red: " + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}