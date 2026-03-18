package com.example.gymzy.general.sesion;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.gymzy.R;
import com.example.gymzy.general.PantallasPrincipales.HomeActivity;
import com.example.gymzy.general.Usuarios.Usuario;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegistroActivity extends AppCompatActivity {
    private TextInputEditText etNom, etEd, etPe, etAl;
    private AutoCompleteTextView spGen, spObj, spAct;
    private MaterialButton btnFin;
    private FirebaseFirestore db; // Referencia a Firestore

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance();

        etNom = findViewById(R.id.etNombreCompleto);
        etEd = findViewById(R.id.etEdad);
        etPe = findViewById(R.id.etPeso);
        etAl = findViewById(R.id.etAltura);
        spGen = findViewById(R.id.spGenero);
        spObj = findViewById(R.id.spObjetivo);
        spAct = findViewById(R.id.spActividad);
        btnFin = findViewById(R.id.btnFinalizarRegistro);

        setupSpinners();

        btnFin.setOnClickListener(v -> guardarEnFirestore());
    }

    private void setupSpinners() {
        String[] generos = {"Hombre", "Mujer", "Otro"};
        String[] objetivos = {"Perder peso", "Mantener peso", "Ganar masa muscular"};
        String[] actividades = {"Sedentario", "Ligero", "Moderado", "Intenso"};

        spGen.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, generos));
        spObj.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, objetivos));
        spAct.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, actividades));
    }

    private void guardarEnFirestore() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        try {
            // Creamos el objeto usuario
            Usuario u = new Usuario(
                    etNom.getText().toString().trim(),
                    Integer.parseInt(etEd.getText().toString().trim()),
                    Double.parseDouble(etPe.getText().toString().trim()),
                    Double.parseDouble(etAl.getText().toString().trim()),
                    spGen.getText().toString(),
                    spObj.getText().toString(),
                    spAct.getText().toString()
            );

            // GUARDADO EN FIRESTORE
            db.collection("Usuarios").document(uid).set(u)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "¡Perfil creado en Firestore!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, HomeActivity.class));
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });

        } catch (Exception e) {
            Toast.makeText(this, "Revisa los datos ingresados", Toast.LENGTH_SHORT).show();
        }
    }
}