package com.example.gymzy.general.pantallasprincipales;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.gymzy.R;
import com.example.gymzy.general.api.traductor.traductormlkit;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class detalleejercicioactivity extends AppCompatActivity {

    private TextView tvTitulo, tvDescripcion;
    private ImageView imgEjercicio;
    private EditText etSeries, etReps, etPeso;
    private Button btnGuardar, btnVolver, btnLimpiar, btnHistorial; // ⚡ Añadido btnHistorial
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_ejercicio);

        // Inicializar Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // 1. Vincular vistas con los IDs del XML
        btnVolver = findViewById(R.id.buttonVolver);
        btnHistorial = findViewById(R.id.buttonHistorial); // ⚡ Vinculamos el botón de historial
        tvTitulo = findViewById(R.id.tituloEjercicio);
        imgEjercicio = findViewById(R.id.imgEjercicioDetalle);
        tvDescripcion = findViewById(R.id.descripcionEjercicio);

        etSeries = findViewById(R.id.editSeries);
        etReps = findViewById(R.id.editReps);
        etPeso = findViewById(R.id.editPeso);

        btnGuardar = findViewById(R.id.buttonReproducir);
        btnLimpiar = findViewById(R.id.buttonPausar);

        // 2. Recuperar datos enviados mediante el Intent
        String nombre = getIntent().getStringExtra("nombre");
        String imagenUrl = getIntent().getStringExtra("imagen");
        String desc = getIntent().getStringExtra("descripcion");

        // 3. Asignar los textos recuperados aplicando traducción inteligente de Google
        if (nombre != null) {
            traductormlkit.traducir(nombre, textoTraducido -> {
                tvTitulo.setText(textoTraducido);
            });
        }

        if (desc != null) {
            tvDescripcion.setText("Traduciendo instrucciones...");
            traductormlkit.traducir(desc, textoTraducido -> {
                tvDescripcion.setText(textoTraducido);
            });
        }

        // 4. ⚡ CARGA INTELIGENTE Y REPRODUCCIÓN EN MOVIMIENTO CONTINUO
        if (imagenUrl != null && !imagenUrl.isEmpty()) {
            Log.d("GYMZY_MEDIA", "Descargando animación desde: " + imagenUrl);

            Glide.with(this)
                    .load(imagenUrl)
                    .placeholder(R.drawable.ic_logoredondo)
                    .error(R.drawable.background)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imgEjercicio);
        } else {
            imgEjercicio.setImageResource(R.drawable.background);
        }

        // --- CONFIGURACIÓN DE LOS EVENTOS DE LOS BOTONES ---

        btnVolver.setOnClickListener(v -> finish());

        btnLimpiar.setOnClickListener(v -> {
            etSeries.setText("");
            etReps.setText("");
            etPeso.setText("");
            Toast.makeText(this, "Campos limpiados", Toast.LENGTH_SHORT).show();
        });

        btnGuardar.setOnClickListener(v -> {
            if (nombre != null) {
                guardarEnFirebase(nombre);
            } else {
                Toast.makeText(this, "Error: Nombre de ejercicio no disponible", Toast.LENGTH_SHORT).show();
            }
        });

        // ⚡ ACCIÓN DEL NUEVO BOTÓN: Redirige a la pantalla de registros filtrando por este ejercicio
        btnHistorial.setOnClickListener(v -> {
            if (nombre != null) {
                // Modifica 'HistorialEjercicioActivity.class' por el nombre de tu actividad de historial real si difiere
                Intent intent = new Intent(detalleejercicioactivity.this, historialejercicioactivity.class);
                intent.putExtra("ejercicio_nombre", nombre); // Enviamos la clave para el query de Firestore
                startActivity(intent);
            } else {
                Toast.makeText(this, "No se puede abrir el historial sin un nombre de ejercicio válido", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void guardarEnFirebase(String nombreEj) {
        String s = etSeries.getText().toString().trim();
        String r = etReps.getText().toString().trim();
        String p = etPeso.getText().toString().trim();

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(this, "Error: Usuario no autenticado", Toast.LENGTH_SHORT).show();
            return;
        }
        String uId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (s.isEmpty() || r.isEmpty() || p.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos para registrar la sesión", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> data = new HashMap<>();

        // ⚡ CLAVE DE LA SOLUCIÓN: Forzamos minúsculas al guardar en la base de datos
        data.put("ejercicio", nombreEj.toLowerCase().trim());

        data.put("series", s);
        data.put("reps", r);
        data.put("peso", p);
        data.put("fechaMillis", System.currentTimeMillis()); // Guarda el número limpio
        data.put("usuarioId", uId);

        db.collection("Historial")
                .add(data)
                .addOnSuccessListener(doc -> {
                    Toast.makeText(this, "¡Progreso guardado!", Toast.LENGTH_SHORT).show();
                    etSeries.setText("");
                    etReps.setText("");
                    etPeso.setText("");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al guardar: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}