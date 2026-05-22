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

public class listaadminfragment extends Fragment {

    private RecyclerView recyclerView;
    private usuariosAdminAdapter adapter;
    private List<usuario> listaUsuarios = new ArrayList<>();
    private FirebaseFirestore db;
    private String tipoFiltro;

    public static listaadminfragment newInstance(String tipoFiltro) {
        listaadminfragment fragment = new listaadminfragment();
        Bundle args = new Bundle();
        args.putString("FILTRO", tipoFiltro);
        fragment.setArguments(args);
        return fragment;
    }

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

        adapter = new usuariosAdminAdapter(listaUsuarios, () -> {});
        recyclerView.setAdapter(adapter);

        cargarDatosDesdeFirestore();

        return view;
    }

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