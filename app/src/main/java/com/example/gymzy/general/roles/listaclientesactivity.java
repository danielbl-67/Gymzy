package com.example.gymzy.general.roles;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymzy.R;
import com.example.gymzy.general.pantallasprincipales.menuinferior;
import com.example.gymzy.general.usuarios.usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Actividad encargada de descargar, estructurar y renderizar de forma optimizada el catálogo de usuarios
 * vinculados al profesional que mantiene la sesión activa.
 * Hereda directamente de {@link menuinferior} integrando la lógica en la barra global y abstrayendo
 * los filtros de datos según el rol secundario de Firestore.
 *
 * @author Gymzy Team
 * @version 2.0
 */
public class listaclientesactivity extends menuinferior {

    /**
     * Campo de texto destinado a mostrar el título de la pantalla (dinámico según rol).
     */
    private TextView tvTituloHeader;

    /**
     * Campo de texto alternativo visible únicamente cuando no se retornan registros en la consulta.
     */
    private TextView tvListaVacia;

    /**
     * Contenedor visual estructurado para desplegar la lista de elementos en cascada.
     */
    private RecyclerView rvClientes;

    /**
     * Componente indicador visual de carga síncrona/asíncrona.
     */
    private ProgressBar progressBar;

    /**
     * Referencia del servicio de persistencia y consultas Cloud Firestore.
     */
    private FirebaseFirestore db;

    /**
     * Estructura lineal en memoria que almacena los objetos serializados de tipo {@link usuario}.
     */
    private List<usuario> listaclientes;

    /**
     * Adaptador puente encargado de enlazar la colección de datos con los ViewHolders del RecyclerView.
     */
    private clientesadapter adapter;

    /**
     * Inicializa el ciclo de vida de la actividad. Envía la petición de inflado estructural a la
     * superclase, enlaza componentes visuales por ID, inicializa la persistencia e inicia
     * la validación de roles de seguridad.
     *
     * @param savedInstanceState Contenedor de persistencia de estado de la UI frente a recreaciones.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflamos el diseño usando la estructura del menú inferior
        View view = getLayoutInflater().inflate(R.layout.activity_lista_clientes, null);
        setContentView(view);

        initUI();
        setupRecyclerView();

        db = FirebaseFirestore.getInstance();
        verificarRolYBuscarClientes();
    }

    /**
     * Mapea y enlaza las referencias locales a los componentes visuales inyectados mediante el XML.
     * Oculta el botón tradicional de retroceso delegando el control al ecosistema del menú inferior.
     */
    private void initUI() {
        tvTituloHeader = findViewById(R.id.tvTituloListaClientes);
        tvListaVacia = findViewById(R.id.tvListaVacia);
        rvClientes = findViewById(R.id.rvClientesPro);
        progressBar = findViewById(R.id.progressBarClientesPro);

        ImageView ivBtnAtras = findViewById(R.id.ivAtrasLista);
        if (ivBtnAtras != null) ivBtnAtras.setVisibility(View.GONE);
    }

    /**
     * Inicializa el listado en memoria y configura el {@link RecyclerView} asignándole un
     * administrador de diseño de tipo {@link LinearLayoutManager} junto con el adaptador personalizado.
     */
    private void setupRecyclerView() {
        if (rvClientes != null) {
            listaclientes = new ArrayList<>();
            adapter = new clientesadapter(listaclientes);
            rvClientes.setLayoutManager(new LinearLayoutManager(this));
            rvClientes.setAdapter(adapter);
        }
    }

    /**
     * Extrae el identificador único universal (UID) de Firebase Auth. Si la sesión es válida,
     * consume el documento del usuario en Firestore para modificar de forma semántica la interfaz
     * ("MIS PACIENTES" para Nutricionistas, "MIS ALUMNOS" para Entrenadores) y dispara la búsqueda extendida.
     */
    private void verificarRolYBuscarClientes() {
        String uidPro = FirebaseAuth.getInstance().getUid();
        if (uidPro == null) return;

        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        db.collection("Usuarios").document(uidPro).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String rol = doc.getString("rol");
                        String codigoPro = doc.getString("codigoVinculacion");

                        if (tvTituloHeader != null && rol != null) {
                            if ("Nutricionista".equalsIgnoreCase(rol)) tvTituloHeader.setText("MIS PACIENTES");
                            else if ("Entrenador".equalsIgnoreCase(rol)) tvTituloHeader.setText("MIS ALUMNOS");
                        }

                        if (codigoPro != null && !codigoPro.isEmpty()) {
                            obtenerUsuariosVinculados(codigoPro);
                        } else {
                            if (progressBar != null) progressBar.setVisibility(View.GONE);
                            Toast.makeText(this, "Código de vinculación inactivo", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * Ejecuta una consulta indexada compuesta en la colección de "Usuarios". Filtra en tiempo
     * de ejecución aquellos registros que tengan asignado estrictamente el rol de "Usuario" y cuyo
     * token de propiedad `codigoVinculacion` sea equivalente al parámetro suministrado.
     * Limpia las listas, serializa el JSON y refresca el adaptador visualmente.
     *
     * @param codigoVinculacion Token de vinculación textual único extraído del perfil profesional.
     */
    private void obtenerUsuariosVinculados(String codigoVinculacion) {
        db.collection("Usuarios")
                .whereEqualTo("codigoVinculacion", codigoVinculacion)
                .whereEqualTo("rol", "Usuario")
                .get()
                .addOnSuccessListener(querySnapshots -> {
                    listaclientes.clear();
                    for (QueryDocumentSnapshot doc : querySnapshots) {
                        usuario cliente = doc.toObject(usuario.class);
                        if (cliente != null) listaclientes.add(cliente);
                    }

                    adapter.notifyDataSetChanged();
                    if (progressBar != null) progressBar.setVisibility(View.GONE);

                    if (listaclientes.isEmpty()) {
                        if (tvListaVacia != null) tvListaVacia.setVisibility(View.VISIBLE);
                    } else {
                        if (tvListaVacia != null) tvListaVacia.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(e -> {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    Log.e("GYMZY", "Error en consulta de vinculación", e);
                });
    }
}