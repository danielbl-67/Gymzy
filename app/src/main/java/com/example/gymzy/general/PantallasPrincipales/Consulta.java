package com.example.gymzy.general.PantallasPrincipales;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymzy.R;
import com.example.gymzy.general.Api.openfoodfast.Alimento;
import com.example.gymzy.general.Api.openfoodfast.IngredienteAdapter;
import com.example.gymzy.general.Api.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Heredamos de DrawerBaseActivity para mantener el menú lateral
public class Consulta extends DrawerBaseActivity {
    EditText etIngrediente, etGramos;
    TextView tvTotalReceta;
    double totalCaloriasReceta = 0;

    RecyclerView rvIngredientes;
    IngredienteAdapter adapter;
    List<String> nombresList = new ArrayList<>();
    List<Double> caloriasList = new ArrayList<>();

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
        adapter = new IngredienteAdapter(nombresList, caloriasList);
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
            Toast.makeText(Consulta.this, "Datos borrados", Toast.LENGTH_SHORT).show();
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

    private void buscarYAgregar(String query, double gramos) {
        RetrofitClient.getApi().buscarAlimento(query).enqueue(new Callback<Alimento.Response>() {
            @Override
            public void onResponse(Call<Alimento.Response> call, Response<Alimento.Response> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().products.isEmpty()) {

                    Alimento.Product productoSeleccionado = null;

                    for (Alimento.Product p : response.body().products) {
                        if (p.nombre != null && p.nombre.equalsIgnoreCase(query)) {
                            productoSeleccionado = p;
                            break;
                        }
                    }

                    if (productoSeleccionado == null) {
                        productoSeleccionado = response.body().products.get(0);
                        Toast.makeText(Consulta.this, "Usando el resultado más cercano", Toast.LENGTH_SHORT).show();
                    }

                    Alimento.Nutrients n = productoSeleccionado.nutrientes;

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
                    Toast.makeText(Consulta.this, "Alimento no encontrado", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Alimento.Response> call, Throwable t) {
                // Esto te dirá el error real (Ej: "Hostname not verified" o "Timeout")
                Toast.makeText(Consulta.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                t.printStackTrace();
            }
        });
    }
}