package com.example.gymzy.general.roles;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.example.gymzy.R;
import com.example.gymzy.general.pantallasprincipales.menuinferior;
import com.example.gymzy.general.sesion.iniciosesion;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

/**
 * Actividad independiente encargada de gestionar y visualizar la división de usuarios por roles.
 * Alberga el TabLayout, el ViewPager2 y el botón flotante (FAB) de creación.
 * Hereda de {@link menuinferior} para integrarse con la barra de navegación de la aplicación.
 *
 * @author Gymzy Team
 * @version 1.0
 */
public class listausuariosadminactivity extends menuinferior {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private FloatingActionButton fabAgregar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflamos el archivo de diseño que contiene las pestañas y el ViewPager2
        View view = getLayoutInflater().inflate(R.layout.activity_panel_admin, null);
        setContentView(view);
        allocateActivityTitle("Gestión de Usuarios");

        // Vincular componentes del XML (Asegurando la eliminación de ivLogout antiguo de aquí)
        tabLayout = findViewById(R.id.tabLayoutAdmin);
        viewPager = findViewById(R.id.viewPagerAdmin);
        fabAgregar = findViewById(R.id.fabAgregarUsuario);

        // Configuración del adaptador para despachar los fragmentos
        viewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                if (position == 0) {
                    return listaadminfragment.newInstance("usuarios");
                } else {
                    return listaadminfragment.newInstance("profesionales");
                }
            }

            @Override
            public int getItemCount() {
                return 2;
            }
        });

        // Vincular indicadores de pestañas
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("Usuarios");
            } else {
                tab.setText("Profesionales");
            }
        }).attach();

        // El FAB redirige de forma aislada al formulario de registro
        if (fabAgregar != null) {
            fabAgregar.setOnClickListener(v -> {
                Intent intent = new Intent(listausuariosadminactivity.this, iniciosesion.class);
                startActivity(intent);
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (viewPager != null && viewPager.getAdapter() != null) {
            viewPager.getAdapter().notifyDataSetChanged();
        }
    }
}