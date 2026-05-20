package com.example.gymzy.general.roles;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gymzy.R;
import com.example.gymzy.general.usuarios.usuario;
import java.util.List;

/**
 * Adaptador para el RecyclerView encargado de desplegar la lista de clientes vinculados
 * dentro de la interfaz o panel de un usuario profesional (Entrenador o Nutricionista).
 */
public class clientesadapter extends RecyclerView.Adapter<clientesadapter.clienteviewholder> {

    private List<usuario> listaclientes;

    /**
     * Constructor del adaptador que recibe la fuente de datos.
     *
     * @param listaclientes Lista de objetos {@link usuario} que representan los alumnos o pacientes.
     */
    public clientesadapter(List<usuario> listaclientes) {
        this.listaclientes = listaclientes;
    }

    /**
     * Infla el diseño XML correspondiente a la tarjeta o fila de cada cliente y crea el ViewHolder.
     *
     * @param parent   El contenedor principal sobre el cual se añadira la vista.
     * @param viewType El tipo de vista (no se utiliza en este adaptador simple).
     * @return Una nueva instancia de {@link clienteviewholder} con la vista inflada.
     */
    @NonNull
    @Override
    public clienteviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_usuario_pro, parent, false);
        return new clienteviewholder(view);
    }

    /**
     * Acopla los atributos de un cliente especifico en la posicion de la lista con las vistas
     * del ViewHolder, controlando ademas la visibilidad del codigo de vinculacion.
     *
     * @param holder   El objeto ViewHolder que contiene las referencias de los elementos visuales.
     * @param position La posicion indexada del elemento dentro de la lista de datos.
     */
    @Override
    public void onBindViewHolder(@NonNull clienteviewholder holder, int position) {
        usuario cliente = listaclientes.get(position);

        holder.tvNombre.setText(cliente.nombre);
        holder.tvEmail.setText(cliente.email);
        holder.tvEdad.setText("Edad: " + cliente.edad);
        holder.tvPeso.setText("Peso: " + cliente.peso + " kg");
        holder.tvAltura.setText("Alt: " + cliente.altura + " cm");

        if (cliente.rol != null) {
            holder.tvRol.setText(cliente.rol.toUpperCase());
        } else {
            holder.tvRol.setText("USUARIO");
        }

        if (cliente.codigoVinculacion != null && !cliente.codigoVinculacion.isEmpty()) {
            holder.tvCodigo.setVisibility(View.VISIBLE);
            holder.tvCodigo.setText("Código: " + cliente.codigoVinculacion);
        } else {
            holder.tvCodigo.setVisibility(View.GONE);
        }
    }

    /**
     * Devuelve el total de elementos presentes en la coleccion de clientes.
     *
     * @return Cantidad de elementos en la lista.
     */
    @Override
    public int getItemCount() {
        return listaclientes.size();
    }

    /**
     * Clase estatica que almacena y mapea las referencias a los componentes visuales de la fila
     * para evitar llamadas costosas a findViewById durante el scroll.
     */
    public static class clienteviewholder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvRol, tvEmail, tvEdad, tvPeso, tvAltura, tvCodigo;

        /**
         * Constructor del ViewHolder que enlaza las vistas del XML del elemento individual.
         *
         * @param itemView La vista raiz de la celda inflada (item_usuario_pro).
         */
        public clienteviewholder(@NonNull View itemView) {
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