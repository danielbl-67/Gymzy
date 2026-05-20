package com.example.gymzy.general.pantallasprincipales;

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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.gymzy.R;
import com.example.gymzy.general.sesion.autenticacion;
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

/**
 * Actividad principal del usuario comun que gestiona el panel de control (Dashboard).
 * Monitorea el contador de pasos mediante sensores de hardware, registra la ingesta de agua
 * y sincroniza las metricas diarias con Firebase Realtime Database.
 */
public class homeactivity extends menuinferior implements SensorEventListener {

    private registrarsesiones sessionManager;
    private SensorManager sensorManager;
    private Sensor stepSensor;
    private DatabaseReference mDatabase;

    private TextView tvWaterCount, tvCurrentDate, tvWalkingTime, tvCalories, tvStepCount;
    private ProgressBar pbSteps;
    private TextView tvL, tvM, tvX, tvJ, tvV;
    private View barMon, barTue, barWed, barThu, barFri;
    private ScrollView scrollViewPrincipal;

    private float litrosActuales = 0.0f;
    private final float OBJETIVO_AGUA = 2.5f;
    private int pasosIniciales = -1;
    private int pasosDeHoy = 0;

    /**
     * Inicializa componentes, gestores de datos, enlace de sensores de hardware
     * y solicita permisos en tiempo de ejecucion para el reconocimiento de actividad fisica.
     *
     * @param savedInstanceState Estado previamente almacenado de la instancia.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = getLayoutInflater().inflate(R.layout.activity_home, null);
        setContentView(view);
        allocateActivityTitle("Inicio");

        sessionManager = new registrarsesiones(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        initUI();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 100);
        }

        actualizarFecha();
        sincronizarConFirebase();
    }

    /**
     * Enlaza las vistas del XML, gestiona las acciones de los botones de progreso,
     * scroll automatico, comparticion social y cierre de sesion.
     */
    private void initUI() {
        scrollViewPrincipal = (ScrollView) findViewById(android.R.id.content).getRootView().findViewWithTag("scroll_view_principal");
        if (scrollViewPrincipal == null) {
            View v = findViewById(R.id.tvWelcomeUser);
            if (v != null && v.getParent() != null && v.getParent().getParent() instanceof ScrollView) {
                scrollViewPrincipal = (ScrollView) v.getParent().getParent();
            }
        }

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

        Button btnVerHistorial = findViewById(R.id.btnVerHistorial);
        Button btnCompartirProgreso = findViewById(R.id.btnCompartirProgreso);

        tvWelcome.setText("¡Hola, " + (sessionManager.getUsername() != null ? sessionManager.getUsername() : "Atleta") + "!");

        btnVerHistorial.setOnClickListener(v -> {
            if (scrollViewPrincipal != null) {
                scrollViewPrincipal.post(() -> scrollViewPrincipal.smoothScrollTo(0, barMon.getTop()));
            }
        });

        btnCompartirProgreso.setOnClickListener(v -> {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "¡Hoy he dado " + pasosDeHoy + " pasos en GYMZY! Sigue mi ritmo.");
            sendIntent.setType("text/plain");
            Intent shareIntent = Intent.createChooser(sendIntent, null);
            startActivity(shareIntent);
        });

        Button btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        btnCerrarSesion.setOnClickListener(v -> cerrarSesion());

        btnAddWater.setOnClickListener(v -> {
            litrosActuales += 0.25f;
            actualizarUIActividad();
            guardarProgresoEnFirebase();
        });
    }

    /**
     * Termina la sesion activa en Firebase Auth, limpia las preferencias locales y redirige a la pantalla de login.
     */
    private void cerrarSesion() {
        FirebaseAuth.getInstance().signOut();
        sessionManager.logout();
        Intent intent = new Intent(homeactivity.this, autenticacion.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * Envia los datos acumulados del dia (pasos, agua y calorias calculadas) a Firebase Realtime Database.
     */
    private void guardarProgresoEnFirebase() {
        String userId = sessionManager.getUsername();
        if (userId == null) return;

        Map<String, Object> actividadHoy = new HashMap<>();
        actividadHoy.put("pasos", pasosDeHoy);
        actividadHoy.put("agua", litrosActuales);
        actividadHoy.put("calorias", pasosDeHoy * 0.04f);

        mDatabase.child("Usuarios").child(userId).child("actividad_hoy").setValue(actividadHoy);
    }

    /**
     * Recupera de Firebase la informacion de la ingesta de agua guardada previamente para el dia en curso.
     */
    private void sincronizarConFirebase() {
        String userId = sessionManager.getUsername();
        if (userId == null) return;

        mDatabase.child("Usuarios").child(userId).child("actividad_hoy").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Double agua = snapshot.child("agua").getValue(Double.class);
                    if (agua != null) litrosActuales = agua.floatValue();
                    actualizarUIActividad();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    /**
     * Refresca los indicadores textuales y la barra de progreso de pasos y agua en la pantalla.
     */
    private void actualizarUIActividad() {
        tvWaterCount.setText(String.format(Locale.getDefault(), "%.2fL / %.1fL", litrosActuales, OBJETIVO_AGUA));
        tvStepCount.setText(String.format(Locale.getDefault(), "%d\npasos", pasosDeHoy));
        pbSteps.setProgress(pasosDeHoy);
        tvCalories.setText(String.format(Locale.getDefault(), "%.0f\nkcal", pasosDeHoy * 0.04f));
        tvWalkingTime.setText(String.format(Locale.getDefault(), "%d\nmin", pasosDeHoy / 100));
    }

    /**
     * Carga del almacenamiento local SharedPreferences el historico de pasos para resaltar los dias completados.
     */
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

    /**
     * Modifica las dimensiones de las vistas de barra para representar graficamente los pasos semanales.
     */
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

    /**
     * Verifica el cambio de dia; si la fecha cambio, respalda los datos de ayer en Firebase y reinicia los contadores locales.
     */
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

                prefs.edit().putInt(diaAyer, pasosDeHoy).apply();

                if (userId != null) {
                    mDatabase.child("Usuarios").child(userId).child("historial").child(lastDate).child("pasos").setValue(pasosDeHoy);
                }
            }
            pasosIniciales = -1;
            pasosDeHoy = 0;
            litrosActuales = 0.0f;
            prefs.edit().putString("last_date", currentDate).apply();

            if (userId != null) mDatabase.child("Usuarios").child(userId).child("actividad_hoy").removeValue();
        }
    }

    /**
     * Mapea una constante de dia de Calendar con su respectivo componente TextView.
     *
     * @param calendarDay Codigo del dia de la semana de java.util.Calendar.
     * @return El TextView correspondiente al dia de la semana, o null si no aplica.
     */
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

    /**
     * Destaca de forma visual (colores y tipografia) el dia actual de la semana en la interfaz.
     */
    private void marcarDiaActual() {
        Calendar calendar = Calendar.getInstance();
        TextView hoy = getTextViewPorDia(calendar.get(Calendar.DAY_OF_WEEK));
        if (hoy != null) {
            hoy.setBackgroundColor(Color.BLACK);
            hoy.setTextColor(Color.WHITE);
            hoy.setTypeface(null, Typeface.BOLD);
        }
    }

    /**
     * Formatea e inyecta la fecha actual con la primera letra en mayuscula en el banner de la pantalla.
     */
    private void actualizarFecha() {
        String date = new SimpleDateFormat("EEEE, d MMM", Locale.getDefault()).format(new Date());
        tvCurrentDate.setText(date.substring(0, 1).toUpperCase() + date.substring(1));
    }

    /**
     * Recibe las actualizaciones de los sensores de hardware; calcula los pasos netos dados en el dia actual.
     *
     * @param event Objeto con los datos capturados del sensor.
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            if (pasosIniciales == -1) pasosIniciales = (int) event.values[0];
            pasosDeHoy = (int) event.values[0] - pasosIniciales;
            actualizarUIActividad();

            if (pasosDeHoy % 100 == 0) {
                guardarProgresoEnFirebase();
            }
        }
    }

    /**
     * Evento disparado al cambiar la precision del sensor (no implementado).
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    /**
     * Registra los escuchadores del sensor de pasos y refresca graficas al volver a primer plano.
     */
    @Override
    protected void onResume() {
        super.onResume();
        checkDailyReset();
        cargarHistorial();
        actualizarGrafica();
        marcarDiaActual();
        if (stepSensor != null) sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI);
    }

    /**
     * Remueve los listeners de hardware para mitigar el drenaje de bateria y respalda los ultimos datos.
     */
    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        guardarProgresoEnFirebase();
    }
}