package com.example.gymzy.general.sesion;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.gymzy.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    private TextInputEditText etUsername, etEmail, etPassword;
    private MaterialButton btnSiguiente;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etUsername = findViewById(R.id.editTextUsername);
        etEmail = findViewById(R.id.editTextEmail);
        etPassword = findViewById(R.id.editTextPassword);
        btnSiguiente = findViewById(R.id.btnSiguientePaso); // Asegúrate que este ID sea el de tu XML

        btnSiguiente.setOnClickListener(v -> registrar());
    }

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
                            Intent intent = new Intent(SignUpActivity.this, RegistroActivity.class);
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