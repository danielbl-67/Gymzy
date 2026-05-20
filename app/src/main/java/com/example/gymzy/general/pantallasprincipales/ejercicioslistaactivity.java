package com.example.gymzy.general.pantallasprincipales;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gymzy.R;
import com.example.gymzy.general.api.musclewiki.ejerciciomuscle;
import com.example.gymzy.general.api.musclewiki.ejerciciosadapter;
import com.example.gymzy.general.api.musclewiki.musclewikiclient;
import java.util.List;

/**
 * Actividad que muestra los ejercicios asociados a un grupo muscular especifico.
 * Se encarga de mapear la categoria seleccionada al ingles y consumir los datos desde la API de MuscleWiki.
 */
public class ejercicioslistaactivity extends AppCompatActivity {

    private RecyclerView rvEjercicios;
    private TextView tvTitulo;
    private ImageButton btnVolver;
    private ejerciciosadapter adapter;

    /**
     * Inicializa la interfaz, recupera el grupo muscular del Intent, realiza el mapeo
     * de idioma y ejecuta la llamada asincrona a la API externa en segundo plano.
     *
     * @param savedInstanceState Contiene el estado previo de los datos de la interfaz.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ejercicios_lista);

        rvEjercicios = findViewById(R.id.rvEjerciciosGenericos);
        tvTitulo = findViewById(R.id.tvTituloCategoria);
        btnVolver = findViewById(R.id.btnVolverLista);

        String musculoSeleccionado = getIntent().getStringExtra("TITULO_MUSCULO");
        tvTitulo.setText(musculoSeleccionado);

        rvEjercicios.setLayoutManager(new LinearLayoutManager(this));

        String musculoParaApi = mapearMusculo(musculoSeleccionado);

        musclewikiclient.getEjerciciosPorMusculo(musculoParaApi, new musclewikiclient.EjerciciosCallback() {
            /**
             * Callback que recibe la lista de ejercicios de la API y actualiza el RecyclerView en el hilo principal.
             *
             * @param ejercicios Lista de objetos {@link ejerciciomuscle} devuelta por el cliente API.
             */
            @Override
            public void onResponse(List<ejerciciomuscle> ejercicios) {
                runOnUiThread(() -> {
                    adapter = new ejerciciosadapter(ejercicios, ejercicioslistaactivity.this);
                    rvEjercicios.setAdapter(adapter);
                });
            }

            /**
             * Callback que captura errores de red o de procesamiento en la peticion HTTP.
             *
             * @param error Mensaje descriptivo con el detalle del fallo.
             */
            @Override
            public void onFailure(String error) {
                Log.e("GYMZY_API", "Error: " + error);
            }
        });

        btnVolver.setOnClickListener(v -> finish());
    }

    /**
     * Traduce los nombres de los grupos musculares del espanol al ingles
     * para que coincidan con las claves aceptadas por los endpoints de MuscleWiki.
     *
     * @param esp Nombre del musculo en espanol proveniente de la interfaz.
     * @return Cadena de texto equivalente en ingles compatible con la API.
     */
    private String mapearMusculo(String esp) {
        if (esp == null) return "chest";
        switch (esp) {
            case "Pecho": return "chest";
            case "Espalda": return "back";
            case "Hombros": return "shoulders";
            case "Bíceps": return "biceps";
            case "Tríceps": return "triceps";
            case "Piernas": return "quads";
            case "Glúteos": return "glutes";
            case "Abdominales": return "abs";
            default: return esp.toLowerCase();
        }
    }
}