package com.example.ruletaapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TopPuntuacionsAdapter extends RecyclerView.Adapter<TopPuntuacionsAdapter.ViewHolder> {

    private List<Puntuacio> llista;

    public TopPuntuacionsAdapter(List<Puntuacio> llista) {
        this.llista = llista;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_historial, parent, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Puntuacio p = llista.get(position);
        holder.txtEmail.setText(p.getEmail());
        holder.txtMonedes.setText("Monedes: " + p.getMonedes());
        holder.txtData.setText("Timestamp: " + p.getTimestamp()); // o formatat si vols
    }

    @Override
    public int getItemCount() {
        return llista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtEmail, txtMonedes, txtData;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtEmail = itemView.findViewById(R.id.txtEmail);
            txtMonedes = itemView.findViewById(R.id.txtMonedes);
            txtData = itemView.findViewById(R.id.txtData);
        }
    }
}
