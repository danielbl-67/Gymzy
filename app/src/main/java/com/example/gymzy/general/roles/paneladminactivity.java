package com.example.gymzy.general.roles;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.example.gymzy.R;
import com.example.gymzy.general.pantallasprincipales.menuinferior;
import com.example.gymzy.general.sesion.autenticacion;
import com.example.gymzy.general.pantallasprincipales.registrarsesiones;
import com.example.gymzy.general.sesion.iniciosesion;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Actividad que representa el Panel de Control del Administrador general de la plataforma.
 * Hereda de {@link menuinferior} e implementa una interfaz de navegación por pestañas (Tabs)
 * mediante ViewPager2. Permite supervisar de forma separada las cuentas de clientes y profesionales,
 * dar de alta nuevos usuarios y gestionar el cierre de sesión seguro.
 */
public class paneladminactivity extends menuinferior {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private FloatingActionButton fabAgregar;
    private ImageView ivLogout;
    private registrarsesiones sessionManager;

    /**
     * Metodo de ciclo de vida que inicializa el panel de administración.
     * <p>
     * Realiza las siguientes tareas de configuración:
     * <ul>
     *   <li>1. Infla el diseño XML de la vista de administración en el contenedor base.</li>
     *   <li>2. Enlaza los componentes del archivo de diseño (Tabs, ViewPager, FAB, Botones).</li>
     *   <li>3. Acopla el {@link FragmentStateAdapter} al ViewPager2 para despachar las sub-vistas.</li>
     *   <li>4. Sincroniza los títulos del {@link TabLayout} de manera posicional mediante un {@link TabLayoutMediator}.</li>
     *   <li>5. Asigna los listeners de navegación para la creación de cuentas y cierre de sesión.</li>
     * </ul>
     *
     * @param savedInstanceState Si la actividad se recrea, este objeto contiene el estado previo de los datos.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflamos el diseño específico del admin dentro de la estructura base
        android.view.View view = getLayoutInflater().inflate(R.layout.activity_panel_admin, null);
        setContentView(view);
        allocateActivityTitle("Panel de Administración");

        sessionManager = new registrarsesiones(this);

        // Vincular componentes del XML
        tabLayout = findViewById(R.id.tabLayoutAdmin);
        viewPager = findViewById(R.id.viewPagerAdmin);
        fabAgregar = findViewById(R.id.fabAgregarUsuario);
        ivLogout = findViewById(R.id.ivAdminLogout);

        // Configuración del ViewPager2 para las pestañas de Usuarios/Profesionales
        viewPager.setAdapter(new FragmentStateAdapter(this) {
            /**
             * Instancia el fragmento correspondiente según la pestaña seleccionada en el control.
             *
             * @param position Índice de la pestaña activa (0 para usuarios, cualquier otro para profesionales).
             * @return Una nueva instancia parametrizada de {@link listaadminfragment}.
             */
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                if (position == 0) {
                    return listaadminfragment.newInstance("usuarios");
                } else {
                    return listaadminfragment.newInstance("profesionales");
                }
            }

            /**
             * Devuelve la cantidad fija de pestañas asignadas al panel de administración.
             *
             * @return Total de elementos del adaptador (siempre devuelve 2).
             */
            @Override
            public int getItemCount() {
                return 2;
            }
        });

        // Sincronizar pestañas e inyectar las etiquetas de texto
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("Usuarios");
            } else {
                tab.setText("Profesionales");
            }
        }).attach();

        // El botón flotante abre el flujo para crear una nueva cuenta
        fabAgregar.setOnClickListener(v -> {
            Intent intent = new Intent(paneladminactivity.this, iniciosesion.class);
            startActivity(intent);
        });

        // ⚡ ACCIÓN: Cierre de sesión instantáneo del Administrador
        if (ivLogout != null) {
            ivLogout.setOnClickListener(v -> {
                FirebaseAuth.getInstance().signOut();
                sessionManager.logout();

                Intent intent = new Intent(paneladminactivity.this, autenticacion.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }
    }

    /**
     * Metodo de ciclo de vida que se dispara cuando la actividad vuelve al primer plano.
     * Fuerza la actualización asíncrona de los datos del adaptador del ViewPager2 para
     * reflejar de inmediato cualquier alta o baja de usuarios ocurrida en pantallas posteriores.
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (viewPager != null && viewPager.getAdapter() != null) {
            viewPager.getAdapter().notifyDataSetChanged();
        }
    }
}