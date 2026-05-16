package com.example.gymzy.general.pantallasprincipales;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.example.gymzy.R;
import com.example.gymzy.general.roles.paneladminactivity;
import com.example.gymzy.general.roles.panelprofesionalactivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public abstract class menuinferior extends AppCompatActivity {

    protected BottomNavigationView bottomNavigationView;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String rolUsuario = "Usuario";

    @Override
    public void setContentView(View view) {
        View baseLayout = getLayoutInflater().inflate(R.layout.activity_drawer_base, null);
        FrameLayout container = baseLayout.findViewById(R.id.activityContainer);

        container.addView(view);
        super.setContentView(baseLayout);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        bottomNavigationView = baseLayout.findViewById(R.id.bottom_navigation);

        configurarBotonesPorRol();

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            navegacionInteligente(id);
            return true;
        });
    }

    private void configurarBotonesPorRol() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        Menu menu = bottomNavigationView.getMenu();
        MenuItem itemConfig = menu.findItem(R.id.nav_configuracion);
        MenuItem itemNutri = menu.findItem(R.id.nav_nutricion);
        MenuItem itemInicio = menu.findItem(R.id.nav_inicio);
        MenuItem itemTrain = menu.findItem(R.id.nav_entrenamiento);
        MenuItem itemPlanes = menu.findItem(R.id.nav_planes);

        db.collection("Usuarios").document(user.getUid()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String rol = documentSnapshot.getString("rol");
                        if (rol != null) {
                            rolUsuario = rol.trim();

                            boolean esUsuarioComun = rolUsuario.equalsIgnoreCase("Usuario");
                            boolean esPro = rolUsuario.equalsIgnoreCase("Nutricionista") || rolUsuario.equalsIgnoreCase("Entrenador");
                            boolean esAdmin = rolUsuario.equalsIgnoreCase("Admin");

                            // ⚡ CASO 1: USUARIO NORMAL (Muestra los 5 iconos en tu orden exacto)
                            if (esUsuarioComun) {
                                if (itemConfig != null) { itemConfig.setVisible(true); itemConfig.setTitle("Config"); itemConfig.setIcon(R.drawable.ic_usuario); }
                                if (itemNutri != null) { itemNutri.setVisible(true); itemNutri.setTitle("Nutrición"); itemNutri.setIcon(R.drawable.ic_logoredondo); }
                                if (itemInicio != null) { itemInicio.setVisible(true); itemInicio.setTitle("Inicio"); itemInicio.setIcon(R.drawable.ic_hogar); }
                                if (itemTrain != null) { itemTrain.setVisible(true); itemTrain.setTitle("Rutinas"); itemTrain.setIcon(R.drawable.ic_musculo); }
                                if (itemPlanes != null) { itemPlanes.setVisible(true); itemPlanes.setTitle("Planes"); itemPlanes.setIcon(R.drawable.ic_logoredondo); }
                            }

                            // ⚡ CASO 2: ADMINISTRADOR (Casita fija en el centro)
                            else if (esAdmin) {
                                // Apagamos los botones que corresponden al usuario común para limpiar espacio
                                if (itemConfig != null) itemConfig.setVisible(false);
                                if (itemTrain != null) itemTrain.setVisible(false);

                                // El botón 2 (Izquierda) se convierte en sus Listas
                                if (itemNutri != null) {
                                    itemNutri.setVisible(true);
                                    itemNutri.setTitle("Listas");
                                    itemNutri.setIcon(android.R.drawable.ic_menu_agenda);
                                }
                                // El botón 3 (CENTRO) SE QUEDA COMO LA CASITA
                                if (itemInicio != null) {
                                    itemInicio.setVisible(true);
                                    itemInicio.setTitle("Inicio");
                                    itemInicio.setIcon(R.drawable.ic_hogar);
                                }
                                // El botón 5 (Derecha) se convierte en su Panel maestro
                                if (itemPlanes != null) {
                                    itemPlanes.setVisible(true);
                                    itemPlanes.setTitle("Panel");
                                    itemPlanes.setIcon(android.R.drawable.ic_menu_manage);
                                }
                            }

                            else if (esPro) {
                                String tipoLista = rolUsuario.equalsIgnoreCase("Nutricionista") ? "Pacientes" : "Alumnos";
                                String tipoPlan = rolUsuario.equalsIgnoreCase("Nutricionista") ? "Dietas" : "Rutinas";
                                int iconoLista = rolUsuario.equalsIgnoreCase("Nutricionista") ? android.R.drawable.ic_menu_agenda : android.R.drawable.ic_menu_myplaces;
                                int iconoPlan = rolUsuario.equalsIgnoreCase("Nutricionista") ? android.R.drawable.ic_menu_today : android.R.drawable.ic_menu_edit;

                                // Ocultamos los botones del usuario común
                                if (itemConfig != null) itemConfig.setVisible(false);
                                if (itemTrain != null) itemTrain.setVisible(false);

                                // El botón 2 (Izquierda) pasa a ser la Lista de Alumnos/Pacientes
                                if (itemNutri != null) {
                                    itemNutri.setVisible(true);
                                    itemNutri.setTitle(tipoLista);
                                    itemNutri.setIcon(iconoLista);
                                }
                                // El botón 3 (CENTRO EXPANSIBLE) SE QUEDA OBLIGATORIAMENTE COMO LA CASITA
                                if (itemInicio != null) {
                                    itemInicio.setVisible(true);
                                    itemInicio.setTitle("Inicio");
                                    itemInicio.setIcon(R.drawable.ic_hogar); // Usa el drawable de tu casita
                                }
                                // El botón 5 (Derecha) pasa a ser el creador de Planes/Rutinas
                                if (itemPlanes != null) {
                                    itemPlanes.setVisible(true);
                                    itemPlanes.setTitle(tipoPlan);
                                    itemPlanes.setIcon(iconoPlan);
                                }
                            }

                            marcarBotonActivo();
                        }
                    }
                });
    }

    private void navegacionInteligente(int id) {
        Intent intent = null;

        // 1. Botón de Configuración (Solo usuarios normales)
        if (id == R.id.nav_configuracion) {
            if (!(this instanceof configuracionactivity)) intent = new Intent(this, configuracionactivity.class);
        }
        // 2. Botón de Nutrición (Usuarios) o Listas (Pros/Admin)
        else if (id == R.id.nav_nutricion) {
            if (rolUsuario.equalsIgnoreCase("Usuario")) {
                if (!(this instanceof consulta)) intent = new Intent(this, consulta.class);
            } else if (rolUsuario.equalsIgnoreCase("Admin")) {
                if (!(this instanceof paneladminactivity)) intent = new Intent(this, paneladminactivity.class);
            } else {
                if (!(this instanceof panelprofesionalactivity)) intent = new Intent(this, panelprofesionalactivity.class);
            }
        }
        // 3. BOTÓN CENTRAL: LA CASITA
        else if (id == R.id.nav_inicio) {
            if (rolUsuario.equalsIgnoreCase("Nutricionista") || rolUsuario.equalsIgnoreCase("Entrenador")) {
                if (!(this instanceof panelprofesionalactivity)) intent = new Intent(this, panelprofesionalactivity.class);
            } else if (rolUsuario.equalsIgnoreCase("Admin")) {
                if (!(this instanceof paneladminactivity)) intent = new Intent(this, paneladminactivity.class);
            } else {
                if (!(this instanceof homeactivity)) intent = new Intent(this, homeactivity.class);
            }
        }
        // 4. Botón de Entrenamiento (Solo usuarios normales)
        else if (id == R.id.nav_entrenamiento) {
            if (!(this instanceof listarutina)) intent = new Intent(this, listarutina.class);
        }
        // 5. Botón de Planes (Usuarios) o Panel/Asignar (Pros/Admin)
        else if (id == R.id.nav_planes) {
            if (rolUsuario.equalsIgnoreCase("Usuario")) {
                if (!(this instanceof precios)) intent = new Intent(this, precios.class);
            } else if (rolUsuario.equalsIgnoreCase("Admin")) {
                if (!(this instanceof paneladminactivity)) intent = new Intent(this, paneladminactivity.class);
            } else {
                if (!(this instanceof panelprofesionalactivity)) intent = new Intent(this, panelprofesionalactivity.class);
            }
        }

        if (intent != null) {
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(0, 0);
        }
    }

    private void marcarBotonActivo() {
        Menu m = bottomNavigationView.getMenu();
        if (this instanceof homeactivity || (this instanceof panelprofesionalactivity && m.findItem(R.id.nav_nutricion) == null)) {
            if (m.findItem(R.id.nav_inicio) != null) m.findItem(R.id.nav_inicio).setChecked(true);
        } else if (this instanceof listarutina && m.findItem(R.id.nav_entrenamiento) != null) {
            m.findItem(R.id.nav_entrenamiento).setChecked(true);
        } else if (this instanceof configuracionactivity && m.findItem(R.id.nav_configuracion) != null) {
            m.findItem(R.id.nav_configuracion).setChecked(true);
        } else if (this instanceof consulta && m.findItem(R.id.nav_nutricion) != null) {
            m.findItem(R.id.nav_nutricion).setChecked(true);
        } else if (this instanceof precios && m.findItem(R.id.nav_planes) != null) {
            m.findItem(R.id.nav_planes).setChecked(true);
        }
    }

    protected void allocateActivityTitle(String titleString) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setTitle("");
        }
    }
}