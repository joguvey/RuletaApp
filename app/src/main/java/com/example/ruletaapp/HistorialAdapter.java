package com.example.ruletaapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HistorialAdapter extends RecyclerView.Adapter<HistorialAdapter.HistorialViewHolder> {

    private List<HistorialItem> historialList;

    public HistorialAdapter(List<HistorialItem> historialList) {
        this.historialList = historialList;
    }

    public static class HistorialViewHolder extends RecyclerView.ViewHolder {
        TextView monedesFinalsTextView;
        TextView dataTextView;
        TextView textUbicacio;

        public HistorialViewHolder(View itemView) {

            super(itemView);
            monedesFinalsTextView = itemView.findViewById(R.id.monedesFinalsTextView);
            dataTextView = itemView.findViewById(R.id.dataTextView);
            textUbicacio = itemView.findViewById(R.id.textUbicacio);

        }
    }

    @NonNull
    @Override
    public HistorialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_historial, parent, false);
        return new HistorialViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistorialViewHolder holder, int position) {
        HistorialItem item = historialList.get(position);
        holder.monedesFinalsTextView.setText("Monedes finals: " + item.getMonedes());
        holder.dataTextView.setText("Data: " + item.getData());
        double latitud = item.getLatitud();
        double longitud = item.getLongitud();
        holder.textUbicacio.setText("Ubicació: " + latitud + ", " + longitud);
        holder.textUbicacio.setText("Ubicació: " + latitud + ", " + longitud + "\n" + item.getAdreca());

    }

    @Override
    public int getItemCount() {
        return historialList.size();
    }
}
