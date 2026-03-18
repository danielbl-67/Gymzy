package com.example.gymzy.general.PantallasPrincipales;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.gymzy.R;
import com.example.gymzy.general.sesion.SessionManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class HomeActivity extends DrawerBaseActivity implements SensorEventListener {

    private SessionManager sessionManager;
    private SensorManager sensorManager;
    private Sensor stepSensor;
    private DatabaseReference mDatabase;

    private TextView tvWaterCount, tvCurrentDate, tvWalkingTime, tvCalories, tvStepCount;
    private ProgressBar pbSteps;
    private TextView tvL, tvM, tvX, tvJ, tvV;
    private View barMon, barTue, barWed, barThu, barFri;

    private float litrosActuales = 0.0f;
    private final float OBJETIVO_AGUA = 2.5f;
    private int pasosIniciales = -1;
    private int pasosDeHoy = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = getLayoutInflater().inflate(R.layout.activity_home, null);
        setContentView(view);
        allocateActivityTitle("Inicio");

        sessionManager = new SessionManager(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        initUI();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 100);
        }

        actualizarFecha();
        sincronizarConFirebase(); // Descarga datos de la nube al iniciar
    }

    private void initUI() {
        tvWaterCount = findViewById(R.id.tvWaterCount);
        tvCurrentDate = findViewById(R.id.tvCurrentDate);
        tvWalkingTime = findViewById(R.id.tvWalkingTime);
        tvCalories = findViewById(R.id.tvCalories);
        tvStepCount = findViewById(R.id.tvStepCount);
        pbSteps = findViewById(R.id.pbSteps);

        tvL = findViewById(R.id.tvLunes);
        tvM = findViewById(R.id.tvMartes);
        tvX = findViewById(R.id.tvMiercoles);
        tvJ = findViewById(R.id.tvJueves);
        tvV = findViewById(R.id.tvViernes);

        barMon = findViewById(R.id.barMon);
        barTue = findViewById(R.id.barTue);
        barWed = findViewById(R.id.barWed);
        barThu = findViewById(R.id.barThu);
        barFri = findViewById(R.id.barFri);

        TextView tvWelcome = findViewById(R.id.tvWelcomeUser);
        Button btnAddWater = findViewById(R.id.btnAddWater);
        Button btnMusculos = findViewById(R.id.btnMusculos);

        tvWelcome.setText("¡Hola, " + (sessionManager.getUsername() != null ? sessionManager.getUsername() : "Atleta") + "!");
        btnMusculos.setOnClickListener(v -> startActivity(new Intent(this, ListaRutina.class)));

        Button btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        // Evento para cerrar sesión
        btnCerrarSesion.setOnClickListener(v -> {
            cerrarSesion();
        });

        btnAddWater.setOnClickListener(v -> {
            litrosActuales += 0.25f;
            actualizarUIActividad();
            guardarProgresoEnFirebase(); // Guarda el agua en tiempo real
        });
    }
    private void cerrarSesion() {
        // 1. Cerrar sesión en Firebase Auth
        FirebaseAuth.getInstance().signOut();

        // 2. Limpiar SharedPreferences a través de tu SessionManager
        sessionManager.logout();

        // 3. Redirigir a la pantalla de Auth o Login
        // (Asegúrate de cambiar 'AuthActivity.class' por el nombre de tu clase de inicio)
        Intent intent = new Intent(HomeActivity.this, com.example.gymzy.general.sesion.AuthActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Limpia el historial de pantallas
        startActivity(intent);

        finish(); // Cierra la actividad actual
    }

    // --- Lógica de Sincronización Firebase ---

    private void guardarProgresoEnFirebase() {
        String userId = sessionManager.getUsername();
        if (userId == null) return;

        Map<String, Object> actividadHoy = new HashMap<>();
        actividadHoy.put("pasos", pasosDeHoy);
        actividadHoy.put("agua", litrosActuales);
        actividadHoy.put("calorias", pasosDeHoy * 0.04f);

        mDatabase.child("Usuarios").child(userId).child("actividad_hoy").setValue(actividadHoy);
    }

    private void sincronizarConFirebase() {
        String userId = sessionManager.getUsername();
        if (userId == null) return;

        mDatabase.child("Usuarios").child(userId).child("actividad_hoy").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Si hay datos en Firebase (ej. abres la app en otro móvil), los carga
                    Double agua = snapshot.child("agua").getValue(Double.class);
                    if (agua != null) litrosActuales = agua.floatValue();
                    actualizarUIActividad();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void actualizarUIActividad() {
        tvWaterCount.setText(String.format(Locale.getDefault(), "%.2fL / %.1fL", litrosActuales, OBJETIVO_AGUA));
        tvStepCount.setText(String.format(Locale.getDefault(), "%d\npasos", pasosDeHoy));
        pbSteps.setProgress(pasosDeHoy);
        tvCalories.setText(String.format(Locale.getDefault(), "%.0f\nkcal", pasosDeHoy * 0.04f));
        tvWalkingTime.setText(String.format(Locale.getDefault(), "%d\nmin", pasosDeHoy / 100));
    }

    // --- Lógica de Historial y Gráfica ---

    private void cargarHistorial() {
        SharedPreferences prefs = getSharedPreferences("GymzyHistory", MODE_PRIVATE);
        int[] diasCalendar = {Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY};
        String[] nombresDocs = {"Mon", "Tue", "Wed", "Thu", "Fri"};

        for (int i = 0; i < diasCalendar.length; i++) {
            int pasos = prefs.getInt(nombresDocs[i], 0);
            TextView tvDia = getTextViewPorDia(diasCalendar[i]);
            if (pasos > 5000 && tvDia != null) {
                tvDia.setBackgroundColor(Color.parseColor("#0D47A1"));
                tvDia.setTextColor(Color.WHITE);
            }
        }
    }

    private void actualizarGrafica() {
        SharedPreferences prefs = getSharedPreferences("GymzyHistory", MODE_PRIVATE);
        View[] barras = {barMon, barTue, barWed, barThu, barFri};
        String[] nombresDocs = {"Mon", "Tue", "Wed", "Thu", "Fri"};
        int alturaMaxPx = 300;

        for (int i = 0; i < barras.length; i++) {
            int pasos = prefs.getInt(nombresDocs[i], 0);
            float ratio = (float) pasos / 10000;
            if (ratio > 1) ratio = 1;

            ViewGroup.LayoutParams params = barras[i].getLayoutParams();
            params.height = (int) (ratio * alturaMaxPx) + 10;
            barras[i].setLayoutParams(params);
            if (pasos > 0) barras[i].setBackgroundColor(Color.parseColor("#FFD700"));
        }
    }

    private void checkDailyReset() {
        SharedPreferences prefs = getSharedPreferences("GymzyHistory", MODE_PRIVATE);
        String lastDate = prefs.getString("last_date", "");
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        if (!currentDate.equals(lastDate)) {
            String userId = sessionManager.getUsername();
            if (!lastDate.isEmpty()) {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, -1);
                String diaAyer = new SimpleDateFormat("EEE", Locale.US).format(cal.getTime());

                // Guardar en Local
                prefs.edit().putInt(diaAyer, pasosDeHoy).apply();

                // Guardar en Firebase el histórico antes de borrar
                if (userId != null) {
                    mDatabase.child("Usuarios").child(userId).child("historial").child(lastDate).child("pasos").setValue(pasosDeHoy);
                }
            }
            pasosIniciales = -1;
            pasosDeHoy = 0;
            litrosActuales = 0.0f;
            prefs.edit().putString("last_date", currentDate).apply();

            // Limpiar datos de hoy en Firebase para el nuevo día
            if (userId != null) mDatabase.child("Usuarios").child(userId).child("actividad_hoy").removeValue();
        }
    }

    private TextView getTextViewPorDia(int calendarDay) {
        switch (calendarDay) {
            case Calendar.MONDAY: return tvL;
            case Calendar.TUESDAY: return tvM;
            case Calendar.WEDNESDAY: return tvX;
            case Calendar.THURSDAY: return tvJ;
            case Calendar.FRIDAY: return tvV;
            default: return null;
        }
    }

    private void marcarDiaActual() {
        Calendar calendar = Calendar.getInstance();
        TextView hoy = getTextViewPorDia(calendar.get(Calendar.DAY_OF_WEEK));
        if (hoy != null) {
            hoy.setBackgroundColor(Color.BLACK);
            hoy.setTextColor(Color.WHITE);
            hoy.setTypeface(null, Typeface.BOLD);
        }
    }

    private void actualizarFecha() {
        String date = new SimpleDateFormat("EEEE, d MMM", Locale.getDefault()).format(new Date());
        tvCurrentDate.setText(date.substring(0, 1).toUpperCase() + date.substring(1));
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            if (pasosIniciales == -1) pasosIniciales = (int) event.values[0];
            pasosDeHoy = (int) event.values[0] - pasosIniciales;
            actualizarUIActividad();

            // Cada 100 pasos guardamos en Firebase para no saturar la red
            if (pasosDeHoy % 100 == 0) {
                guardarProgresoEnFirebase();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    protected void onResume() {
        super.onResume();
        checkDailyReset();
        cargarHistorial();
        actualizarGrafica();
        marcarDiaActual();
        if (stepSensor != null) sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        guardarProgresoEnFirebase(); // Guardar antes de salir
    }
}