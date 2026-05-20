package com.example.gymzy.general.pantallasprincipales;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gymzy.R;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Adaptador para el RecyclerView encargado de parsear y mostrar las marcas pasadas de un ejercicio.
 * Transforma mapas de datos crudos de Cloud Firestore en tarjetas con series, repeticiones, peso y fechas.
 */
public class historialadaptador extends RecyclerView.Adapter<historialadaptador.ViewHolder> {

    private List<Map<String, Object>> listaRegistros;

    /**
     * Constructor del adaptador que recibe los datos de las marcas historicas.
     *
     * @param listaRegistros Lista de mapas que contienen los atributos guardados de cada sesion.
     */
    public historialadaptador(List<Map<String, Object>> listaRegistros) {
        this.listaRegistros = listaRegistros;
    }

    /**
     * Infla el diseño XML de la fila del historial y genera una nueva instancia del ViewHolder.
     *
     * @param parent   Contenedor padre donde se alojara la vista del item.
     * @param viewType Indicador del tipo de vista.
     * @return Nueva instancia de la clase interna ViewHolder.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_historial, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Vincula los datos del mapa con los TextViews del ViewHolder e implementa una conversion
     * segura de milisegundos a texto legible de fecha y hora.
     *
     * @param holder   Contenedor de las referencias a los elementos visuales de la fila.
     * @param position Indice numerico del elemento dentro de la coleccion de datos.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, Object> registro = listaRegistros.get(position);

        String series = String.valueOf(registro.get("series"));
        String reps = String.valueOf(registro.get("reps"));
        String peso = String.valueOf(registro.get("peso")) + " kg";

        holder.tvSeries.setText(series);
        holder.tvReps.setText(reps);
        holder.tvPeso.setText(peso);

        if (registro.containsKey("fechaMillis") && registro.get("fechaMillis") != null) {
            try {
                long millis;
                Object fechaObj = registro.get("fechaMillis");

                if (fechaObj instanceof Number) {
                    millis = ((Number) fechaObj).longValue();
                } else {
                    millis = Long.parseLong(fechaObj.toString());
                }

                SimpleDateFormat sdf = new SimpleDateFormat("dd 'de' MMMM, yyyy - HH:mm", Locale.getDefault());
                holder.tvFecha.setText(sdf.format(new Date(millis)));
            } catch (Exception e) {
                holder.tvFecha.setText("Fecha no válida");
            }
        } else {
            holder.tvFecha.setText("Sin fecha");
        }
    }

    /**
     * Devuelve la cantidad total de marcas indexadas en la coleccion de historial.
     *
     * @return Numero total de elementos.
     */
    @Override
    public int getItemCount() {
        return listaRegistros.size();
    }

    /**
     * Clase estatica contenedora que aloja e identifica las referencias visuales del diseno del item.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFecha, tvSeries, tvReps, tvPeso;

        /**
         * Constructor que vincula los TextViews del archivo de diseno XML individual.
         *
         * @param itemView Vista raiz de la fila (item_historial).
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFecha = itemView.findViewById(R.id.tvFechaItem);
            tvSeries = itemView.findViewById(R.id.tvSeriesItem);
            tvReps = itemView.findViewById(R.id.tvRepsItem);
            tvPeso = itemView.findViewById(R.id.tvPesoItem);
        }
    }
}