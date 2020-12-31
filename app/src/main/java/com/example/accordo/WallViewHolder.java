package com.example.accordo;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class WallViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView cTitleView;
    private OnRecyclerViewClickListener onRecyclerViewClickListener;

    public WallViewHolder(@NonNull View itemView, OnRecyclerViewClickListener onRecyclerViewClickListener) {
        super(itemView);
        itemView.setOnClickListener(this);
        this.onRecyclerViewClickListener = onRecyclerViewClickListener;
        cTitleView = itemView.findViewById(R.id.cTitle);
    }

    public void bind(String cTitle){
        cTitleView.setText(cTitle);
    }

    @Override
    public void onClick(View view) {
        onRecyclerViewClickListener.onRecyclerViewClick(view, getAdapterPosition());
    }
}
