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

        public HistorialViewHolder(View itemView) {
            super(itemView);
            monedesFinalsTextView = itemView.findViewById(R.id.monedesFinalsTextView);
            dataTextView = itemView.findViewById(R.id.dataTextView);
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
        holder.monedesFinalsTextView.setText("Monedes finals: " + item.getMonedesFinals());
        holder.dataTextView.setText("Data: " + item.getData());
    }

    @Override
    public int getItemCount() {
        return historialList.size();
    }
}
