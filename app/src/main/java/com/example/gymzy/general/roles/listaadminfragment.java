package com.example.gymzy.general.roles;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gymzy.R;
import com.example.gymzy.general.usuarios.usuario;
import com.example.gymzy.general.usuarios.usuariosAdminAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

/**
 * Fragmento que muestra listas filtradas de usuarios o profesionales para el modulo de administracion.
 * Utiliza un argumento dinamico para determinar que tipo de roles debe listar desde Firestore.
 */
public class listaadminfragment extends Fragment {

    private RecyclerView recyclerView;
    private usuariosAdminAdapter adapter;
    private List<usuario> listaUsuarios = new ArrayList<>();
    private FirebaseFirestore db;
    private String tipoFiltro;

    /**
     * Construye una nueva instancia parametrizada del fragmento aplicando un filtro especifico.
     *
     * @param tipoFiltro Cadena de texto que define el grupo a mostrar ("usuarios" o "profesionales").
     * @return Instancia configurada de listaadminfragment.
     */
    public static listaadminfragment newInstance(String tipoFiltro) {
        listaadminfragment fragment = new listaadminfragment();
        Bundle args = new Bundle();
        args.putString("FILTRO", tipoFiltro);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Infla el diseño visual, recupera los argumentos de filtrado e inicializa el adaptador del RecyclerView.
     *
     * @param inflater           Objeto encargado de inflar la vista en el fragmento.
     * @param container          Contenedor contenedor del fragmento.
     * @param savedInstanceState Estado previamente almacenado de la instancia.
     * @return Vista raiz del fragmento.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lista_admin, container, false);

        if (getArguments() != null) {
            tipoFiltro = getArguments().getString("FILTRO");
        }

        db = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.recyclerViewAdminTab);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inicializamos pasando la lista y la interfaz de escucha de borrado
        adapter = new usuariosAdminAdapter(listaUsuarios, () -> {
            // Aquí puedes meter lógicas extras si se vacía la lista
        });
        recyclerView.setAdapter(adapter);

        cargarDatosDesdeFirestore();

        return view;
    }

    /**
     * Consulta la coleccion completa de usuarios en Firestore y discrimina los registros localmente
     * agregando a la lista solo aquellos que coincidan con las restricciones del filtro configurado.
     */
    public void cargarDatosDesdeFirestore() {
        db.collection("Usuarios")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listaUsuarios.clear();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        usuario usuario = document.toObject(usuario.class);
                        String rol = usuario.rol;

                        if (rol != null) {
                            if (tipoFiltro.equals("usuarios") && rol.equalsIgnoreCase("Usuario")) {
                                listaUsuarios.add(usuario);
                            } else if (tipoFiltro.equals("profesionales") && (rol.equalsIgnoreCase("Nutricionista") || rol.equalsIgnoreCase("Entrenador"))) {
                                listaUsuarios.add(usuario);
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                });
    }
}