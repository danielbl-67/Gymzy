package com.example.gymzy.general.sesion;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.gymzy.R;
import com.example.gymzy.general.PantallasPrincipales.HomeActivity;
import com.example.gymzy.general.Usuarios.Usuario;
import com.example.gymzy.general.firebase.FirebaseHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class RegistroActivity extends AppCompatActivity {
    private TextInputEditText etNom, etEd, etPe, etAl;
    private MaterialButton btnFin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        etNom = findViewById(R.id.etNombreCompleto);
        etEd = findViewById(R.id.etEdad);
        etPe = findViewById(R.id.etPeso);
        etAl = findViewById(R.id.etAltura);
        btnFin = findViewById(R.id.btnFinalizarRegistro);

        btnFin.setOnClickListener(v -> guardarPerfil());
    }

    private void guardarPerfil() {
        try {
            Usuario u = new Usuario(
                    etNom.getText().toString(),
                    Integer.parseInt(etEd.getText().toString()),
                    Double.parseDouble(etPe.getText().toString()),
                    Double.parseDouble(etAl.getText().toString()),
                    "Hombre", "Ganar Masa", "Moderado" // Valores por defecto o de tus spinners
            );

            String uid = FirebaseHelper.getAuth().getCurrentUser().getUid();
            FirebaseHelper.getFirestore().collection("Usuarios").document(uid).set(u)
                    .addOnSuccessListener(aVoid -> {
                        startActivity(new Intent(this, HomeActivity.class));
                        finish();
                    });
        } catch (Exception e) {
            Toast.makeText(this, "Error en los datos numéricos", Toast.LENGTH_SHORT).show();
        }
    }
}