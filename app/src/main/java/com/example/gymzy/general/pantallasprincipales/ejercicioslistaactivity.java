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

public class ejercicioslistaactivity extends AppCompatActivity {

    private RecyclerView rvEjercicios;
    private TextView tvTitulo;
    private ImageButton btnVolver;
    private ejerciciosadapter adapter;

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

        // Mapeamos para la API
        String musculoParaApi = mapearMusculo(musculoSeleccionado);

        musclewikiclient.getEjerciciosPorMusculo(musculoParaApi, new musclewikiclient.EjerciciosCallback() {
            @Override
            public void onResponse(List<ejerciciomuscle> ejercicios) {
                runOnUiThread(() -> {
                    adapter = new ejerciciosadapter(ejercicios, ejercicioslistaactivity.this);
                    rvEjercicios.setAdapter(adapter);
                });
            }

            @Override
            public void onFailure(String error) {
                Log.e("GYMZY_API", "Error: " + error);
            }
        });

        btnVolver.setOnClickListener(v -> finish());
    }

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