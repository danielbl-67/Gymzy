package com.example.gymzy.general.pantallasprincipales;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.example.gymzy.R;

/**
 * Actividad que muestra las diferentes categorias de grupos musculares disponibles.
 * Hereda de {@link menuinferior} y permite al usuario seleccionar una zona para ver sus ejercicios.
 */
public class listarutina extends menuinferior {

    private registrarsesiones sessionManager;
    private View bpecho, bespalda, bhombro, bbiceps, btriceps, bpierna, bgluteo, babs, bcardio;
    private TextView tvBienvenida;

    /**
     * Infla el diseño de rutinas, inicializa las tarjetas de los musculos,
     * recupera el nombre de usuario local y asigna los escuchadores de clic.
     *
     * @param savedInstanceState Contiene el estado previo de los datos de la interfaz.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflamos la vista para el Drawer (Menú lateral)
        View view = getLayoutInflater().inflate(R.layout.activity_rutinas, null);
        setContentView(view);
        allocateActivityTitle("Rutinas");

        sessionManager = new registrarsesiones(this);

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

        // Configurar los Clicks pasándoles el nombre en el idioma base (Español)
        bpecho.setOnClickListener(v -> abrirLista(4, "Pecho"));
        bespalda.setOnClickListener(v -> abrirLista(12, "Espalda"));
        bhombro.setOnClickListener(v -> abrirLista(2, "Hombros"));
        bbiceps.setOnClickListener(v -> abrirLista(1, "Bíceps"));
        btriceps.setOnClickListener(v -> abrirLista(5, "Tríceps"));
        bpierna.setOnClickListener(v -> abrirLista(9, "Piernas"));
        bgluteo.setOnClickListener(v -> abrirLista(11, "Glúteos"));
        babs.setOnClickListener(v -> abrirLista(6, "Abdominales"));
        bcardio.setOnClickListener(v -> abrirLista(15, "Cardio"));
    }

    /**
     * Configura y lanza la actividad contenedora de ejercicios inyectando los parametros de filtrado.
     *
     * @param id     Identificador numerico de la categoria muscular.
     * @param nombre Cadena de texto con el nombre descriptivo del grupo seleccionado.
     */
    private void abrirLista(int id, String nombre) {
        Intent intent = new Intent(listarutina.this, ejercicioslistaactivity.class);
        intent.putExtra("MUSCULO_ID", id);
        intent.putExtra("TITULO_MUSCULO", nombre);
        startActivity(intent);
    }
}