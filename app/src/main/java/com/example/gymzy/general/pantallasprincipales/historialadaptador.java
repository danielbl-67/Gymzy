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

public class historialadaptador extends RecyclerView.Adapter<historialadaptador.ViewHolder> {

    private List<Map<String, Object>> listaRegistros;

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

        // Obtener datos del mapa de Firestore de forma segura
        String series = String.valueOf(registro.get("series"));
        String reps = String.valueOf(registro.get("reps"));
        String peso = String.valueOf(registro.get("peso")) + " kg";

        holder.tvSeries.setText(series);
        holder.tvReps.setText(reps);
        holder.tvPeso.setText(peso);

        // Conversión segura de fechaMillis (soporta tanto Número como String)
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