package com.example.accordo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ChannelAdapter extends RecyclerView.Adapter<ChannelViewHolder> {

    private LayoutInflater mInflater;
    private Model model;

    public ChannelAdapter(Context context, Model model){
        this.mInflater = LayoutInflater.from(context);
        this.model = model;
    }

    @NonNull
    @Override
    public ChannelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_channel, parent, false);
        return new ChannelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChannelViewHolder holder, int position) {
        Post post = model.getPost(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return model.getChannelSize();
    }
}
