package com.example.gymzy.general.PantallasPrincipales;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import com.example.gymzy.R;
import com.example.gymzy.general.sesion.MainActivity;
import com.google.android.material.button.MaterialButton;
import java.text.NumberFormat;
import java.util.Locale;

public class Precios extends DrawerBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_precios);
        allocateActivityTitle("Planes y Precios");

        // Configuración de moneda
        Locale locale = Locale.getDefault();
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(locale);

        // Precios base (ajustados a tu XML)
        double precioNutricion = 19.99;
        double precioEntrenador = 19.99;
        double precioCombo = 34.99;

        // Vincular TextViews (Asegúrate de poner estos IDs en el XML, ver paso abajo)
        TextView txtNutricion = findViewById(R.id.precioNutricion);
        TextView txtEntrenador = findViewById(R.id.precioEntrenador);
        TextView txtCombo = findViewById(R.id.precioCombo);

        String perMonth = locale.getLanguage().equals("es") ? "/mes" : "/month";

        // Asignar los precios formateados si los TextViews existen
        if (txtNutricion != null) txtNutricion.setText(currencyFormat.format(precioNutricion) + " " + perMonth);
        if (txtEntrenador != null) txtEntrenador.setText(currencyFormat.format(precioEntrenador) + " " + perMonth);
        if (txtCombo != null) txtCombo.setText(currencyFormat.format(precioCombo) + " " + perMonth);

        // Configurar Botón Salir (ID corregido a buttonSalir como en tu XML)
        MaterialButton btnSalir = findViewById(R.id.buttonSalir);
        if (btnSalir != null) {
            btnSalir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Volver a la actividad principal
                    Intent intent = new Intent(Precios.this, MainActivity.class);
                    startActivity(intent);
                    finish(); // Opcional: cierra esta actividad
                }
            });
        }
    }
}