package com.example.gymzy.general.Api.openfoodfast;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gymzy.R;
import java.util.List;

public class IngredienteAdapter extends RecyclerView.Adapter<IngredienteAdapter.ViewHolder> {
    private List<String> nombres;
    private List<Double> calorias;

    public IngredienteAdapter(List<String> nombres, List<Double> calorias) {
        this.nombres = nombres;
        this.calorias = calorias;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // CORRECCIÓN: Inflamos el layout del item individual
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ingrediente, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvNom.setText(nombres.get(position));
        holder.tvCal.setText(String.format("%.1f kcal", calorias.get(position)));
    }

    @Override
    public int getItemCount() {
        return nombres.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNom, tvCal;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNom = itemView.findViewById(R.id.tvNombreItem);
            tvCal = itemView.findViewById(R.id.tvCalItem);
        }
    }
}