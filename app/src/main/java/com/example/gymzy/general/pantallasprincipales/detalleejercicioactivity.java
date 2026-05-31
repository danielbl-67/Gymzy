package com.example.gymzy.general.pantallasprincipales;

import android.content.Intent; // <-- Asegúrate de importar Intent
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.gymzy.R;
import com.example.gymzy.general.api.traductor.traductormlkit;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class detalleejercicioactivity extends AppCompatActivity {

    private TextView tvTitulo, tvDescripcion;
    private ImageView imgEjercicio;
    private Button btnVolver, btnGuardarMarca, btnLimpiar, btnHistorial;
    private EditText etSeries, etReps, etPeso; // <-- Declaramos los inputs de marcas

    private String nombreIn;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_ejercicio);

        db = FirebaseFirestore.getInstance();

        // 1. Vincular componentes del XML
        tvTitulo = findViewById(R.id.tituloEjercicio);
        tvDescripcion = findViewById(R.id.descripcionEjercicio);
        imgEjercicio = findViewById(R.id.imgEjercicioDetalle);
        btnVolver = findViewById(R.id.buttonVolver);
        btnHistorial = findViewById(R.id.buttonHistorial); // <-- Botón historial asignado

        btnGuardarMarca = findViewById(R.id.buttonReproducir); // Tu botón XML "Guardar"
        btnLimpiar = findViewById(R.id.buttonPausar);        // Tu botón XML "Limpiar"

        etSeries = findViewById(R.id.editSeries);
        etReps = findViewById(R.id.editReps);
        etPeso = findViewById(R.id.editPeso);

        // 2. Recuperar datos del Intent
        nombreIn = getIntent().getStringExtra("nombre");
        String descIn = getIntent().getStringExtra("descripcion");
        String imgUrl = getIntent().getStringExtra("imagen");

        // 3. Traducir textos y cargar imagen
        tvTitulo.setText(nombreIn);
        traductormlkit.traducir(nombreIn, texto -> tvTitulo.setText(texto));

        tvDescripcion.setText("Traduciendo instrucciones...");
        traductormlkit.traducir(descIn, texto -> tvDescripcion.setText(texto));

        Glide.with(this).load(imgUrl).into(imgEjercicio);

        // 4. Configurar eventos de click

        // GUARDAR EN FIRESTORE
        btnGuardarMarca.setOnClickListener(v -> guardarProgresoEnFirestore());

        // LIMPIAR CAMPOS
        btnLimpiar.setOnClickListener(v -> limpiarCampos());

        // IR AL HISTORIAL
        btnHistorial.setOnClickListener(v -> {
            if (nombreIn != null) {
                Intent intent = new Intent(detalleejercicioactivity.this, historialejercicioactivity.class);
                // Pasamos la clave bajo la misma etiqueta "ejercicio_nombre" que espera recibir el historial
                intent.putExtra("ejercicio_nombre", nombreIn);
                startActivity(intent);
            } else {
                Toast.makeText(this, "No se puede abrir el historial sin el nombre del ejercicio", Toast.LENGTH_SHORT).show();
            }
        });

        btnVolver.setOnClickListener(v -> finish());
    }

    /**
     * Registra una nueva marca/entrenamiento en la colección "Historial" recogiendo los inputs del usuario.
     */
    private void guardarProgresoEnFirestore() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        // Obtener lo que el usuario ha escrito
        String seriesText = etSeries.getText().toString().trim();
        String repsText = etReps.getText().toString().trim();
        String pesoText = etPeso.getText().toString().trim();

        // Validación básica: evitar campos vacíos
        if (seriesText.isEmpty() || repsText.isEmpty() || pesoText.isEmpty()) {
            Toast.makeText(this, "Por favor, rellena todos los campos de marcas", Toast.LENGTH_SHORT).show();
            return;
        }

        String uId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String ejercicioClave = nombreIn.toLowerCase().trim();
        long fechaActualMillis = System.currentTimeMillis();

        // Creamos el mapa con la información exacta que maneja tu app
        Map<String, Object> registro = new HashMap<>();
        registro.put("usuarioId", uId);
        registro.put("ejercicio", ejercicioClave);
        registro.put("fechaMillis", fechaActualMillis);

        // Guardamos los datos introducidos (puedes convertirlos a Integer/Double si prefieres guardarlos numéricamente)
        registro.put("series", Integer.parseInt(seriesText));
        registro.put("repeticiones", Integer.parseInt(repsText));
        registro.put("peso", Double.parseDouble(pesoText));

        // Guardar en la colección "Historial"
        db.collection("Historial")
                .add(registro)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(detalleejercicioactivity.this, "¡Progreso guardado correctamente!", Toast.LENGTH_SHORT).show();
                    limpiarCampos(); // Se limpian automáticamente los inputs tras guardar con éxito
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(detalleejercicioactivity.this, "Error al guardar: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    /**
     * Resetea el texto de los campos numéricos de marcas
     */
    private void limpiarCampos() {
        etSeries.setText("");
        etReps.setText("");
        etPeso.setText("");
    }
}