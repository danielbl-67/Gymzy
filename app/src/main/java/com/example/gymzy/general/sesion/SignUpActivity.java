package com.example.gymzy.general.sesion;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.gymzy.R;
import com.example.gymzy.general.firebase.FirebaseHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class SignUpActivity extends AppCompatActivity {
    private TextInputEditText etUsername, etEmail, etPassword;
    private MaterialButton btnCrearCuenta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etUsername = findViewById(R.id.editTextUsername);
        etEmail = findViewById(R.id.editTextEmail);
        etPassword = findViewById(R.id.editTextPassword);
        btnCrearCuenta = findViewById(R.id.btnCrearCuenta);

        btnCrearCuenta.setOnClickListener(v -> registrar());
    }

    private void registrar() {
        String user = etUsername.getText().toString().trim().toLowerCase();
        String email = etEmail.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();

        if (user.isEmpty() || email.isEmpty() || pass.length() < 6) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // PASO 1: Verificar que nadie más tenga ese nombre de usuario
        FirebaseHelper.getDatabase().child("UsuariosLogueo").child(user).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    Toast.makeText(this, "Ese nombre de usuario ya existe", Toast.LENGTH_SHORT).show();
                } else {
                    // PASO 2: Crear la cuenta en Firebase Auth
                    FirebaseHelper.getAuth().createUserWithEmailAndPassword(email, pass).addOnCompleteListener(authTask -> {
                        if (authTask.isSuccessful()) {
                            // PASO 3: Guardar el vínculo Username -> Email
                            FirebaseHelper.getDatabase().child("UsuariosLogueo").child(user).setValue(email)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(this, "¡Usuario registrado!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(this, RegistroActivity.class));
                                        finish();
                                    });
                        } else {
                            Toast.makeText(this, "Error: " + authTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}