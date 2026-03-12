package com.example.gymzy.general.sesion;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gymzy.R;
import com.example.gymzy.general.Usuarios.Usuario;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;

public class SessionManager {
    // Shared Preferences references
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;
    private MediaPlayer soundPlayer;

    // Shared Preferences mode
    private static final int PRIVATE_MODE = 0;

    // Shared Preferences file name
    private static final String PREF_NAME = "GymzyAppPref";

    // All Shared Preferences Keys
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_LOGIN_TIME = "login_time";
    private static final String KEY_SOUND_ENABLED = "sound_enabled";

    // Constructor
    @SuppressLint("WrongConstant")
    public SessionManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = sharedPreferences.edit();

        // Inicializar el reproductor de sonido
        initializeSoundPlayer();
    }

    /**
     * Inicializar el reproductor de sonido
     */
    private void initializeSoundPlayer() {
        soundPlayer = MediaPlayer.create(context, R.raw.main_sonidobotones);
        if (soundPlayer != null) {
            soundPlayer.setVolume(0.5f, 0.5f); // Volumen al 50%
        }
    }

    /**
     * Reproducir sonido de click
     */
    public void playClickSound() {
        if (soundPlayer != null && isSoundEnabled()) {
            try {
                if (soundPlayer.isPlaying()) {
                    soundPlayer.seekTo(0);
                } else {
                    soundPlayer.start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Verificar si el sonido está habilitado
     */
    public boolean isSoundEnabled() {
        return sharedPreferences.getBoolean(KEY_SOUND_ENABLED, true); // Por defecto activado
    }

    /**
     * Habilitar/deshabilitar sonido
     */
    public void setSoundEnabled(boolean enabled) {
        editor.putBoolean(KEY_SOUND_ENABLED, enabled);
        editor.commit();
    }

    /**
     * Create login session with username
     */
    public void createLoginSession(String username) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USERNAME, username);
        editor.putLong(KEY_LOGIN_TIME, System.currentTimeMillis());
        editor.commit();
    }

    /**
     * Check login status
     */
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Get stored username
     */
    public String getUsername() {
        return sharedPreferences.getString(KEY_USERNAME, "");
    }

    /**
     * Get login time
     */
    public long getLoginTime() {
        return sharedPreferences.getLong(KEY_LOGIN_TIME, 0);
    }

    /**
     * Clear session details (logout)
     */
    public void logout() {
        editor.clear();
        editor.commit();
    }

    /**
     * Liberar recursos del reproductor de sonido
     */
    public void releaseSoundPlayer() {
        if (soundPlayer != null) {
            soundPlayer.release();
            soundPlayer = null;
        }
    }

    public static class RegistroSesion implements Serializable {
        private String ejercicio;
        private int series;
        private int reps;
        private float peso;

        public RegistroSesion(String ejercicio, int series, int reps, float peso) {
            this.ejercicio = ejercicio;
            this.series = series;
            this.reps = reps;
            this.peso = peso;
        }

        // Getters
        public String getEjercicio() { return ejercicio; }
        public int getSeries() { return series; }
        public int getReps() { return reps; }
        public float getPeso() { return peso; }



    }

    public static class RegistroActivity extends AppCompatActivity {

        // Cambiamos EditText por TextInputEditText
        private TextInputEditText etNombre, etEdad, etPeso, etAltura;
        // Cambiamos Spinner por AutoCompleteTextView
        private AutoCompleteTextView spGenero, spActividad, spObjetivo;
        private MaterialButton btnRegistrar;

        private DatabaseReference mDatabase;
        private FirebaseAuth mAuth;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_registro);

            // Inicializar Firebase
            mAuth = FirebaseAuth.getInstance();
            mDatabase = FirebaseDatabase.getInstance().getReference();

            // Vincular vistas con los IDs del XML
            etNombre = findViewById(R.id.etNombreCompleto);
            etEdad = findViewById(R.id.etEdad);
            etPeso = findViewById(R.id.etPeso);
            etAltura = findViewById(R.id.etAltura);

            btnRegistrar = findViewById(R.id.btnFinalizarRegistro);

            // Configurar los menús desplegables
            setupDropdowns();

            btnRegistrar.setOnClickListener(v -> guardarDatosEnFirebase());
        }

        private void setupDropdowns() {
            String[] opcionesGenero = {"Hombre", "Mujer", "Otro"};
            String[] opcionesObjetivo = {"Perder peso", "Mantener peso", "Ganar masa muscular"};
            String[] opcionesActividad = {"Sedentario", "Ligero", "Moderado", "Intenso"};

            spGenero.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, opcionesGenero));
            spObjetivo.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, opcionesObjetivo));
            spActividad.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, opcionesActividad));
        }

        private void guardarDatosEnFirebase() {
            try {
                // Obtener valores y validar que no estén vacíos
                String nombre = etNombre.getText().toString().trim();
                String edadStr = etEdad.getText().toString().trim();
                String pesoStr = etPeso.getText().toString().trim();
                String alturaStr = etAltura.getText().toString().trim();
                String genero = spGenero.getText().toString();
                String objetivo = spObjetivo.getText().toString();
                String actividad = spActividad.getText().toString();

                if (nombre.isEmpty() || edadStr.isEmpty() || pesoStr.isEmpty() || alturaStr.isEmpty()) {
                    Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                int edad = Integer.parseInt(edadStr);
                double peso = Double.parseDouble(pesoStr);
                double altura = Double.parseDouble(alturaStr);

                // Crear objeto usuario (Asegúrate de que tu clase Usuario tenga estos campos)
                // LÍNEA CORREGIDA (Línea 237 aprox en tu código)
                Usuario user = new Usuario(nombre, edad, peso, altura, genero, objetivo, actividad);

                if (mAuth.getCurrentUser() != null) {
                    String userId = mAuth.getCurrentUser().getUid();

                    btnRegistrar.setEnabled(false); // Evitar múltiples clics

                    mDatabase.child("Usuarios").child(userId).setValue(user)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(RegistroActivity.this, "¡Perfil creado!", Toast.LENGTH_SHORT).show();
                                // Redirigir a la pantalla principal
                                // startActivity(new Intent(this, MainActivity.class));
                                // finish();
                            })
                            .addOnFailureListener(e -> {
                                btnRegistrar.setEnabled(true);
                                Toast.makeText(RegistroActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }
            } catch (Exception e) {
                Toast.makeText(this, "Revisa los datos ingresados", Toast.LENGTH_SHORT).show();
            }
        }
    }
}