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

public class paneladminactivity extends menuinferior {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private FloatingActionButton fabAgregar;
    private ImageView ivLogout;
    private registrarsesiones sessionManager;

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

        // Sincronizar pestañas
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

    @Override
    protected void onResume() {
        super.onResume();
        if (viewPager != null && viewPager.getAdapter() != null) {
            viewPager.getAdapter().notifyDataSetChanged();
        }
    }
}