package com.example.gymzy.general.sesion;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.gymzy.R;
import com.example.gymzy.general.PantallasPrincipales.HomeActivity;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

public class AuthActivity extends AppCompatActivity {

    private MaterialButton btnIrLogin, btnIrRegistro;
    private FirebaseAuth mAuth;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        // Si ya está logueado, saltamos directo al Home
        if (mAuth.getCurrentUser() != null) {
            irAlHome();
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
            startActivity(new Intent(AuthActivity.this, MainActivity.class));
        });

        btnIrRegistro.setOnClickListener(v -> {
            // Primero creamos la cuenta de email
            Intent intent = new Intent(AuthActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
    }

    private void irAlHome() {
        startActivity(new Intent(AuthActivity.this, HomeActivity.class));
        finish();
    }
}