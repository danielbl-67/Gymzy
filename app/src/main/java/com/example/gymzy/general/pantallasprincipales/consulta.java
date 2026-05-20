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
 * Actividad que implementa la calculadora de calorias de recetas.
 * Hereda de {@link menuinferior} y consume la API de OpenFoodFacts mediante Retrofit
 * para buscar alimentos y calcular macronutrientes segun el pesaje ingresado.
 */
public class consulta extends menuinferior {
    EditText etIngrediente, etGramos;
    TextView tvTotalReceta;
    double totalCaloriasReceta = 0;

    RecyclerView rvIngredientes;
    ingredienteadapter adapter;
    List<String> nombresList = new ArrayList<>();
    List<Double> caloriasList = new ArrayList<>();

    /**
     * Infla el diseno, enlaza componentes visuales, inicializa el adaptador
     * del RecyclerView y establece la logica de limpieza e ingreso de ingredientes.
     *
     * @param savedInstanceState Contiene el estado previo de los datos de la interfaz.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Inflamos la vista dentro del Drawer para que el menú funcione
        View view = getLayoutInflater().inflate(R.layout.activity_consulta_nutrientes, null);
        setContentView(view);

        // 2. Establecemos el título en la barra superior del Drawer
        allocateActivityTitle("Calculadora de Recetas");

        // 3. Inicializar vistas
        etIngrediente = findViewById(R.id.etIngrediente);
        etGramos = findViewById(R.id.etGramos);
        tvTotalReceta = findViewById(R.id.tvTotalReceta);
        Button btnAgregar = findViewById(R.id.btnAgregar);
        Button btnBorrar = findViewById(R.id.btnLimpiar);

        // 4. Configurar RecyclerView
        rvIngredientes = findViewById(R.id.rvIngredientes);
        rvIngredientes.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ingredienteadapter(nombresList, caloriasList);
        rvIngredientes.setAdapter(adapter);

        // 5. Lógica Botón Borrar
        btnBorrar.setOnClickListener(v -> {
            totalCaloriasReceta = 0;
            tvTotalReceta.setText("0.00 kcal");
            etIngrediente.setText("");
            etGramos.setText("");
            nombresList.clear();
            caloriasList.clear();
            adapter.notifyDataSetChanged();
            Toast.makeText(consulta.this, "Datos borrados", Toast.LENGTH_SHORT).show();
        });

        // 6. Lógica Botón Agregar
        btnAgregar.setOnClickListener(v -> {
            String nombre = etIngrediente.getText().toString().trim();
            String gramosStr = etGramos.getText().toString().trim();

            if (!nombre.isEmpty() && !gramosStr.isEmpty()) {
                try {
                    double gramos = Double.parseDouble(gramosStr);
                    buscarYAgregar(nombre, gramos);
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Introduce un número válido en gramos", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Realiza una llamada asincrona HTTP por Retrofit para buscar el alimento.
     * Procesa la respuesta para calcular proporcionalmente las kcal segun los gramos dados
     * e inserta el resultado reactivamente en el adaptador.
     *
     * @param query  Nombre o termino de busqueda del alimento.
     * @param gramos Peso del ingrediente introducido por el usuario.
     */
    private void buscarYAgregar(String query, double gramos) {
        retrofitclient.getApi().buscarAlimento(query).enqueue(new Callback<alimento.Response>() {
            /**
             * Evalua la respuesta del servidor, discrimina el producto mas cercano,
             * actualiza el contador de calorias total e inserta el elemento al inicio de la lista.
             *
             * @param call     Instancia de la llamada HTTP ejecutada.
             * @param response Objeto contenedor de la respuesta de la API.
             */
            @Override
            public void onResponse(Call<alimento.Response> call, Response<alimento.Response> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().products.isEmpty()) {

                    alimento.Product productoSeleccionado = null;

                    for (alimento.Product p : response.body().products) {
                        if (p.nombre != null && p.nombre.equalsIgnoreCase(query)) {
                            productoSeleccionado = p;
                            break;
                        }
                    }

                    if (productoSeleccionado == null) {
                        productoSeleccionado = response.body().products.get(0);
                        Toast.makeText(consulta.this, "Usando el resultado más cercano", Toast.LENGTH_SHORT).show();
                    }

                    alimento.Nutrients n = productoSeleccionado.nutrientes;

                    // Cálculo de calorías basado en los gramos introducidos
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
                    Toast.makeText(consulta.this, "Alimento no encontrado", Toast.LENGTH_SHORT).show();
                }
            }

            /**
             * Captura fallos criticos de red, timeouts o problemas de resolucion de DNS.
             *
             * @param call Instancia de la llamada HTTP ejecutada.
             * @param t    Objeto Throwable con el detalle tecnico del error.
             */
            @Override
            public void onFailure(Call<alimento.Response> call, Throwable t) {
                Toast.makeText(consulta.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                t.printStackTrace();
            }
        });
    }
}