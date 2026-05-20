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

public class clientesadapter extends RecyclerView.Adapter<clientesadapter.clienteviewholder> {

    private List<usuario> listaclientes;

    public clientesadapter(List<usuario> listaclientes) {
        this.listaclientes = listaclientes;
    }

    @NonNull
    @Override
    public clienteviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_usuario_pro, parent, false);
        return new clienteviewholder(view);
    }

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

    @Override
    public int getItemCount() {
        return listaclientes.size();
    }

    public static class clienteviewholder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvRol, tvEmail, tvEdad, tvPeso, tvAltura, tvCodigo;

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