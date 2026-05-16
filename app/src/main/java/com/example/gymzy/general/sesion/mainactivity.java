package com.example.gymzy.general.sesion;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.gymzy.R;
import com.example.gymzy.general.pantallasprincipales.menuinferior;
import com.example.gymzy.general.pantallasprincipales.homeactivity;
import com.example.gymzy.general.pantallasprincipales.registrarsesiones;
import com.example.gymzy.general.roles.paneladminactivity;
import com.example.gymzy.general.roles.panelprofesionalactivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

public class mainactivity extends menuinferior {
    private TextInputEditText etUser, etPass;
    private TextInputLayout layUser, layPass;
    private MaterialButton btnLogin;
    private registrarsesiones sessionManager;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sessionManager = new registrarsesiones(this);
        db = FirebaseFirestore.getInstance();

        etUser = findViewById(R.id.editTextUsuario);
        etPass = findViewById(R.id.editTextContrasena);
        layUser = findViewById(R.id.textInputLayoutUsuario);
        layPass = findViewById(R.id.textInputLayoutContrasena);
        btnLogin = findViewById(R.id.btnIniciarSesion);

        btnLogin.setOnClickListener(v -> iniciarSesion());
    }

    private void iniciarSesion() {
        String user = etUser.getText().toString().trim().toLowerCase();
        String pass = etPass.getText().toString().trim();

        if (user.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        btnLogin.setEnabled(false); // Evitamos múltiples clics seguidos

        // PASO 1: Buscamos el email asociado al username en Realtime Database
        FirebaseDatabase.getInstance().getReference().child("UsuariosLogueo").child(user).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        String emailRecuperado = task.getResult().getValue(String.class);

                        // PASO 2: Iniciamos sesión en Firebase Auth con el email obtenido
                        FirebaseAuth.getInstance().signInWithEmailAndPassword(emailRecuperado, pass)
                                .addOnCompleteListener(authTask -> {
                                    if (authTask.isSuccessful() && authTask.getResult().getUser() != null) {
                                        // Guardamos la sesión local en SharedPreferences
                                        sessionManager.createLoginSession(user);

                                        // PASO 3: Verificamos el rol en Firestore para redirigir
                                        verificarRolYRedirigir(authTask.getResult().getUser().getUid());
                                    } else {
                                        btnLogin.setEnabled(true);
                                        layPass.setError("Contraseña incorrecta");
                                        Toast.makeText(this, "Contraseña incorrecta", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        btnLogin.setEnabled(true);
                        layUser.setError("El usuario no existe");
                        Toast.makeText(this, "El usuario '" + user + "' no existe", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void verificarRolYRedirigir(String uid) {
        // Consultamos la colección unificada de Firestore
        db.collection("Usuarios").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String rol = documentSnapshot.getString("rol");
                        Intent intent;

                        // Evaluamos el rol de forma segura ignorando mayúsculas/minúsculas
                        if (rol != null && (rol.equalsIgnoreCase("Nutricionista") || rol.equalsIgnoreCase("Entrenador"))) {
                            intent = new Intent(mainactivity.this, panelprofesionalactivity.class);
                        } else if (rol != null && rol.equalsIgnoreCase("Admin")) {
                            intent = new Intent(mainactivity.this, paneladminactivity.class);
                        } else {
                            intent = new Intent(mainactivity.this, homeactivity.class);
                        }

                        // Limpiamos la pila de actividades para una navegación limpia
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        // Si está autenticado pero no creó su documento físico en Firestore, forzamos el registro
                        Intent intent = new Intent(mainactivity.this, registroactivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    btnLogin.setEnabled(true);
                    Toast.makeText(this, "Error al verificar el perfil: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}