package com.example.gymzy.general.api.openfoodfast;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gymzy.R;
import java.util.List;

/**
 * Adaptador para el RecyclerView encargado de listar los ingredientes agregados a una receta.
 * Vincula de forma paralela los nombres de los alimentos y sus respectivas calorias calculadas.
 */
public class ingredienteadapter extends RecyclerView.Adapter<ingredienteadapter.ViewHolder> {
    private List<String> nombres;
    private List<Double> calorias;

    /**
     * Constructor del adaptador que recibe las colecciones de datos de la receta.
     *
     * @param nombres  Lista con los nombres y pesajes de los ingredientes.
     * @param calorias Lista con los aportes caloricos proporcionales de cada ingrediente.
     */
    public ingredienteadapter(List<String> nombres, List<Double> calorias) {
        this.nombres = nombres;
        this.calorias = calorias;
    }

    /**
     * Infla el diseño XML para la celda del ingrediente y genera una nueva instancia del ViewHolder.
     *
     * @param parent   Contenedor principal donde se insertara la vista del elemento.
     * @param viewType Indicador del tipo de vista de la celda.
     * @return Nueva instancia de la clase interna ViewHolder.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ingrediente, parent, false);
        return new ViewHolder(v);
    }

    /**
     * Acopla los valores de texto y formatea las kilocalorias de un ingrediente especifico
     * en base a su posicion indexada en las listas de datos.
     *
     * @param holder   Contenedor que aloja las referencias a los elementos visuales de la fila.
     * @param position Indice numerico del elemento dentro de la coleccion de datos.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvNom.setText(nombres.get(position));
        holder.tvCal.setText(String.format("%.1f kcal", calorias.get(position)));
    }

    /**
     * Devuelve la cantidad total de ingredientes agregados a la calculadora en la sesion actual.
     *
     * @return Cantidad de elementos en la lista.
     */
    @Override
    public int getItemCount() {
        return nombres.size();
    }

    /**
     * Clase estatica contenedora que identifica y guarda las referencias de los TextViews
     * del archivo de diseño de la fila individual (item_ingrediente).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNom, tvCal;

        /**
         * Constructor del ViewHolder que enlaza las vistas del XML del elemento individual.
         *
         * @param itemView Vista raiz de la fila inflada.
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNom = itemView.findViewById(R.id.tvNombreItem);
            tvCal = itemView.findViewById(R.id.tvCalItem);
        }
    }
}