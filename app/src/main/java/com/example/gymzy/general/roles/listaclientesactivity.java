package com.example.gymzy.general.roles;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gymzy.R;
import com.example.gymzy.general.usuarios.usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

/**
 * Actividad que despliega la lista de clientes asociados al profesional logueado.
 * Carga los usuarios desde Firestore filtrando por el codigo de vinculacion.
 */
public class listaclientesactivity extends AppCompatActivity {

    private TextView tvTituloHeader;
    private ImageView ivBtnAtras;
    private RecyclerView rvClientes;
    private FirebaseFirestore db;
    private List<usuario> listaclientes;
    private clientesadapter adapter;

    /**
     * Inicializa la actividad, configura el RecyclerView, los listeners de navegacion
     * y arranca la comprobacion de credenciales y roles.
     *
     * @param savedInstanceState Contiene el estado previo de la actividad si existiera.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_clientes);

        tvTituloHeader = findViewById(R.id.tvTituloActivityLista);
        ivBtnAtras = findViewById(R.id.ivAtrasLista);

        rvClientes = new RecyclerView(this);
        rvClientes.setLayoutParams(new RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.MATCH_PARENT
        ));
        rvClientes.setPadding(8, 8, 8, 8);
        rvClientes.setClipToPadding(false);

        findViewById(R.id.contenedorListaClientes);

        db = FirebaseFirestore.getInstance();
        listaclientes = new ArrayList<>();

        rvClientes.setLayoutManager(new LinearLayoutManager(this));
        adapter = new clientesadapter(listaclientes);
        rvClientes.setAdapter(adapter);

        ivBtnAtras.setOnClickListener(v -> finish());

        verificarRolYBuscarClientes();
    }

    /**
     * Valida la sesion del profesional en Firebase y obtiene su rol y codigo de vinculacion
     * para adaptar el encabezado de la pantalla.
     */
    private void verificarRolYBuscarClientes() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(this, "Sesión no válida", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String uidPro = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("Usuarios").document(uidPro).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String rol = documentSnapshot.getString("rol");
                        String codigoPro = documentSnapshot.getString("codigoVinculacion");

                        if (tvTituloHeader != null && rol != null) {
                            if ("Nutricionista".equalsIgnoreCase(rol)) {
                                tvTituloHeader.setText("MIS PACIENTES");
                            } else if ("Entrenador".equalsIgnoreCase(rol)) {
                                tvTituloHeader.setText("MIS ALUMNOS");
                            }
                        }

                        if (codigoPro != null && !codigoPro.isEmpty()) {
                            obtenerUsuariosVinculados(codigoPro);
                        } else {
                            Toast.makeText(this, "No posees un código de vinculación activo", Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error de conexión: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Consulta Firestore para extraer los usuarios con rol "Usuario" vinculados al codigo dado
     * e inyecta los resultados en el adaptador del RecyclerView.
     *
     * @param codigoVinculacion Codigo identificador del profesional para realizar el filtro.
     */
    private void obtenerUsuariosVinculados(String codigoVinculacion) {
        db.collection("Usuarios")
                .whereEqualTo("codigoVinculacion", codigoVinculacion)
                .whereEqualTo("rol", "Usuario")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listaclientes.clear();

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        usuario cliente = doc.toObject(usuario.class);
                        listaclientes.add(cliente);
                    }

                    adapter.notifyDataSetChanged();

                    if (listaclientes.isEmpty()) {
                        Toast.makeText(this, "No tienes usuarios registrados con tu código aún", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al procesar la lista: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}