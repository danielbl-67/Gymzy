package com.example.gymzy.general.usuarios;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymzy.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class usuariosAdminAdapter extends RecyclerView.Adapter<usuariosAdminAdapter.UsuarioViewHolder> {

    private List<usuario> listaUsuarios;

    // Interface para notificar al Fragment cuando se elimina un elemento de la lista
    public interface OnUsuarioEliminadoListener {
        void onEliminado();
    }

    private OnUsuarioEliminadoListener eliminadoListener;

    public usuariosAdminAdapter(List<usuario> listaUsuarios, OnUsuarioEliminadoListener eliminadoListener) {
        this.listaUsuarios = listaUsuarios;
        this.eliminadoListener = eliminadoListener;
    }

    @NonNull
    @Override
    public UsuarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_usuario_admin, parent, false);
        return new UsuarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsuarioViewHolder holder, int position) {
        usuario usuario = listaUsuarios.get(position);

        holder.tvNombre.setText(usuario.nombre);

        if (usuario.rol != null) {
            holder.tvRol.setText(usuario.rol.toUpperCase());
        } else {
            holder.tvRol.setText("USUARIO");
        }

        holder.tvEmail.setText(usuario.email);
        holder.tvEdad.setText("Edad: " + usuario.edad);
        holder.tvPeso.setText("Peso: " + usuario.peso + " kg");
        holder.tvAltura.setText("Alt: " + usuario.altura + " cm");

        if (usuario.codigoVinculacion != null && !usuario.codigoVinculacion.isEmpty()) {
            holder.tvCodigo.setVisibility(View.VISIBLE);
            holder.tvCodigo.setText("Código: " + usuario.codigoVinculacion);
        } else {
            holder.tvCodigo.setVisibility(View.GONE);
        }

        // ⚡ ACCIÓN: Clic largo para eliminar el usuario seleccionado
        holder.itemView.setOnLongClickListener(v -> {
            mostrarDialogoEliminar(v.getContext(), usuario, position);
            return true;
        });
    }

    private void mostrarDialogoEliminar(android.content.Context context, usuario usuario, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Eliminar Cuenta")
                .setMessage("¿Estás seguro de que deseas borrar a " + usuario.nombre + "? Esta acción no se puede deshacer de Firestore.")
                .setPositiveButton("Eliminar", (dialog, which) -> {

                    // Buscamos el documento en Firestore por el email para obtener su UID de forma indirecta,
                    // o lo ideal es buscarlo si tuviéramos el UID guardado en el objeto.
                    FirebaseFirestore.getInstance().collection("Usuarios")
                            .whereEqualTo("email", usuario.email)
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                if (!queryDocumentSnapshots.isEmpty()) {
                                    String idDocumento = queryDocumentSnapshots.getDocuments().get(0).getId();

                                    // Borramos de Firestore
                                    FirebaseFirestore.getInstance().collection("Usuarios").document(idDocumento)
                                            .delete()
                                            .addOnSuccessListener(aVoid -> {
                                                Toast.makeText(context, "Usuario eliminado de la base de datos", Toast.LENGTH_SHORT).show();
                                                listaUsuarios.remove(position);
                                                notifyItemRemoved(position);
                                                if (eliminadoListener != null) eliminadoListener.onEliminado();
                                            });
                                }
                            });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public int getItemCount() {
        return listaUsuarios.size();
    }

    public static class UsuarioViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvRol, tvEmail, tvEdad, tvPeso, tvAltura, tvCodigo;

        public UsuarioViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvItemNombre);
            tvRol = itemView.findViewById(R.id.tvItemRol);
            tvEmail = itemView.findViewById(R.id.tvItemEmail);
            tvEdad = itemView.findViewById(R.id.tvItemEdad);
            tvPeso = itemView.findViewById(R.id.tvItemPeso);
            tvAltura = itemView.findViewById(R.id.tvItemAltura);
            tvCodigo = itemView.findViewById(R.id.tvItemCodigo);
        }
    }
}