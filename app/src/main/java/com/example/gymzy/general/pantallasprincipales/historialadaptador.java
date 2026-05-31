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

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_historial, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, Object> registro = listaRegistros.get(position);

        // --- SOLUCCIÓN AL PROBLEMA DEL "null" ---

        // 1. Validar Series
        Object seriesObj = registro.get("series");
        String series = (seriesObj != null) ? seriesObj.toString() : "-";

        // 2. Validar Repeticiones (Usa "repeticiones" o "reps" según cómo lo guardes en Firestore)
        // Como en el paso anterior lo guardamos como "repeticiones", lo buscamos prioritariamente así:
        Object repsObj = registro.get("repeticiones");
        if (repsObj == null) {
            repsObj = registro.get("reps"); // Por si acaso quedan registros viejos con la clave corta
        }
        String reps = (repsObj != null) ? repsObj.toString() : "-";

        // 3. Validar Peso
        Object pesoObj = registro.get("peso");
        String peso = (pesoObj != null) ? pesoObj.toString() + " kg" : "- kg";

        // Asignar los valores limpios a las vistas
        holder.tvSeries.setText(series);
        holder.tvReps.setText(reps);
        holder.tvPeso.setText(peso);

        // --- Procesamiento de Fecha (Se mantiene igual, protegido) ---
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

    @Override
    public int getItemCount() {
        return listaRegistros.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFecha, tvSeries, tvReps, tvPeso;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFecha = itemView.findViewById(R.id.tvFechaItem);
            tvSeries = itemView.findViewById(R.id.tvSeriesItem);
            tvReps = itemView.findViewById(R.id.tvRepsItem);
            tvPeso = itemView.findViewById(R.id.tvPesoItem);
        }
    }
}