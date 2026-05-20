package com.example.gymzy.general.roles;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gymzy.R;
import com.example.gymzy.general.usuarios.usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class listaclientesfragment extends Fragment {

    private TextView tvTituloLista;
    private RecyclerView rvClientes;
    private ProgressBar progressBar;
    private FirebaseFirestore db;
    private List<usuario> listaclientes;
    private clientesadapter adapter;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lista_clientes, container, false);

        tvTituloLista = view.findViewById(R.id.tvTituloListaClientes);
        rvClientes = view.findViewById(R.id.rvClientesPro);
        progressBar = view.findViewById(R.id.progressBarClientesPro);

        db = FirebaseFirestore.getInstance();
        listaclientes = new ArrayList<>();

        rvClientes.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new clientesadapter(listaclientes);
        rvClientes.setAdapter(adapter);

        cargarClientesDelProfesional();

        return view;
    }

    private void cargarClientesDelProfesional() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;

        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        String uidPro = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("Usuarios").document(uidPro).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String rol = documentSnapshot.getString("rol");
                        String codigoPro = documentSnapshot.getString("codigoVinculacion");

                        if (tvTituloLista != null && rol != null) {
                            if ("Nutricionista".equalsIgnoreCase(rol)) {
                                tvTituloLista.setText("MIS PACIENTES");
                            } else {
                                tvTituloLista.setText("MIS ALUMNOS");
                            }
                        }

                        if (codigoPro != null && !codigoPro.isEmpty()) {
                            db.collection("Usuarios")
                                    .whereEqualTo("codigoVinculacion", codigoPro)
                                    .whereEqualTo("rol", "Usuario")
                                    .get()
                                    .addOnSuccessListener(queryDocumentSnapshots -> {
                                        if (progressBar != null) progressBar.setVisibility(View.GONE);
                                        listaclientes.clear();

                                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                            usuario cliente = doc.toObject(usuario.class);
                                            listaclientes.add(cliente);
                                        }

                                        adapter.notifyDataSetChanged();

                                        if (listaclientes.isEmpty()) {
                                            Toast.makeText(getContext(), "Aún no tienes usuarios vinculados", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        if (progressBar != null) progressBar.setVisibility(View.GONE);
                                        Toast.makeText(getContext(), "Error al cargar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            if (progressBar != null) progressBar.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Código de vinculación no encontrado", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Error de red: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}