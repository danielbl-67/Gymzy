package com.example.gymzy.general.PantallasPrincipales;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.gymzy.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class DetalleEjercicioActivity extends AppCompatActivity {

    private TextView tvTitulo, tvDescripcion;
    private ImageView imgEjercicio;
    private EditText etSeries, etReps, etPeso;
    private Button btnGuardar, btnVolver, btnLimpiar;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_ejercicio);

        // Inicializar Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // 1. Vincular vistas con los IDs de tu XML
        btnVolver = findViewById(R.id.buttonVolver);
        tvTitulo = findViewById(R.id.tituloEjercicio);
        imgEjercicio = findViewById(R.id.imgEjercicioDetalle);
        tvDescripcion = findViewById(R.id.descripcionEjercicio);

        // IDs de los EditText corregidos para coincidir con tu XML (editSeries, editReps, editPeso)
        etSeries = findViewById(R.id.editSeries);
        etReps = findViewById(R.id.editReps);
        etPeso = findViewById(R.id.editPeso);

        // IDs de los botones corregidos según tu XML (buttonReproducir para Guardar, buttonPausar para Limpiar)
        btnGuardar = findViewById(R.id.buttonReproducir);
        btnLimpiar = findViewById(R.id.buttonPausar);

        // 2. Recuperar datos enviados desde la actividad anterior a través del Intent
        String nombre = getIntent().getStringExtra("nombre");
        String imagenUrl = getIntent().getStringExtra("imagen"); // Esta URL debe apuntar al GIF
        String desc = getIntent().getStringExtra("descripcion");

        // 3. Asignar los textos recuperados a los TextViews
        if (nombre != null) {
            tvTitulo.setText(nombre);
        }
        if (desc != null) {
            tvDescripcion.setText(desc);
        }

        // 4. ÚNICA CARGA DEL VIDEO (GIF) USANDO GLIDE
        // Esta es la parte crítica que hemos arreglado. He añadido estrategias de caché
        // para mejorar el rendimiento al reproducir animaciones.
        if (imagenUrl != null) {
            Glide.with(this)
                    .asGif() // Indicamos explícitamente que trate la carga como un GIF animado
                    .load(imagenUrl)
                    .placeholder(R.drawable.ic_logoredondo) // Imagen opcional mientras carga el video
                    .error(R.drawable.background)        // Imagen o color opcional si falla la carga del video
                    .diskCacheStrategy(DiskCacheStrategy.ALL) // Almacena tanto la imagen original como las transformadas para rapidez
                    .into(imgEjercicio);
        } else {
            // Manejo de caso opcional si no llega URL de imagen
            imgEjercicio.setImageResource(R.drawable.background);
        }

        // --- CONFIGURACIÓN DE LOS EVENTOS DE LOS BOTONES ---

        // Botón Volver: cierra la actividad actual para regresar a la anterior
        btnVolver.setOnClickListener(v -> finish());

        // Botón Limpiar: borra el texto de los campos de entrada para el registro
        btnLimpiar.setOnClickListener(v -> {
            etSeries.setText("");
            etReps.setText("");
            etPeso.setText("");
            Toast.makeText(this, "Campos limpiados", Toast.LENGTH_SHORT).show();
        });

        // Botón Guardar: inicia el proceso para guardar el progreso en Firebase Firestore
        btnGuardar.setOnClickListener(v -> {
            if (nombre != null) {
                guardarEnFirebase(nombre);
            } else {
                Toast.makeText(this, "Error: Nombre de ejercicio no disponible", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Método para recopilar los datos introducidos y guardarlos en una colección "Historial" en Firestore.
     * @param nombreEj El nombre del ejercicio actual para registrarlo.
     */
    private void guardarEnFirebase(String nombreEj) {
        // Obtener los valores de los EditText, eliminando espacios en blanco extra
        String s = etSeries.getText().toString().trim();
        String r = etReps.getText().toString().trim();
        String p = etPeso.getText().toString().trim();

        // Validación: Obtener el ID del usuario actual de Firebase Auth. Si es nulo, no se puede guardar.
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(this, "Error: Usuario no autenticado", Toast.LENGTH_SHORT).show();
            return;
        }
        String uId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Validación básica: verificar que ningún campo obligatorio esté vacío
        if (s.isEmpty() || r.isEmpty() || p.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos para registrar la sesión", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear el mapa de datos (objeto clave-valor) para enviarlo a Firestore
        Map<String, Object> data = new HashMap<>();
        data.put("ejercicio", nombreEj);
        data.put("series", s);
        data.put("reps", r);
        data.put("peso", p);
        data.put("fechaMillis", System.currentTimeMillis()); // Guardamos la fecha actual en milisegundos para fácil ordenación
        data.put("usuarioId", uId); // Asociamos el registro con el ID del usuario

        // Guardar en Firebase Firestore dentro de la colección "Historial"
        db.collection("Historial")
                .add(data)
                .addOnSuccessListener(doc -> {
                    // Si se guarda con éxito
                    Toast.makeText(this, "¡Progreso de " + nombreEj + " guardado!", Toast.LENGTH_SHORT).show();
                    // Opcional: limpiar campos tras guardar exitosamente
                    etSeries.setText("");
                    etReps.setText("");
                    etPeso.setText("");
                    // finish(); // Descomenta esta línea si quieres volver automáticamente a la lista tras guardar
                })
                .addOnFailureListener(e -> {
                    // Si ocurre un error durante el guardado
                    Toast.makeText(this, "Error al guardar el progreso: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}