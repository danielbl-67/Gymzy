package com.example.gymzy.general.sesion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.gymzy.R;
import com.example.gymzy.general.pantallasprincipales.homeactivity;
import com.example.gymzy.general.roles.paneladminactivity;
import com.example.gymzy.general.roles.*;
import com.example.gymzy.general.usuarios.usuario;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Activity encargada del registro y configuración inicial del perfil de usuario.
 * Permite capturar datos personales, físicos y de rol (Usuario, Nutricionista, Entrenador, Admin)
 * adaptando dinámicamente el formulario visible y guardando la información en Firebase Firestore.
 */
public class registroactivity extends AppCompatActivity {
    private TextInputEditText etNom, etEd, etPe, etAl, etCodPro;
    private AutoCompleteTextView spGen, spObj, spAct, spRol;
    private MaterialButton btnFin;
    private FirebaseFirestore db;
    private TextInputLayout layEdad, layPeso, layAltura, layGenero, layObjetivo, layActividad, layCodPro;

    /**
     * Método de ciclo de vida que inicializa la actividad, vincula las vistas del XML,
     * configura los menús desplegables (spinners) y establece los listeners de eventos.
     *
     * @param savedInstanceState Si la actividad se vuelve a inicializar después de cerrarse previamente,
     *                           este Bundle contiene los datos que suministró más recientemente.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        db = FirebaseFirestore.getInstance();

        // Vincular componentes de entrada de datos
        etNom = findViewById(R.id.etNombreCompleto);
        etEd = findViewById(R.id.etEdad);
        etPe = findViewById(R.id.etPeso);
        etAl = findViewById(R.id.etAltura);
        etCodPro = findViewById(R.id.etCodigoProfesional);
        spGen = findViewById(R.id.spGenero);
        spObj = findViewById(R.id.spObjetivo);
        spAct = findViewById(R.id.spActividad);
        spRol = findViewById(R.id.spRol);
        btnFin = findViewById(R.id.btnFinalizarRegistro);

        // Vincular los layouts contenedores para gestionar la visibilidad
        layEdad = (TextInputLayout) etEd.getParent().getParent();
        layPeso = (TextInputLayout) etPe.getParent().getParent();
        layAltura = (TextInputLayout) etAl.getParent().getParent();
        layGenero = (TextInputLayout) spGen.getParent().getParent();
        layObjetivo = (TextInputLayout) spObj.getParent().getParent();
        layActividad = (TextInputLayout) spAct.getParent().getParent();
        layCodPro = (TextInputLayout) etCodPro.getParent().getParent();

        setupSpinners();

        // Listener para adaptar el formulario en tiempo real según el rol seleccionado
        spRol.setOnItemClickListener((parent, view, position, id) -> {
            String rolSeleccionado = parent.getItemAtPosition(position).toString();
            adaptarFormularioPorRol(rolSeleccionado);
        });

        btnFin.setOnClickListener(v -> guardarEnFirestore());
    }

    /**
     * Configura y llena los adaptadores de los menús desplegables (AutoCompleteTextView)
     * con las opciones predefinidas para género, objetivos, actividad física y roles del sistema.
     */
    private void setupSpinners() {
        String[] generos = {"Hombre", "Mujer", "Otro"};
        String[] objetivos = {"Perder peso", "Mantener peso", "Ganar masa muscular"};
        String[] actividades = {"Sedentario", "Ligero", "Moderado", "Intenso"};
        String[] roles = {"Usuario", "Nutricionista", "Entrenador", "Admin"};

        spGen.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, generos));
        spObj.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, objetivos));
        spAct.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, actividades));
        spRol.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, roles));
    }

    /**
     * Alterna la visibilidad de los campos del formulario según el rol seleccionado.
     * Si el rol es "Usuario", se exigen las métricas físicas y el código de vinculación.
     * Para roles profesionales o administrativos, se ocultan estas secciones.
     *
     * @param rol El nombre del rol seleccionado por el usuario en el spinner de roles.
     */
    private void adaptarFormularioPorRol(String rol) {
        if (rol.equalsIgnoreCase("Usuario")) {
            layEdad.setVisibility(View.VISIBLE);
            layPeso.setVisibility(View.VISIBLE);
            layAltura.setVisibility(View.VISIBLE);
            layGenero.setVisibility(View.VISIBLE);
            layObjetivo.setVisibility(View.VISIBLE);
            layActividad.setVisibility(View.VISIBLE);
            layCodPro.setVisibility(View.VISIBLE);
        } else {
            layEdad.setVisibility(View.GONE);
            layPeso.setVisibility(View.GONE);
            layAltura.setVisibility(View.GONE);
            layGenero.setVisibility(View.GONE);
            layObjetivo.setVisibility(View.GONE);
            layActividad.setVisibility(View.GONE);
            layCodPro.setVisibility(View.GONE);
        }
    }

    /**
     * Valida los datos ingresados en el formulario y, si todo es correcto, registra el perfil
     * del usuario en la colección "Usuarios" de Firebase Firestore usando su UID de autenticación.
     * <p>
     * Si el rol es profesional (Nutricionista/Entrenador), genera de forma automática un código de
     * vinculación aleatorio. Finalmente, redirige al usuario a su panel correspondiente según su rol.
     */
    private void guardarEnFirestore() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(this, "Sesión no válida", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        String nombre = etNom.getText().toString().trim();
        String rolSeleccionado = spRol.getText().toString().trim();

        if (nombre.isEmpty() || rolSeleccionado.isEmpty()) {
            Toast.makeText(this, "El nombre y el rol son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean esUsuarioComun = rolSeleccionado.equalsIgnoreCase("Usuario");

        String edadStr = etEd.getText().toString().trim();
        String pesoStr = etPe.getText().toString().trim();
        String alturaStr = etAl.getText().toString().trim();
        String genero = spGen.getText().toString().trim();
        String objetivo = spObj.getText().toString().trim();
        String actividad = spAct.getText().toString().trim();
        String codigoVinculacion = etCodPro.getText().toString().trim().toUpperCase();

        if (esUsuarioComun && (edadStr.isEmpty() || pesoStr.isEmpty() || alturaStr.isEmpty() || genero.isEmpty() || objetivo.isEmpty() || actividad.isEmpty())) {
            Toast.makeText(this, "Por favor, completa todo tu perfil físico", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            btnFin.setEnabled(false);

            int edad = esUsuarioComun ? Integer.parseInt(edadStr) : 0;
            double peso = esUsuarioComun ? Double.parseDouble(pesoStr) : 0.0;
            double altura = esUsuarioComun ? Double.parseDouble(alturaStr) : 0.0;

            if (!esUsuarioComun) {
                genero = "N/A";
                objetivo = "N/A";
                actividad = "N/A";

                // Generación automática de código único para perfiles profesionales
                if (rolSeleccionado.equalsIgnoreCase("Nutricionista") || rolSeleccionado.equalsIgnoreCase("Entrenador")) {
                    codigoVinculacion = rolSeleccionado.substring(0, 3).toUpperCase() + "-" + (int) (Math.random() * 9000 + 1000);
                }
            }

            usuario u = new usuario(
                    nombre, edad, peso, altura, genero, objetivo, actividad,
                    rolSeleccionado, email, codigoVinculacion
            );

            String finalRol = rolSeleccionado;
            db.collection("Usuarios").document(uid).set(u)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "¡Perfil creado con éxito!", Toast.LENGTH_SHORT).show();

                        // Enrutamiento dinámico según el rol asignado
                        Intent intent;
                        if (finalRol.equalsIgnoreCase("Nutricionista") || finalRol.equalsIgnoreCase("Entrenador")) {
                            intent = new Intent(this, panelprofesionalactivity.class);
                        } else if (finalRol.equalsIgnoreCase("Admin")) {
                            intent = new Intent(this, paneladminactivity.class);
                        } else {
                            intent = new Intent(this, homeactivity.class);
                        }

                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        btnFin.setEnabled(true);
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });

        } catch (Exception e) {
            btnFin.setEnabled(true);
            Toast.makeText(this, "Por favor, revisa los datos numéricos ingresados", Toast.LENGTH_SHORT).show();
        }
    }
}