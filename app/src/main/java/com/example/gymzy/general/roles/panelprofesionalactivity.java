package com.example.gymzy.general.roles;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gymzy.R;
import com.example.gymzy.general.pantallasprincipales.menuinferior;
import com.example.gymzy.general.usuarios.usuario;
import com.example.gymzy.general.sesion.autenticacion;
import com.example.gymzy.general.pantallasprincipales.registrarsesiones;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Actividad que representa el panel de control unificado y limpio para los perfiles profesionales.
 * Hereda de {@link menuinferior} y adapta dinámicamente su interfaz gráfica en tiempo de ejecución.
 * Muestra el código de verificación/vinculación y delega las listas de navegación al menú inferior.
 * * @author Gymzy Team
 * @version 2.0
 */
public class panelprofesionalactivity extends menuinferior {

    private TextView tvRolBadge, tvCodigoPro;
    private View btnsalir;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private registrarsesiones sessionManager;
    private String rolUsuario = "";

    /**
     * Método de ciclo de vida que inicializa el panel profesional.
     * Infla el diseño XML limpio centrado en el código de vinculación.
     *
     * @param savedInstanceState Si la actividad se recrea, contiene los datos del estado previo.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflamos la vista correspondiente al home del profesional simplificado
        View view = getLayoutInflater().inflate(R.layout.layout_home_profesional, null);
        setContentView(view);
        allocateActivityTitle("Panel Profesional");

        // Inicializar Gestores de Base de datos y Sesión local
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        sessionManager = new registrarsesiones(this);

        initUI();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            irALogin();
            return;
        }

        cargarDatosProfesional(user.getUid());
        setupClickListeners();
    }

    /**
     * Enlaza y mapea las variables globales exclusivamente con los componentes del diseño limpio.
     */
    private void initUI() {
        tvRolBadge = findViewById(R.id.tvRolBadge);
        tvCodigoPro = findViewById(R.id.tvCodigoPro);
        btnsalir = findViewById(R.id.btnCerrarSesion);
    }

    /**
     * Recupera el documento del profesional desde Cloud Firestore para pintar
     * su código de vinculación y definir el rol.
     *
     * @param uid Identificador único del usuario autenticado en Firebase.
     */
    private void cargarDatosProfesional(String uid) {
        db.collection("Usuarios").document(uid)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        usuario pro = doc.toObject(usuario.class);
                        if (pro != null && pro.rol != null) {
                            rolUsuario = pro.rol.trim();

                            if (tvCodigoPro != null && pro.codigoVinculacion != null) {
                                tvCodigoPro.setText(pro.codigoVinculacion);
                            }
                            configurarInterfazSegunRol();
                        }
                    } else {
                        Toast.makeText(this, "No se encontraron datos de profesional", Toast.LENGTH_SHORT).show();
                        irALogin();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error de conexión: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Modifica las etiquetas del encabezado informativo central de acuerdo al rol.
     */
    private void configurarInterfazSegunRol() {
        if (tvRolBadge != null) {
            if ("Nutricionista".equalsIgnoreCase(rolUsuario)) {
                tvRolBadge.setText("PANEL NUTRICIONAL");
            } else if ("Entrenador".equalsIgnoreCase(rolUsuario)) {
                tvRolBadge.setText("PANEL DE ENTRENAMIENTO");
            }
        }
    }

    /**
     * Asigna la respuesta de evento de clic para el botón de cerrar sesión.
     */
    private void setupClickListeners() {
        if (btnsalir != null) {
            btnsalir.setOnClickListener(v -> ejecutarCerrarSesion());
        } else {
            android.util.Log.e("GYMZY_ERROR", "No se encontró el ID btnCerrarSesion en el archivo XML");
        }
    }

    /**
     * Limpia las credenciales y tokens tanto en Firebase como en las preferencias locales.
     */
    private void ejecutarCerrarSesion() {
        mAuth.signOut();
        sessionManager.logout();
        irALogin();
    }

    /**
     * Redirige hacia el flujo de autenticación purgando la pila de actividades.
     */
    private void irALogin() {
        Intent intent = new Intent(panelprofesionalactivity.this, autenticacion.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}