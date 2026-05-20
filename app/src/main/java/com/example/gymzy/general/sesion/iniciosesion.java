package com.example.gymzy.general.sesion;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.gymzy.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Actividad encargada del primer paso del registro de nuevos usuarios (Sign Up).
 * Se ocupa de la creación de las credenciales de acceso primarias del usuario,
 * vinculando un nombre de usuario único con su correo electrónico mediante una estrategia híbrida
 * que combina Firebase Authentication y Firebase Realtime Database.
 */
public class iniciosesion extends AppCompatActivity {
    private TextInputEditText etUsername, etEmail, etPassword;
    private MaterialButton btnSiguiente;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    /**
     * Metodo de ciclo de vida que inicializa la actividad, vincula las instancias
     * del servicio de autenticación y los elementos del diseño XML.
     *
     * @param savedInstanceState Si la actividad se recrea, este objeto contiene los datos del estado previo.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etUsername = findViewById(R.id.editTextUsername);
        etEmail = findViewById(R.id.editTextEmail);
        etPassword = findViewById(R.id.editTextPassword);
        btnSiguiente = findViewById(R.id.btnSiguientePaso);

        btnSiguiente.setOnClickListener(v -> registrar());
    }

    /**
     * Procesa y valida el alta del nuevo usuario en el sistema.
     * <p>
     * El flujo de operaciones que sigue este metodo incluye:
     * <ul>
     *   <li>1. Verifica que los campos no estén vacíos y que la contraseña cumpla con el mínimo de 6 caracteres.</li>
     *   <li>2. Registra al usuario en Firebase Auth usando el correo y la contraseña provistos.</li>
     *   <li>3. Guarda de forma asíncrona la correspondencia "username -> email" en el nodo central
     *          "UsuariosLogueo" de Firebase Realtime Database para dar soporte al inicio de sesión posterior.</li>
     *   <li>4. Redirige al usuario hacia {@link registroactivity} para el llenado de su información de perfil.</li>
     * </ul>
     */
    private void registrar() {
        String user = etUsername.getText().toString().trim().toLowerCase();
        String email = etEmail.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();

        if (user.isEmpty() || email.isEmpty() || pass.length() < 6) {
            Toast.makeText(this, "Completa los campos correctamente", Toast.LENGTH_SHORT).show();
            return;
        }

        // PASO 1: Crear en Firebase Auth
        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                // PASO 2: Guardar el nombre de usuario en REALTIME DATABASE
                // Usamos la misma ruta que usará el Login
                FirebaseDatabase.getInstance().getReference().child("UsuariosLogueo")
                        .child(user)
                        .setValue(email)
                        .addOnSuccessListener(aVoid -> {
                            // PASO 3: Salto al siguiente paso (Registro de peso/altura)
                            Intent intent = new Intent(iniciosesion.this, registroactivity.class);
                            startActivity(intent);
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Error al guardar username: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });

            } else {
                Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}