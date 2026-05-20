package com.example.gymzy.general.pantallasprincipales;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gymzy.R;
import com.example.gymzy.general.api.traductor.traductormlkit;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Actividad que gestiona y despliega el registro historico de marcas de un ejercicio concreto.
 * Consume datos desde Cloud Firestore ordenados cronologicamente e integra el servicio traductormlkit
 * para renderizar los titulos traducidos en tiempo de ejecucion.
 */
public class historialejercicioactivity extends AppCompatActivity {

    private TextView tvTitulo, tvVacio;
    private RecyclerView rvHistorial;
    private Button btnVolver;

    private FirebaseFirestore db;
    private historialadaptador adapter;
    private List<Map<String, Object>> listaDatos;
    private String nombreEjercicioIngles;

    /**
     * Inicializa los componentes graficos, prepara el adaptador para el RecyclerView
     * y procesa los parametros del Intent para activar la traduccion y la busqueda en Firestore.
     *
     * @param savedInstanceState Contiene el estado previo de los datos de la interfaz.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_ejercicio);

        db = FirebaseFirestore.getInstance();
        listaDatos = new ArrayList<>();

        // 1. Vincular vistas
        btnVolver = findViewById(R.id.btnVolverHistorial);
        tvTitulo = findViewById(R.id.tvTituloHistorial);
        tvVacio = findViewById(R.id.tvHistorialVacio);
        rvHistorial = findViewById(R.id.rvHistorial);

        // 2. Configurar RecyclerView
        rvHistorial.setLayoutManager(new LinearLayoutManager(this));
        adapter = new historialadaptador(listaDatos);
        rvHistorial.setAdapter(adapter);

        // 3. Obtener el nombre clave desde el Intent
        nombreEjercicioIngles = getIntent().getStringExtra("ejercicio_nombre");

        if (nombreEjercicioIngles != null) {
            // Traducimos el nombre para ponerlo en el título superior
            traductormlkit.traducir(nombreEjercicioIngles, textoTraducido -> {
                tvTitulo.setText("Historial: " + textoTraducido);
            });

            // Consultar datos en Firestore
            cargarHistorialDesdeFirestore();
        } else {
            Toast.makeText(this, "Error: No se especificó el ejercicio", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Evento volver
        btnVolver.setOnClickListener(v -> finish());
    }

    /**
     * Recupera de Cloud Firestore los documentos que coincidan con el UID del usuario logueado
     * y la clave del ejercicio actual, ordenandolos por estampa de tiempo descendente.
     */
    private void cargarHistorialDesdeFirestore() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;
        String uId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Query: Filtra por usuarioId, filtra por ejercicio y ordena por fecha de manera descendente
        db.collection("Historial")
                .whereEqualTo("usuarioId", uId)
                .whereEqualTo("ejercicio", nombreEjercicioIngles.toLowerCase().trim())
                .orderBy("fechaMillis", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listaDatos.clear();

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        listaDatos.add(doc.getData());
                    }

                    // Notificar cambios o mostrar pantalla vacía
                    if (listaDatos.isEmpty()) {
                        tvVacio.setVisibility(View.VISIBLE);
                        rvHistorial.setVisibility(View.GONE);
                    } else {
                        tvVacio.setVisibility(View.GONE);
                        rvHistorial.setVisibility(View.VISIBLE);
                        adapter.notifyDataSetChanged(); // Refresco reactivo de UI
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al cargar historial: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}