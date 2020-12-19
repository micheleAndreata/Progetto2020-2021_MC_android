package com.example.accordo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


class WallViewHolder extends RecyclerView.ViewHolder {
    private final TextView itemView;

    private WallViewHolder(View itemView) {
        super(itemView);
        this.itemView = itemView.findViewById(R.id.textView);
    }

    public void bind(String channelName) {
        itemView.setText(channelName);
    }

    static WallViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_wall, parent, false);
        return new WallViewHolder(view);
    }
}