package com.example.gymzy.general.sesion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.example.gymzy.R;
import com.example.gymzy.general.PantallasPrincipales.DrawerBaseActivity;
import com.example.gymzy.general.PantallasPrincipales.HomeActivity;
import com.example.gymzy.general.firebase.FirebaseHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class MainActivity extends DrawerBaseActivity {
    private TextInputEditText etUser, etPass;
    private TextInputLayout layUser, layPass; // Añadido layPass
    private MaterialButton btnLogin;
    private SessionManager sessionManager; // Asegúrate de tener esta variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflamos la vista correctamente para DrawerBaseActivity
        View view = getLayoutInflater().inflate(R.layout.activity_main, null);
        setContentView(view);

        // Inicializamos el SessionManager
        sessionManager = new SessionManager(this);

        // Vincular vistas
        etUser = findViewById(R.id.editTextUsuario);
        etPass = findViewById(R.id.editTextContrasena);
        layUser = findViewById(R.id.textInputLayoutUsuario);
        layPass = findViewById(R.id.textInputLayoutContrasena); // Vinculado correctamente
        btnLogin = findViewById(R.id.btnIniciarSesion);

        btnLogin.setOnClickListener(v -> iniciarSesion());
    }

    private void iniciarSesion() {
        String user = etUser.getText().toString().trim().toLowerCase();
        String pass = etPass.getText().toString().trim();

        // Limpiar errores previos
        layUser.setError(null);
        layPass.setError(null);

        if (user.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Ingresa usuario y contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        // PASO 1: Buscar en la base de datos qué Email le pertenece a ese Username
        FirebaseHelper.getDatabase().child("UsuariosLogueo").child(user).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {

                // PASO 2: Recuperar el email que guardamos en el registro
                String emailAsociado = task.getResult().getValue(String.class);

                if (emailAsociado != null) {
                    // PASO 3: Iniciar sesión en Firebase con ese email y la contraseña
                    FirebaseHelper.getAuth().signInWithEmailAndPassword(emailAsociado, pass)
                            .addOnCompleteListener(authTask -> {
                                if (authTask.isSuccessful()) {
                                    // Guardamos la sesión local
                                    sessionManager.createLoginSession(user);

                                    // Usamos MainActivity.this para el contexto
                                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // Error de contraseña (o credenciales inválidas)
                                    layPass.setError("Contraseña incorrecta");
                                    Toast.makeText(MainActivity.this, "Error al autenticar", Toast.LENGTH_SHORT).show();
                                }
                            });
                }

            } else {
                // BLOQUEO: Si el username no existe en la base de datos
                layUser.setError("Este usuario no existe");
                Toast.makeText(MainActivity.this, "No estás registrado en Gymzy", Toast.LENGTH_LONG).show();
            }
        });
    }
}