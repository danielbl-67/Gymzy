package com.example.gymzy.general.roles;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gymzy.R;
import com.example.gymzy.general.pantallasprincipales.menuinferior;
import com.example.gymzy.general.usuarios.usuario;
import com.example.gymzy.general.sesion.autenticacion;
import com.example.gymzy.general.pantallasprincipales.registrarsesiones;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Actividad que representa el panel de control unificado para los perfiles profesionales.
 * Hereda de {@link menuinferior} y adapta dinámicamente su interfaz gráfica (textos, iconos y títulos)
 * en tiempo de ejecución para actuar como el entorno de trabajo específico de un "Nutricionista"
 * o un "Entrenador", permitiendo la gestión de alumnos/pacientes y la asignación de planes.
 */
public class panelprofesionalactivity extends menuinferior {

    private TextView tvRolBadge, tvCodigoPro, tvTextoAlumnos, tvTextoPlanes;
    private ImageView iconUsers, iconEdit, ivProLogout;
    private MaterialCardView btnGestionarAlumnos, btnSubirPlanes;
    private View btnsalir;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private registrarsesiones sessionManager;
    private String rolUsuario = "";

    /**
     * Metodo de ciclo de vida que inicializa el panel profesional.
     * Infla el diseño XML personalizado de la pantalla inicial del profesional,
     * inicializa los gestores de datos locales y asíncronos, y valida que el usuario mantenga
     * un token de sesión web activo antes de construir el panel.
     *
     * @param savedInstanceState Si la actividad se recrea, este objeto contiene los datos del estado previo.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflamos la vista correspondiente a los profesionales
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
     * Enlaza y mapea las variables globales de la clase con sus respectivos
     * componentes e identificadores visuales definidos en el archivo de diseño XML.
     */
    private void initUI() {
        tvRolBadge = findViewById(R.id.tvRolBadge);
        tvCodigoPro = findViewById(R.id.tvCodigoPro);
        tvTextoAlumnos = findViewById(R.id.tvTextoAlumnos);
        tvTextoPlanes = findViewById(R.id.tvTextoPlanes);
        iconUsers = findViewById(R.id.iconUsers);
        iconEdit = findViewById(R.id.iconEdit);
        btnGestionarAlumnos = findViewById(R.id.btnGestionarAlumnos);
        btnSubirPlanes = findViewById(R.id.btnSubirPlanes);
        ivProLogout = findViewById(R.id.ivProLogout);
        btnsalir = findViewById(R.id.btnCerrarSesion);
    }

    /**
     * Recupera el documento del profesional desde la colección "Usuarios" en Cloud Firestore
     * mediante su identificador único (UID). Extrae el código de vinculación y el rol para
     * dar paso a la personalización de la interfaz.
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
     * Modifica los componentes gráficos en tiempo de ejecución de acuerdo al rol del profesional.
     * <ul>
     *   <li>Si es **Nutricionista**: Configura el badge como "PANEL NUTRICIONAL" y orienta las funciones a pacientes y dietas.</li>
     *   <li>Si es **Entrenador**: Configura el badge como "PANEL DE ENTRENAMIENTO" y orienta las funciones a alumnos y rutinas.</li>
     * </ul>
     */
    private void configurarInterfazSegunRol() {
        if ("Nutricionista".equalsIgnoreCase(rolUsuario)) {
            tvRolBadge.setText("PANEL NUTRICIONAL");
            tvTextoAlumnos.setText("Lista de Pacientes");
            tvTextoPlanes.setText("Asignar Dietas / Menús");
            if (iconUsers != null) iconUsers.setImageResource(android.R.drawable.ic_menu_agenda);
            if (iconEdit != null) iconEdit.setImageResource(android.R.drawable.ic_menu_today);

        } else if ("Entrenador".equalsIgnoreCase(rolUsuario)) {
            tvRolBadge.setText("PANEL DE ENTRENAMIENTO");
            tvTextoAlumnos.setText("Lista de Alumnos");
            tvTextoPlanes.setText("Asignar Rutinas / Ejercicios");
            if (iconUsers != null) iconUsers.setImageResource(android.R.drawable.ic_menu_myplaces);
            if (iconEdit != null) iconEdit.setImageResource(android.R.drawable.ic_menu_edit);
        }
    }

    /**
     * Define y asigna las acciones de respuesta para los eventos de clic en los botones del panel.
     * Gestiona la navegación hacia la lista de clientes independientes, el creador de planes
     * y los disparadores para el cierre de sesión seguro.
     */
    private void setupClickListeners() {
        btnGestionarAlumnos.setOnClickListener(v -> {
            // ⚡ SOLUCIÓN: Cambiamos el fragmento roto por la Activity independiente escrita en minúsculas
            Intent intent = new Intent(panelprofesionalactivity.this, listaclientesactivity.class);
            startActivity(intent);
        });

        btnSubirPlanes.setOnClickListener(v -> {
            String mensaje = "Nutricionista".equalsIgnoreCase(rolUsuario) ? "Dietas" : "Rutinas";
            Toast.makeText(this, "Asignador de " + mensaje, Toast.LENGTH_SHORT).show();
        });

        // Icono de la Toolbar (Si existe arriba a la derecha)
        if (ivProLogout != null) {
            ivProLogout.setOnClickListener(v -> ejecutarCerrarSesion());
        }

        if (btnsalir != null) {
            btnsalir.setOnClickListener(v -> ejecutarCerrarSesion());
        } else {
            android.util.Log.e("GYMZY_ERROR", "No se encontró el ID btnCerrarSesion en el archivo XML");
        }
    }

    /**
     * Destruye el estado de sesión actual del usuario tanto en los servicios de infraestructura
     * distribuidos de Firebase Auth como en las cachés y preferencias de almacenamiento local
     * del dispositivo móvil.
     */
    private void ejecutarCerrarSesion() {
        mAuth.signOut();
        sessionManager.logout();
        irALogin();
    }

    /**
     * Levanta la pantalla de autenticación del sistema y purga por completo el histórico
     * y la pila de ejecución de actividades del sistema operativo para evitar retrocesos accidentales.
     */
    private void irALogin() {
        Intent intent = new Intent(panelprofesionalactivity.this, autenticacion.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}