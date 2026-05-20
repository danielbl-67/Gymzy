package com.example.gymzy.general.pantallasprincipales;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.example.gymzy.R;

/**
 * Actividad que muestra la cartelera de suscripciones, membresias y pasarelas de planes.
 * Hereda de {@link menuinferior} para integrarse dentro del marco global de navegacion.
 */
public class precios extends menuinferior {

    private Button btnVolver, btnNutricion, btnEntrenador, btnVIP;

    /**
     * Infla la vista de tarifas, acopla los botones e inyecta la logica de seleccion
     * de planes comerciales mediante lanzadores de Toast informativos.
     *
     * @param savedInstanceState Contiene el estado previo de los datos de la interfaz.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Inflamos el diseño adaptado dentro del contenedor del menú base de la app
        View view = getLayoutInflater().inflate(R.layout.activity_precios, null);
        setContentView(view);
        allocateActivityTitle("Planes y Tarifas");

        // 2. Vincular componentes con los IDs del XML
        btnVolver = findViewById(R.id.buttonSalir);
        btnNutricion = findViewById(R.id.btnSuscribirNutricion);
        btnEntrenador = findViewById(R.id.btnSuscribirEntrenador);
        btnVIP = findViewById(R.id.btnSuscribirVIP);

        // 3. Configurar escuchadores de eventos de clic simples y limpios (Evita errores de clases anónimas)
        if (btnVolver != null) {
            btnVolver.setOnClickListener(v -> finish());
        }

        if (btnNutricion != null) {
            btnNutricion.setOnClickListener(v -> {
                Toast.makeText(precios.this, "Procesando suscripción: Plan Nutrición (19.99€)", Toast.LENGTH_SHORT).show();
            });
        }

        if (btnEntrenador != null) {
            btnEntrenador.setOnClickListener(v -> {
                Toast.makeText(precios.this, "Procesando suscripción: Plan Entrenador (19.99€)", Toast.LENGTH_SHORT).show();
            });
        }

        if (btnVIP != null) {
            btnVIP.setOnClickListener(v -> {
                Toast.makeText(precios.this, "Procesando suscripción: Plan VIP Todo Incluido (34.99€)", Toast.LENGTH_SHORT).show();
            });
        }
    }
}