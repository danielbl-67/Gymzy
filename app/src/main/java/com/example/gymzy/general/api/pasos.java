package com.example.gymzy.general.api;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Actividad encargada de capturar los datos brutos del sensor de hardware del dispositivo.
 * Implementa el escuchador de eventos de sensores para monitorear el contador acumulado de pasos.
 */
public class pasos extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor stepSensor;
    private int totalSteps = 0;

    /**
     * Inicializa la actividad y adquiere la referencia al servicio del sistema
     * encargado de gestionar los sensores de hardware disponibles.
     *
     * @param savedInstanceState Contiene el estado previo de los datos de la interfaz.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
    }

    /**
     * Captura las modificaciones en las lecturas de los sensores del dispositivo.
     * Extrae de forma especifica el acumulado del sensor de conteo de pasos.
     *
     * @param event Objeto contenedor de las propiedades y valores del evento del sensor.
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            // Este valor es el total de pasos desde que se encendió el móvil
            totalSteps = (int) event.values[0];
            // Aquí puedes calcular las calorías quemadas en tu app de nutrición
        }
    }

    /**
     * Evento disparado cuando se altera la precision del sensor bajo monitoreo (no implementado).
     *
     * @param sensor   Instancia del objeto del sensor modificado.
     * @param accuracy Nuevo nivel de precision asignado al hardware.
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}