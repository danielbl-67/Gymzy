package com.example.gymzy.general.pantallasprincipales;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.example.gymzy.R;

// Importaciones dinámicas del ecosistema de roles y paneles independientes
import com.example.gymzy.general.roles.listaclientesactivity;
import com.example.gymzy.general.roles.listausuariosadminactivity;
import com.example.gymzy.general.roles.paneladminactivity;
import com.example.gymzy.general.roles.panelprofesionalactivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Clase abstracta base que implementa el menú de navegación inferior (BottomNavigationView).
 * Estructura de forma reactiva los iconos y títulos analizando el rol del usuario autenticado en Cloud Firestore,
 * resolviendo bucles redundantes redirigiendo de forma directa hacia las vistas analíticas independientes.
 *
 * @author Gymzy Team
 * @version 2.1
 */
public abstract class menuinferior extends AppCompatActivity {

    /** Contenedor visual del menú de navegación inferior. */
    protected BottomNavigationView bottomNavigationView;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String rolUsuario = "Usuario";

    /**
     * Reemplaza el contenedor base, infla el diseño del menú inferior, inicializa los servicios
     * de Firebase y configura los escuchadores de selección de ítems.
     * <p>
     * Realiza las siguientes tareas de configuración:
     * <ul>
     * <li>1. Infla y encapsula la jerarquía visual nativa sobre el activityContainer base.</li>
     * <li>2. Acopla de forma síncrona la vista hija suministrada en el FrameLayout.</li>
     * <li>3. Inicializar instancias globales de autenticación y base de datos distribuida.</li>
     * <li>4. Arrancar los escuchadores dinámicos de eventos sobre el BottomNavigationView.</li>
     * </ul>
     *
     * @param view Vista de la actividad hija que se acoplará dentro del contenedor principal.
     */
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

    /**
     * Consulta el rol del usuario en Cloud Firestore y reestructura el menú inferior,
     * cambiando la visibilidad, los iconos y las etiquetas de texto de acuerdo al tipo de perfil.
     */
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

                            if (esUsuarioComun) {
                                if (itemConfig != null) { itemConfig.setVisible(true); itemConfig.setTitle("Config"); itemConfig.setIcon(R.drawable.ic_usuario); }
                                if (itemNutri != null) { itemNutri.setVisible(true); itemNutri.setTitle("Nutrición"); itemNutri.setIcon(R.drawable.ic_logoredondo); }
                                if (itemInicio != null) { itemInicio.setVisible(true); itemInicio.setTitle("Inicio"); itemInicio.setIcon(R.drawable.ic_hogar); }
                                if (itemTrain != null) { itemTrain.setVisible(true); itemTrain.setTitle("Rutinas"); itemTrain.setIcon(R.drawable.ic_musculo); }
                                if (itemPlanes != null) { itemPlanes.setVisible(true); itemPlanes.setTitle("Planes"); itemPlanes.setIcon(R.drawable.ic_logoredondo); }
                            }

                            else if (esAdmin) {
                                if (itemConfig != null) itemConfig.setVisible(false);
                                if (itemTrain != null) itemTrain.setVisible(false);

                                if (itemNutri != null) {
                                    itemNutri.setVisible(true);
                                    itemNutri.setTitle("Listas");
                                    itemNutri.setIcon(android.R.drawable.ic_menu_agenda);
                                }
                                if (itemInicio != null) {
                                    itemInicio.setVisible(true);
                                    itemInicio.setTitle("Inicio");
                                    itemInicio.setIcon(R.drawable.ic_hogar);
                                }
                                if (itemPlanes != null) {
                                    itemPlanes.setVisible(false); // Ocultado para evitar duplicaciones con la nueva pantalla limpia
                                }
                            }

                            else if (esPro) {
                                String tipoLista = rolUsuario.equalsIgnoreCase("Nutricionista") ? "Pacientes" : "Alumnos";
                                String tipoPlan = rolUsuario.equalsIgnoreCase("Nutricionista") ? "Dietas" : "Rutinas";
                                int iconoLista = rolUsuario.equalsIgnoreCase("Nutricionista") ? android.R.drawable.ic_menu_agenda : android.R.drawable.ic_menu_myplaces;
                                int iconoPlan = rolUsuario.equalsIgnoreCase("Nutricionista") ? android.R.drawable.ic_menu_today : android.R.drawable.ic_menu_edit;

                                if (itemConfig != null) itemConfig.setVisible(false);
                                if (itemTrain != null) itemTrain.setVisible(false);

                                if (itemNutri != null) {
                                    itemNutri.setVisible(true);
                                    itemNutri.setTitle(tipoLista);
                                    itemNutri.setIcon(iconoLista);
                                }
                                if (itemInicio != null) {
                                    itemInicio.setVisible(true);
                                    itemInicio.setTitle("Inicio");
                                    itemInicio.setIcon(R.drawable.ic_hogar);
                                }
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

    /**
     * Gestiona el enrutamiento y cambio de actividades en base al ID del elemento seleccionado
     * y al rol asignado al usuario activo, resolviendo bucles infinitos en perfiles de control.
     *
     * @param id Identificador del recurso (ID del item de menu) seleccionado en la barra inferior.
     */
    private void navegacionInteligente(int id) {
        Intent intent = null;

        if (id == R.id.nav_configuracion) {
            if (!(this instanceof configuracionactivity)) intent = new Intent(this, configuracionactivity.class);
        }
        else if (id == R.id.nav_nutricion) {
            if (rolUsuario.equalsIgnoreCase("Usuario")) {
                if (!(this instanceof consulta)) intent = new Intent(this, consulta.class);
            } else if (rolUsuario.equalsIgnoreCase("Admin")) {
                // ⚡ FIJADO: El Admin viaja de forma limpia a su actividad de fragmentos dedicada
                if (!(this instanceof listausuariosadminactivity)) intent = new Intent(this, listausuariosadminactivity.class);
            } else {
                // ⚡ FIJADO: El Profesional abre de forma directa su recycler de alumnos vinculados
                if (!(this instanceof listaclientesactivity)) intent = new Intent(this, listaclientesactivity.class);
            }
        }
        else if (id == R.id.nav_inicio) {
            if (rolUsuario.equalsIgnoreCase("Nutricionista") || rolUsuario.equalsIgnoreCase("Entrenador")) {
                if (!(this instanceof panelprofesionalactivity)) intent = new Intent(this, panelprofesionalactivity.class);
            } else if (rolUsuario.equalsIgnoreCase("Admin")) {
                if (!(this instanceof paneladminactivity)) intent = new Intent(this, paneladminactivity.class);
            } else {
                if (!(this instanceof homeactivity)) intent = new Intent(this, homeactivity.class);
            }
        }
        else if (id == R.id.nav_entrenamiento) {
            if (!(this instanceof listarutina)) intent = new Intent(this, listarutina.class);
        }
        else if (id == R.id.nav_planes) {
            if (rolUsuario.equalsIgnoreCase("Usuario")) {
                if (!(this instanceof precios)) intent = new Intent(this, precios.class);
            } else if (rolUsuario.equalsIgnoreCase("Admin")) {
                if (!(this instanceof paneladminactivity)) intent = new Intent(this, paneladminactivity.class);
            } else {
                android.widget.Toast.makeText(this, "Asignador de Planes: Próximamente", android.widget.Toast.LENGTH_SHORT).show();
            }
        }

        if (intent != null) {
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(0, 0);
        }
    }

    /**
     * Remarca visualmente de manera forzada el boton correspondiente a la actividad que se esta
     * mostrando en primer plano en la pantalla del dispositivo, controlando los estados de las subclases.
     */
    private void marcarBotonActivo() {
        Menu m = bottomNavigationView.getMenu();
        if (m == null) return;

        if (this instanceof homeactivity || this instanceof panelprofesionalactivity || this instanceof paneladminactivity) {
            if (m.findItem(R.id.nav_inicio) != null) m.findItem(R.id.nav_inicio).setChecked(true);
        } else if (this instanceof listarutina && m.findItem(R.id.nav_entrenamiento) != null) {
            m.findItem(R.id.nav_entrenamiento).setChecked(true);
        } else if (this instanceof configuracionactivity && m.findItem(R.id.nav_configuracion) != null) {
            m.findItem(R.id.nav_configuracion).setChecked(true);
        } else if (this instanceof consulta && m.findItem(R.id.nav_nutricion) != null) {
            m.findItem(R.id.nav_nutricion).setChecked(true);
        } else if ((this instanceof listaclientesactivity || this instanceof listausuariosadminactivity) && m.findItem(R.id.nav_nutricion) != null) {
            // ⚡ FIJADO: Ilumina la pestaña analítica correcta (Pacientes/Listas) al abrir las subpantallas
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