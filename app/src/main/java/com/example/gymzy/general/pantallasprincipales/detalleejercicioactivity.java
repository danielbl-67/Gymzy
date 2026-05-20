package com.example.gymzy.general.pantallasprincipales;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.gymzy.R;
import com.example.gymzy.general.api.traductor.traductormlkit;

public class detalleejercicioactivity extends AppCompatActivity {

    private TextView tvTitulo, tvDescripcion;
    private ImageView imgEjercicio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_ejercicio);

        tvTitulo = findViewById(R.id.tituloEjercicio);
        tvDescripcion = findViewById(R.id.descripcionEjercicio);
        imgEjercicio = findViewById(R.id.imgEjercicioDetalle);
        Button btnVolver = findViewById(R.id.buttonVolver);

        String nombreIn = getIntent().getStringExtra("nombre");
        String descIn = getIntent().getStringExtra("descripcion");
        String imgUrl = getIntent().getStringExtra("imagen");

        // Traducir Nombre
        tvTitulo.setText(nombreIn);
        traductormlkit.traducir(nombreIn, texto -> tvTitulo.setText(texto));

        // Traducir Descripción
        tvDescripcion.setText("Traduciendo instrucciones...");
        traductormlkit.traducir(descIn, texto -> tvDescripcion.setText(texto));

        Glide.with(this).load(imgUrl).into(imgEjercicio);

        btnVolver.setOnClickListener(v -> finish());
    }
}