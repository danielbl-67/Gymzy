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

/**
 * Adaptador para el RecyclerView que gestiona la lista de usuarios desde la vista de administrador.
 * Permite visualizar los datos de cada usuario y eliminarlos de Firebase Firestore mediante un clic largo.
 */
public class usuariosAdminAdapter extends RecyclerView.Adapter<usuariosAdminAdapter.UsuarioViewHolder> {

    private List<usuario> listaUsuarios;

    /**
     * Interfaz para notificar al Fragment o Activity contenedor cuando se elimina
     * un elemento de la lista en la base de datos.
     */
    public interface OnUsuarioEliminadoListener {
        /**
         * Evento disparado inmediatamente después de que el usuario ha sido borrado con éxito.
         */
        void onEliminado();
    }

    private OnUsuarioEliminadoListener eliminadoListener;

    /**
     * Constructor del adaptador.
     *
     * @param listaUsuarios     Lista de objetos {@link usuario} que se van a mostrar.
     * @param eliminadoListener Callback para escuchar los eventos de eliminación.
     */
    public usuariosAdminAdapter(List<usuario> listaUsuarios, OnUsuarioEliminadoListener eliminadoListener) {
        this.listaUsuarios = listaUsuarios;
        this.eliminadoListener = eliminadoListener;
    }

    /**
     * Infla el diseño XML para cada fila del RecyclerView y crea el ViewHolder.
     *
     * @param parent   El ViewGroup en el que se añadirá la nueva vista después de vincularse a una posición.
     * @param viewType El tipo de vista de la nueva vista (no se utiliza en este adaptador simple).
     * @return Una nueva instancia de {@link UsuarioViewHolder} que contiene la vista de la fila.
     */
    @NonNull
    @Override
    public UsuarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_usuario_admin, parent, false);
        return new UsuarioViewHolder(view);
    }

    /**
     * Vincula los datos del usuario en la posición especificada con los componentes visuales del ViewHolder.
     * Configura el texto, los roles y el listener de clic largo para eliminar el registro.
     *
     * @param holder   El ViewHolder que debe actualizarse para representar el contenido del elemento.
     * @param position La posición del elemento dentro de la lista de datos del adaptador.
     */
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

    /**
     * Muestra un cuadro de diálogo de confirmación de tipo AlertDialog para eliminar al usuario.
     * Si el administrador confirma, se busca el documento en Firestore usando el correo electrónico,
     * se borra de la base de datos y se actualiza la lista local de la aplicación.
     *
     * @param context  El contexto de la aplicación o actividad necesario para mostrar el diálogo.
     * @param usuario  El objeto {@link usuario} que se pretende eliminar.
     * @param position La posición indexada del usuario dentro de la lista para gestionar su remoción.
     */
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

    /**
     * Devuelve el tamaño total de la lista de usuarios que maneja el adaptador.
     *
     * @return El número de elementos contenidos en la lista de usuarios.
     */
    @Override
    public int getItemCount() {
        return listaUsuarios.size();
    }

    /**
     * Clase contenedora que mantiene las referencias de las vistas de los componentes
     * de la interfaz de usuario para cada elemento de la lista.
     */
    public static class UsuarioViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvRol, tvEmail, tvEdad, tvPeso, tvAltura, tvCodigo;

        /**
         * Constructor del ViewHolder que inicializa y enlaza los componentes visuales del archivo XML.
         *
         * @param itemView La vista raíz del elemento de la lista (item_usuario_admin).
         */
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