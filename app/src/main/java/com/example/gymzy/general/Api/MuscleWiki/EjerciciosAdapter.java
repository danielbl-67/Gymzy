package com.example.gymzy.general.Api.MuscleWiki;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.gymzy.R;
import com.example.gymzy.general.PantallasPrincipales.DetalleEjercicioActivity;

import java.util.List;
public class EjerciciosAdapter extends RecyclerView.Adapter<EjerciciosAdapter.ViewHolder> {

    private List<EjercicioMuscle> lista;
    private Context context;

    public EjerciciosAdapter(List<EjercicioMuscle> lista, Context context) {
        this.lista = lista;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_exercise, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EjercicioMuscle ej = lista.get(position);
        holder.tvNombre.setText(ej.getName());

        Glide.with(context)
                .load(ej.getVideoUrl())
                .placeholder(R.drawable.ic_logoredondo)
                .into(holder.ivImagen);

        // Al hacer click, enviamos los datos al detalle
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetalleEjercicioActivity.class);
            intent.putExtra("nombre", ej.getName());
            intent.putExtra("imagen", ej.getVideoUrl());
            intent.putExtra("descripcion", ej.getStepsFormatted());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() { return lista.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre;
        ImageView ivImagen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreEjercicio);
            ivImagen = itemView.findViewById(R.id.ivEjercicioGif);
        }
    }
}