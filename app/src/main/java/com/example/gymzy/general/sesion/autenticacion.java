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

public class autenticacion extends AppCompatActivity {

    private MaterialButton btnIrLogin, btnIrRegistro;
    private FirebaseAuth mAuth;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        btnIrLogin = findViewById(R.id.btnIrLogin);
        btnIrRegistro = findViewById(R.id.btnIrRegistro);

        btnIrLogin.setOnClickListener(v -> {
            // MainActivity es tu pantalla de Login
            startActivity(new Intent(autenticacion.this, mainactivity.class));
        });

        btnIrRegistro.setOnClickListener(v -> {
            // Primero creamos la cuenta de email
            Intent intent = new Intent(autenticacion.this, iniciosesion.class);
            startActivity(intent);
        });
    }

    private void irAlHome() {
        startActivity(new Intent(autenticacion.this, homeactivity.class));
        finish();
    }
}