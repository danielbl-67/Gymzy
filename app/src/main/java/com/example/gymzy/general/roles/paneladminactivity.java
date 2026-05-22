package com.example.gymzy.general.roles;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.example.gymzy.R;
import com.example.gymzy.general.pantallasprincipales.menuinferior;
import com.example.gymzy.general.sesion.autenticacion;
import com.example.gymzy.general.pantallasprincipales.registrarsesiones;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Actividad que representa la pantalla de inicio limpia y minimalista para el Administrador.
 * Hereda de {@link menuinferior} para mantener el menú inferior activo, liberando el centro
 * de la pantalla de las listas y pestañas complejas.
 *
 * @author Gymzy Team
 * @version 2.5
 */
public class paneladminactivity extends menuinferior {

    private TextView tvRolBadge;
    private View btnSalir;
    private registrarsesiones sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflamos la nueva vista limpia para el inicio del administrador
        View view = getLayoutInflater().inflate(R.layout.layout_home_admin, null);
        setContentView(view);
        allocateActivityTitle("Panel de Administración");

        sessionManager = new registrarsesiones(this);

        tvRolBadge = findViewById(R.id.tvRolBadgeAdmin);
        btnSalir = findViewById(R.id.btnCerrarSesionAdmin);

        if (tvRolBadge != null) {
            tvRolBadge.setText("SISTEMA DE ADMINISTRACIÓN GLOBAL");
        }

        if (btnSalir != null) {
            btnSalir.setOnClickListener(v -> {
                FirebaseAuth.getInstance().signOut();
                sessionManager.logout();

                Intent intent = new Intent(paneladminactivity.this, autenticacion.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }
    }
}