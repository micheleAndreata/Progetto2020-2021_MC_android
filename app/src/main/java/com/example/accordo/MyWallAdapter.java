package com.example.accordo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.accordo.model.Model;

public class MyWallAdapter extends RecyclerView.Adapter<WallViewHolder> {

    private LayoutInflater mInflater;
    private Model model;
    private OnRecyclerViewClickListener onRecyclerViewClickListener;

    public MyWallAdapter(Context context, Model model, OnRecyclerViewClickListener onRecyclerViewClickListener){
        this.mInflater = LayoutInflater.from(context);
        this.model = model;
        this.onRecyclerViewClickListener = onRecyclerViewClickListener;
    }

    @NonNull
    @Override
    public WallViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_wall, parent, false);
        return new WallViewHolder(view, onRecyclerViewClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull WallViewHolder holder, int position) {
        String cTitle = model.getMyWall().get(position);
        holder.bind(cTitle);
    }

    @Override
    public int getItemCount() {
        return model.getMyWall().size();
    }
}
