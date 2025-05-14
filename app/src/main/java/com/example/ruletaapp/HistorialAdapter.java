package com.example.ruletaapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistorialAdapter extends RecyclerView.Adapter<HistorialAdapter.HistorialViewHolder> {

    private final List<Puntuacio> llistaPuntuacions;

    public HistorialAdapter(List<Puntuacio> llistaPuntuacions) {
        this.llistaPuntuacions = llistaPuntuacions;
    }

    @NonNull
    @Override
    public HistorialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_historial, parent, false);
        return new HistorialViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull HistorialViewHolder holder, int position) {
        Puntuacio puntuacio = llistaPuntuacions.get(position);
        holder.txtEmail.setText(puntuacio.getEmail());
        holder.txtMonedes.setText("Monedes: " + puntuacio.getMonedes());

        // Formata la data
        Date data = new Date(puntuacio.getTimestamp());
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        holder.txtData.setText(format.format(data));
    }

    @Override
    public int getItemCount() {
        return llistaPuntuacions.size();
    }

    public static class HistorialViewHolder extends RecyclerView.ViewHolder {
        TextView txtEmail, txtMonedes, txtData;

        public HistorialViewHolder(@NonNull View itemView) {
            super(itemView);
            txtEmail = itemView.findViewById(R.id.txtEmail);
            txtMonedes = itemView.findViewById(R.id.txtMonedes);
            txtData = itemView.findViewById(R.id.txtData);
        }
    }
}
