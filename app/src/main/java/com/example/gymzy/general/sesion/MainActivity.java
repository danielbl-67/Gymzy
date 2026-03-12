package com.example.gymzy.general.sesion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gymzy.R;
import com.example.gymzy.general.PantallasPrincipales.HomeActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    private TextInputEditText etUser, etPass;
    private TextInputLayout layUser, layPass;
    private MaterialButton btnLogin;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Usamos directamente el layout de la actividad
        setContentView(R.layout.activity_main);

        sessionManager = new SessionManager(this);

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

        if (user.isEmpty() || pass.isEmpty()) return;

        // BUSCAMOS EN LA MISMA RUTA: UsuariosLogueo -> nombre_usuario
        FirebaseDatabase.getInstance().getReference().child("UsuariosLogueo").child(user).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {

                        // Si existe, sacamos el email que guardamos en el SignUp
                        String emailRecuperado = task.getResult().getValue(String.class);

                        // Iniciamos sesión con ese email
                        FirebaseAuth.getInstance().signInWithEmailAndPassword(emailRecuperado, pass)
                                .addOnCompleteListener(authTask -> {
                                    if (authTask.isSuccessful()) {
                                        sessionManager.createLoginSession(user);
                                        startActivity(new Intent(MainActivity.this, HomeActivity.class));
                                        finish();
                                    } else {
                                        Toast.makeText(this, "Contraseña incorrecta", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        // Si llegas aquí, es que el nombre de usuario no existe en la DB
                        Toast.makeText(this, "El usuario '" + user + "' no existe", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void loginConFirebase(String email, String password, String username) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(authTask -> {
                    if (authTask.isSuccessful()) {
                        // LOGIN EXITOSO
                        sessionManager.createLoginSession(username);

                        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                        // Limpiamos la pila de actividades para que no pueda volver atrás al login
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        // FALLO EN LA CONTRASEÑA O CUENTA BLOQUEADA
                        layPass.setError("Contraseña incorrecta o error de autenticación");
                        String errorMsg = authTask.getException() != null ? authTask.getException().getMessage() : "Error desconocido";
                        Toast.makeText(MainActivity.this, "Fallo: " + errorMsg, Toast.LENGTH_LONG).show();
                    }
                });
    }
}