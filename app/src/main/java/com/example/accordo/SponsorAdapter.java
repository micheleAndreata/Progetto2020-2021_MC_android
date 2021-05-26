package com.example.accordo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.accordo.model.Model;

import org.json.JSONObject;

public class SponsorAdapter extends RecyclerView.Adapter<SponsorViewHolder>{

    private LayoutInflater inflater;
    private Model model;
    private OnRecyclerViewClickListener onRecyclerViewClickListener;

    public SponsorAdapter(Context context, Model model, OnRecyclerViewClickListener onRecyclerViewClickListener){
        this.inflater = LayoutInflater.from(context);
        this.model = model;
        this.onRecyclerViewClickListener = onRecyclerViewClickListener;
    }

    @NonNull
    @Override
    public SponsorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recyclerview_sponsor, parent, false);
        return new SponsorViewHolder(view, onRecyclerViewClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull SponsorViewHolder holder, int position) {
        JSONObject sponsor = model.getSponsor().get(position);
        holder.bind(sponsor);
    }

    @Override
    public int getItemCount() {
        return model.getSponsor().size();
    }
}
