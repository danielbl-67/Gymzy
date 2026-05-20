package com.example.gymzy.general.sesion;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.gymzy.R;
import com.example.gymzy.general.pantallasprincipales.homeactivity;
import com.example.gymzy.general.roles.paneladminactivity;
import com.example.gymzy.general.roles.panelprofesionalactivity;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Actividad de bienvenida y puerta de entrada al sistema de autenticación (Splash/Gatekeeper).
 * Evalúa en segundo plano si el dispositivo tiene una sesión activa para redirigir
 * automáticamente al usuario según su rol sin mostrar la interfaz de bienvenida.
 * Si no hay sesión, ofrece las opciones de ir a la pantalla de Login o Registro.
 */
public class autenticacion extends AppCompatActivity {

    private MaterialButton btnIrLogin, btnIrRegistro;
    private FirebaseAuth mAuth;

    /**
     * Metodo de ciclo de vida que se ejecuta cuando la actividad se vuelve visible al usuario.
     * <p>
     * Realiza un control de flujo crítico:
     * <ul>
     *   <li>1. Si hay un usuario autenticado en Firebase Auth, consulta su perfil en Cloud Firestore.</li>
     *   <li>2. Evalúa el campo "rol" y realiza el enrutamiento inteligente (Profesionales, Admin o Cliente).</li>
     *   <li>3. Si el usuario existe pero no completó su ficha física, lo desvía a {@link registroactivity}.</li>
     *   <li>4. Si ocurre un fallo de red o lectura, usa {@link homeactivity} como mecanismo de fallback.</li>
     * </ul>
     */
    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            // En vez de saltar ciegamente a HomeActivity, leemos el rol en Firestore
            FirebaseFirestore.getInstance().collection("Usuarios")
                    .document(mAuth.getCurrentUser().getUid()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String rol = documentSnapshot.getString("rol");
                            Intent intent;

                            if (rol != null && (rol.equalsIgnoreCase("Nutricionista") || rol.equalsIgnoreCase("Entrenador"))) {
                                intent = new Intent(autenticacion.this, panelprofesionalactivity.class);
                            } else if (rol != null && rol.equalsIgnoreCase("Admin")) {
                                intent = new Intent(autenticacion.this, paneladminactivity.class);
                            } else {
                                intent = new Intent(autenticacion.this, homeactivity.class);
                            }
                            startActivity(intent);
                            finish();
                        } else {
                            // Sesión iniciada pero no completó los datos físicos
                            startActivity(new Intent(autenticacion.this, registroactivity.class));
                            finish();
                        }
                    }).addOnFailureListener(e -> {
                        // Si falla el internet, enviamos a Home por fallback o deslogueamos
                        startActivity(new Intent(autenticacion.this, homeactivity.class));
                        finish();
                    });
        }
    }

    /**
     * Metodo de ciclo de vida que inicializa la actividad si no se procesó una sesión activa en onStart.
     * Enlaza los botones de navegación inicial y asigna sus respectivos comportamientos de clic.
     *
     * @param savedInstanceState Si la actividad se recrea, este objeto contiene los datos del estado previo.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        btnIrLogin = findViewById(R.id.btnIrLogin);
        btnIrRegistro = findViewById(R.id.btnIrRegistro);

        // Envía al usuario a la pantalla de Login (MainActivity)
        btnIrLogin.setOnClickListener(v -> {
            startActivity(new Intent(autenticacion.this, mainactivity.class));
        });

        // Envía al usuario al paso primario de Registro (InicioSesion)
        btnIrRegistro.setOnClickListener(v -> {
            Intent intent = new Intent(autenticacion.this, iniciosesion.class);
            startActivity(intent);
        });
    }

    /**
     * Redirige al usuario directamente hacia la pantalla principal (HomeActivity)
     * destruyendo la actividad actual de la pila de navegación.
     *
     * @deprecated Este metodo quedó sin uso en el flujo actual ya que la lógica de enrutamiento
     *             se maneja de forma asíncrona dentro del callback de {@link #onStart()}.
     */
    private void irAlHome() {
        startActivity(new Intent(autenticacion.this, homeactivity.class));
        finish();
    }
}