package com.example.gymzy.general.api.musclewiki;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.gymzy.R;
import com.example.gymzy.general.api.traductor.traductormlkit;
import com.example.gymzy.general.pantallasprincipales.detalleejercicioactivity;
import java.util.List;

/**
 * Adaptador para el RecyclerView encargado de listar las tarjetas de ejercicios de una categoria.
 * Administra la carga asincrona de miniaturas multimedia mediante Glide, solicita traducciones al vuelo
 * para los titulos y gestiona los eventos de navegacion hacia la vista de detalle.
 */
public class ejerciciosadapter extends RecyclerView.Adapter<ejerciciosadapter.ViewHolder> {

    private List<ejerciciomuscle> lista;
    private Context context;

    /**
     * Constructor del adaptador que recibe la lista de ejercicios cargados desde la API.
     *
     * @param lista   Lista de objetos {@link ejerciciomuscle} a renderizar.
     * @param context Contexto de la actividad que contiene el RecyclerView.
     */
    public ejerciciosadapter(List<ejerciciomuscle> lista, Context context) {
        this.lista = lista;
        this.context = context;
    }

    /**
     * Infla el diseno XML para la tarjeta del ejercicio y genera una nueva instancia del ViewHolder.
     *
     * @param parent   Contenedor principal donde se acoplara la vista del elemento.
     * @param viewType Indicador del tipo de vista de la celda.
     * @return Nueva instancia de la clase interna ViewHolder vinculada al layout correspondiente.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_exercise, parent, false);
        return new ViewHolder(v);
    }

    /**
     * Enlaza las propiedades del modelo con los componentes visuales de la celda. Inyecta el titulo
     * original de forma temporal e invoca un hilo secundario de traduccion automatica junto con la carga
     * de la miniatura de previsualizacion.
     *
     * @param holder   Contenedor de las referencias a las vistas de la tarjeta.
     * @param position Indice de posicion del elemento dentro de la lista de datos.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ejerciciomuscle ej = lista.get(position);

        // Ponemos el nombre en inglés mientras la traducción llega
        holder.tvNombre.setText(ej.getName());

        // Traducimos el nombre
        traductormlkit.traducir(ej.getName(), textoTraducido -> {
            holder.tvNombre.setText(textoTraducido);
        });

        Glide.with(context)
                .load(ej.getVideoUrl())
                .placeholder(R.drawable.ic_logoredondo)
                .into(holder.ivImagen);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, detalleejercicioactivity.class);
            // Pasamos los originales, la actividad de detalle se encargará de traducir al abrirse
            intent.putExtra("nombre", ej.getName());
            intent.putExtra("imagen", ej.getVideoUrl());
            intent.putExtra("descripcion", ej.getStepsFormatted());
            context.startActivity(intent);
        });
    }

    /**
     * Devuelve la cantidad total de ejercicios disponibles en el catalogo filtrado de la coleccion.
     *
     * @return Numero total de elementos en la lista, o 0 si la referencia es nula.
     */
    @Override
    public int getItemCount() { return lista != null ? lista.size() : 0; }

    /**
     * Clase estatica contenedora encargada de mantener e identificar las referencias visuales
     * de los elementos internos de la celda de ejercicio (item_exercise).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre;
        ImageView ivImagen;

        /**
         * Constructor del ViewHolder que enlaza las vistas del XML del elemento individual.
         *
         * @param itemView Vista raiz de la tarjeta inflada.
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreEjercicio);
            ivImagen = itemView.findViewById(R.id.ivEjercicioGif);
        }
    }
}