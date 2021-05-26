package com.example.accordo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.accordo.model.Model;

public class MyWallAdapter extends RecyclerView.Adapter<WallViewHolder> { //serve per gestire i viewHolder e li ricicla perchè estende RecyclerView

    private LayoutInflater mInflater;
    private Model model;
    private OnRecyclerViewClickListener onRecyclerViewClickListener;

    public MyWallAdapter(Context context, Model model, OnRecyclerViewClickListener onRecyclerViewClickListener){
        this.mInflater = LayoutInflater.from(context);
        this.model = model;
        this.onRecyclerViewClickListener = onRecyclerViewClickListener;
    }

    // gestisce la creazione del viewHolder (l'elemento della lista)
    @NonNull
    @Override
    public WallViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // definiamo una view
        View view = mInflater.inflate(R.layout.recyclerview_wall, parent, false);
        return new WallViewHolder(view, onRecyclerViewClickListener); 
    }

    @Override // gestisce l'inserimento dei dati nel viewHolder
    public void onBindViewHolder(@NonNull WallViewHolder holder, int position) {
        String cTitle = model.getMyWall().get(position);
        holder.bind(cTitle);
    }

    @Override
    public int getItemCount() { //ritorna la dimensione della lista, è una funzione che SERVE ALL'ADAPTER
        return model.getMyWall().size();
    }
}
