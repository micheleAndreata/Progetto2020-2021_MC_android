package com.example.accordo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.accordo.model.Model;
import com.example.accordo.model.Post;

public class ChannelAdapter extends RecyclerView.Adapter<ChannelViewHolder> {

    private LayoutInflater mInflater;
    private Model model;
    private OnRecyclerViewClickListener onRecyclerViewClickListener;

    public ChannelAdapter(Context context, Model model, OnRecyclerViewClickListener onRecyclerViewClickListener){
        this.mInflater = LayoutInflater.from(context);
        this.model = model;
        this.onRecyclerViewClickListener = onRecyclerViewClickListener;
    }

    @NonNull
    @Override
    public ChannelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_channel, parent, false);
        return new ChannelViewHolder(view, onRecyclerViewClickListener, parent.getContext());
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
