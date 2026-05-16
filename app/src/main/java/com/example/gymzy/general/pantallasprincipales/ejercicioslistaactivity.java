package com.example.gymzy.general.pantallasprincipales;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
        setContentView(R.layout.activity_ejercicios_lista); // TU XML

        rvEjercicios = findViewById(R.id.rvEjerciciosGenericos);
        tvTitulo = findViewById(R.id.tvTituloCategoria);
        btnVolver = findViewById(R.id.btnVolverLista);
        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ejercicioslistaactivity.this, homeactivity.class);
                startActivity(intent);
            }
        });
        String musculoSeleccionado = getIntent().getStringExtra("TITULO_MUSCULO");
        tvTitulo.setText(musculoSeleccionado);

        rvEjercicios.setLayoutManager(new LinearLayoutManager(this));

        // Traducción para la búsqueda (El JSON está en inglés)
        String musculoBusqueda = traducir(musculoSeleccionado);

        musclewikiclient.getEjerciciosPorMusculo(musculoBusqueda, new musclewikiclient.EjerciciosCallback() {
            @Override
            public void onResponse(List<ejerciciomuscle> ejercicios) {
                runOnUiThread(() -> {
                    adapter = new ejerciciosadapter(ejercicios, ejercicioslistaactivity.this);
                    rvEjercicios.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                });
            }

            @Override
            public void onFailure(String error) {
                Log.e("GYMZY_API", "Fallo: " + error);
            }
        });
    }

    private String traducir(String esp) {
        switch (esp) {
            case "Pecho": return "chest";
            case "Espalda": return "back";
            case "Hombros": return "shoulders";
            case "Bíceps": return "biceps";
            case "Tríceps": return "triceps";
            case "Piernas": return "quads";
            case "Glúteos": return "glutes";
            case "Abdominales": return "abs";
            default: return esp;
        }
    }
}