package com.example.gymzy.general.PantallasPrincipales;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.example.gymzy.R;
import com.example.gymzy.general.sesion.SessionManager;

public class ListaRutina extends DrawerBaseActivity {

    private SessionManager sessionManager;
    private View bpecho, bespalda, bhombro, bbiceps, btriceps, bpierna, bgluteo, babs, bcardio;
    private TextView tvBienvenida;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflamos la vista para el Drawer
        View view = getLayoutInflater().inflate(R.layout.activity_rutinas, null);
        setContentView(view);
        allocateActivityTitle("Rutinas");

        sessionManager = new SessionManager(this);

        // Inicializar vistas
        babs = findViewById(R.id.babs);
        bpecho = findViewById(R.id.bpecho);
        bespalda = findViewById(R.id.bespalda);
        bhombro = findViewById(R.id.bhombro);
        bbiceps = findViewById(R.id.bbiceps);
        btriceps = findViewById(R.id.btriceps);
        bpierna = findViewById(R.id.bpierna);
        bgluteo = findViewById(R.id.bgluteo);
        bcardio = findViewById(R.id.bcardio);
        tvBienvenida = findViewById(R.id.tvBienvenida);

        String usuario = sessionManager.getUsername();
        tvBienvenida.setText("¡Bienvenido, " + (usuario != null ? usuario : "Atleta") + "!");

        // Configurar los Clicks
        bpecho.setOnClickListener(v -> abrirLista(4, "Pecho"));
        bespalda.setOnClickListener(v -> abrirLista(12, "Espalda"));
        bhombro.setOnClickListener(v -> abrirLista(2, "Hombros"));
        bbiceps.setOnClickListener(v -> abrirLista(1, "Bíceps"));
        btriceps.setOnClickListener(v -> abrirLista(5, "Tríceps"));
        bpierna.setOnClickListener(v -> abrirLista(9, "Piernas"));
        bgluteo.setOnClickListener(v -> abrirLista(11, "Glúteos"));
        babs.setOnClickListener(v -> abrirLista(6, "Abdominales"));
        bcardio.setOnClickListener(v -> abrirLista(15, "Cardio"));

    } // <--- EL ONCREATE TERMINA AQUÍ

    // EL MeTODO DEBE ESTAR FUERA DE ONCREATE
    private void abrirLista(int id, String nombre) {
        // Usamos 'ListaRutina.this' para asegurar el contexto correcto
        Intent intent = new Intent(ListaRutina.this, EjerciciosListaActivity.class);
        intent.putExtra("MUSCULO_ID", id);
        intent.putExtra("TITULO_MUSCULO", nombre);
        startActivity(intent);
    }
}