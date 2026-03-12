package com.example.gymzy.general.PantallasPrincipales;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.example.gymzy.R;
import com.example.gymzy.general.Usuarios.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;

public class ConfiguracionActivity extends DrawerBaseActivity {

    private EditText etNombre, etEdad, etPeso, etAltura;
    private TextView tvValorIMC, tvEstadoIMC;
    private Spinner spinnerSexo, spinnerObjetivo, spinnerActividad;
    private ImageView ivPerfil;
    private Button btnGuardar;

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null) {
                        ivPerfil.setImageURI(imageUri);
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = getLayoutInflater().inflate(R.layout.activity_configuracion, null);
        setContentView(view);
        allocateActivityTitle("Configuración de Perfil");

        initUI();
        setupSpinners();
        cargarDatosDesdeFirestore();

        ivPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryLauncher.launch(intent);
        });

        TextWatcher imcWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { calcularIMC(); }
            @Override public void afterTextChanged(Editable s) {}
        };
        etPeso.addTextChangedListener(imcWatcher);
        etAltura.addTextChangedListener(imcWatcher);

        btnGuardar.setOnClickListener(v -> guardarPerfilEnFirebase());
    }

    private void initUI() {
        ivPerfil = findViewById(R.id.ivPerfilUsuario);
        etNombre = findViewById(R.id.etNombrePerfil);
        etEdad = findViewById(R.id.etEdadPerfil);
        etPeso = findViewById(R.id.etPesoPerfil);
        etAltura = findViewById(R.id.etAlturaPerfil);
        spinnerSexo = findViewById(R.id.spinnerSexo);
        spinnerObjetivo = findViewById(R.id.spinnerObjetivo);
        spinnerActividad = findViewById(R.id.spinnerActividad);
        tvValorIMC = findViewById(R.id.tvValorIMC);
        tvEstadoIMC = findViewById(R.id.tvEstadoIMC);
        btnGuardar = findViewById(R.id.btnGuardarPerfil);
    }

    private void setupSpinners() {
        String[] opcionesSexo = {"Hombre", "Mujer", "Otro"};
        String[] opcionesObjetivo = {"Perder peso", "Mantener peso", "Ganar masa muscular"};
        String[] opcionesActividad = {"Sedentario", "Ligero", "Moderado", "Intenso"};

        // Usamos R.layout.spinner_item en lugar del predeterminado de Android
        ArrayAdapter<String> adapterSexo = new ArrayAdapter<>(this, R.layout.spinner_item, opcionesSexo);
        ArrayAdapter<String> adapterObj = new ArrayAdapter<>(this, R.layout.spinner_item, opcionesObjetivo);
        ArrayAdapter<String> adapterAct = new ArrayAdapter<>(this, R.layout.spinner_item, opcionesActividad);

        // Esto es para cuando el menú se despliega
        adapterSexo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterObj.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterAct.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerSexo.setAdapter(adapterSexo);
        spinnerObjetivo.setAdapter(adapterObj);
        spinnerActividad.setAdapter(adapterAct);
    }

    private void cargarDatosDesdeFirestore() {
        FirebaseUser user = FirebaseHelper.getAuth().getCurrentUser();
        if (user != null) {
            FirebaseHelper.getFirestore().collection("Usuarios").document(user.getUid())
                    .get().addOnSuccessListener(doc -> {
                        if (doc.exists()) {
                            Usuario u = doc.toObject(Usuario.class);
                            if (u != null) {
                                etNombre.setText(u.nombre);
                                etEdad.setText(String.valueOf(u.edad));
                                etPeso.setText(String.valueOf(u.peso));
                                etAltura.setText(String.valueOf(u.altura));

                                // Seleccionar automáticamente las opciones guardadas
                                actualizarSeleccionSpinner(spinnerSexo, u.genero);
                                actualizarSeleccionSpinner(spinnerObjetivo, u.objetivo);
                                actualizarSeleccionSpinner(spinnerActividad, u.actividad);

                                calcularIMC();
                            }
                        }
                    });
        }
    }

    private void actualizarSeleccionSpinner(Spinner spinner, String valorGuardado) {
        if (valorGuardado == null) return;
        ArrayAdapter adapter = (ArrayAdapter) spinner.getAdapter();
        if (adapter != null) {
            int position = adapter.getPosition(valorGuardado);
            if (position >= 0) spinner.setSelection(position);
        }
    }

    private void calcularIMC() {
        try {
            float peso = Float.parseFloat(etPeso.getText().toString());
            float altura = Float.parseFloat(etAltura.getText().toString()) / 100;
            if (altura > 0) {
                float imc = peso / (altura * altura);
                tvValorIMC.setText(String.format(Locale.US, "%.1f", imc));
                if (imc < 18.5) { tvEstadoIMC.setText("Bajo peso"); tvEstadoIMC.setTextColor(Color.CYAN); }
                else if (imc < 25) { tvEstadoIMC.setText("Peso saludable"); tvEstadoIMC.setTextColor(Color.GREEN); }
                else { tvEstadoIMC.setText("Sobrepeso"); tvEstadoIMC.setTextColor(Color.RED); }
            }
        } catch (Exception e) { tvValorIMC.setText("--"); }
    }

    private void guardarPerfilEnFirebase() {
        FirebaseUser user = FirebaseHelper.getAuth().getCurrentUser();
        if (user == null) return;

        try {
            Usuario perfilEditado = new Usuario(
                    etNombre.getText().toString().trim(),
                    Integer.parseInt(etEdad.getText().toString().trim()),
                    Double.parseDouble(etPeso.getText().toString().trim()),
                    Double.parseDouble(etAltura.getText().toString().trim()),
                    spinnerSexo.getSelectedItem().toString(),
                    spinnerObjetivo.getSelectedItem().toString(),
                    spinnerActividad.getSelectedItem().toString()
            );

            btnGuardar.setEnabled(false);
            FirebaseHelper.getFirestore().collection("Usuarios").document(user.getUid())
                    .set(perfilEditado)
                    .addOnSuccessListener(aVoid -> {
                        btnGuardar.setEnabled(true);
                        Toast.makeText(this, "Perfil actualizado", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        btnGuardar.setEnabled(true);
                        Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show();
                    });
        } catch (Exception e) {
            Toast.makeText(this, "Revisa los números ingresados", Toast.LENGTH_SHORT).show();
        }
    }

    public static class FirebaseHelper {

        public static DatabaseReference getDatabase() {
            // REEMPLAZA ESTA URL con la que copiaste de tu consola de Firebase
            String url = "https://bdpruebasprog-default-rtdb.firebaseio.com/";
            return FirebaseDatabase.getInstance(url).getReference();
        }

        public static FirebaseFirestore getFirestore() {
            return FirebaseFirestore.getInstance();
        }

        public static FirebaseAuth getAuth() {
            return FirebaseAuth.getInstance();
        }
    }
}