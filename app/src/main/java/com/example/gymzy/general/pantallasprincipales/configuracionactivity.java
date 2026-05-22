package com.example.gymzy.general.pantallasprincipales;

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
import com.example.gymzy.general.usuarios.usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;

/**
 * Actividad encargada de la gestion y actualizacion del perfil del usuario.
 * Hereda de {@link menuinferior} y permite modificar datos fisicos, calcular el IMC
 * en tiempo real y persistir los cambios en Cloud Firestore sin perder metadatos del sistema.
 */
public class configuracionactivity extends menuinferior {

    private EditText etNombre, etEdad, etPeso, etAltura;
    private TextView tvValorIMC, tvEstadoIMC;
    private Spinner spinnerSexo, spinnerObjetivo, spinnerActividad;
    private Button btnGuardar;
    private usuario usuarioActual;



    /**
     * Infla el diseño, vincula elementos UI, inicializa los componentes de seleccion,
     * dispara la descarga de datos y configura los escuchadores para el calculo reactivo del IMC.
     *
     * @param savedInstanceState Contiene el estado previo de los datos de la interfaz.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = getLayoutInflater().inflate(R.layout.activity_configuracion, null);
        setContentView(view);
        allocateActivityTitle("Configuración de Perfil");

        initUI();
        setupSpinners();
        cargarDatosDesdeFirestore();


        TextWatcher imcWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { calcularIMC(); }
            @Override public void afterTextChanged(Editable s) {}
        };
        etPeso.addTextChangedListener(imcWatcher);
        etAltura.addTextChangedListener(imcWatcher);

        btnGuardar.setOnClickListener(v -> guardarPerfilEnFirebase());
    }

    /**
     * Enlaza las variables locales con sus respectivos identificadores del archivo XML.
     */
    private void initUI() {
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

    /**
     * Llena los componentes Spinner con arreglos de strings correspondientes a opciones
     * de sexo, meta deportiva y nivel de actividad fisica.
     */
    private void setupSpinners() {
        String[] opcionesSexo = {"Hombre", "Mujer", "Otro"};
        String[] opcionesObjetivo = {"Perder peso", "Mantener peso", "Ganar masa muscular"};
        String[] opcionesActividad = {"Sedentario", "Ligero", "Moderado", "Intenso"};

        ArrayAdapter<String> adapterSexo = new ArrayAdapter<>(this, R.layout.spinner_item, opcionesSexo);
        ArrayAdapter<String> adapterObj = new ArrayAdapter<>(this, R.layout.spinner_item, opcionesObjetivo);
        ArrayAdapter<String> adapterAct = new ArrayAdapter<>(this, R.layout.spinner_item, opcionesActividad);

        adapterSexo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterObj.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterAct.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerSexo.setAdapter(adapterSexo);
        spinnerObjetivo.setAdapter(adapterObj);
        spinnerActividad.setAdapter(adapterAct);
    }

    /**
     * Baja el documento del usuario logueado desde Cloud Firestore para rellenar los
     * campos editables y posicionar los selectores en sus valores guardados.
     */
    private void cargarDatosDesdeFirestore() {
        FirebaseUser user = FirebaseHelper.getAuth().getCurrentUser();
        if (user != null) {
            FirebaseHelper.getFirestore().collection("Usuarios").document(user.getUid())
                    .get().addOnSuccessListener(doc -> {
                        if (doc.exists()) {
                            usuarioActual = doc.toObject(usuario.class);
                            if (usuarioActual != null) {
                                etNombre.setText(usuarioActual.nombre);
                                etEdad.setText(String.valueOf(usuarioActual.edad));
                                etPeso.setText(String.valueOf(usuarioActual.peso));
                                etAltura.setText(String.valueOf(usuarioActual.altura));

                                actualizarSeleccionSpinner(spinnerSexo, usuarioActual.genero);
                                actualizarSeleccionSpinner(spinnerObjetivo, usuarioActual.objetivo);
                                actualizarSeleccionSpinner(spinnerActividad, usuarioActual.actividad);

                                calcularIMC();
                            }
                        }
                    });
        }
    }

    /**
     * Busca la posicion de una cadena de texto dentro del adaptador de un Spinner y establece la seleccion.
     *
     * @param spinner       Componente visual de seleccion a modificar.
     * @param valorGuardado Cadena de texto a buscar dentro de las opciones del componente.
     */
    private void actualizarSeleccionSpinner(Spinner spinner, String valorGuardado) {
        if (valorGuardado == null) return;
        ArrayAdapter adapter = (ArrayAdapter) spinner.getAdapter();
        if (adapter != null) {
            int position = adapter.getPosition(valorGuardado);
            if (position >= 0) spinner.setSelection(position);
        }
    }

    /**
     * Realiza el calculo aritmetico del indice de Masa Corporal en base a las entradas de peso y estatura,
     * actualizando los indicadores de pantalla y alterando los colores del estado.
     */
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

    /**
     * Extrae los datos actualizados del formulario, rescata las propiedades estructurales previas
     * y reescribe de forma asincrona el documento en Cloud Firestore.
     */
    private void guardarPerfilEnFirebase() {
        FirebaseUser user = FirebaseHelper.getAuth().getCurrentUser();
        if (user == null) return;

        try {
            String rolExistente = (usuarioActual != null && usuarioActual.rol != null) ? usuarioActual.rol : "Usuario";
            String emailExistente = (usuarioActual != null && usuarioActual.email != null) ? usuarioActual.email : user.getEmail();
            String codigoExistente = (usuarioActual != null && usuarioActual.codigoVinculacion != null) ? usuarioActual.codigoVinculacion : "";

            usuario perfilEditado = new usuario(
                    etNombre.getText().toString().trim(),
                    Integer.parseInt(etEdad.getText().toString().trim()),
                    Double.parseDouble(etPeso.getText().toString().trim()),
                    Double.parseDouble(etAltura.getText().toString().trim()),
                    spinnerSexo.getSelectedItem().toString(),
                    spinnerObjetivo.getSelectedItem().toString(),
                    spinnerActividad.getSelectedItem().toString(),
                    rolExistente,
                    emailExistente,
                    codigoExistente
            );

            btnGuardar.setEnabled(false);

            FirebaseHelper.getFirestore().collection("Usuarios").document(user.getUid())
                    .set(perfilEditado)
                    .addOnSuccessListener(aVoid -> {
                        btnGuardar.setEnabled(true);
                        usuarioActual = perfilEditado;
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

    /**
     * Clase estatica de soporte que provee las referencias unificadas para el acceso a
     * las instancias distribuidas de Firebase Realtime Database, Cloud Firestore y Auth.
     */
    public static class FirebaseHelper {
        /**
         * Inicializa y devuelve la referencia al nodo raiz de Realtime Database apuntando a una URL especifica.
         *
         * @return DatabaseReference configurada para la base de datos de pruebas.
         */
        public static DatabaseReference getDatabase() {
            String url = "https://bdpruebasprog-default-rtdb.firebaseio.com/";
            return FirebaseDatabase.getInstance(url).getReference();
        }

        /**
         * Devuelve la instancia singleton activa de Cloud Firestore.
         *
         * @return Objeto FirebaseFirestore.
         */
        public static FirebaseFirestore getFirestore() {
            return FirebaseFirestore.getInstance();
        }

        /**
         * Devuelve la instancia singleton activa del gestor de sesiones de Firebase Auth.
         *
         * @return Objeto FirebaseAuth.
         */
        public static FirebaseAuth getAuth() {
            return FirebaseAuth.getInstance();
        }
    }
}