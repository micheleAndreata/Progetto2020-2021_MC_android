package com.example.accordo;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.accordo.model.PostTypeImage;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SponsorViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView sponsorName;
    private ImageView sponsorImage;
    private OnRecyclerViewClickListener onRecyclerViewClickListener;

    public SponsorViewHolder(@NonNull View itemView, OnRecyclerViewClickListener onRecyclerViewClickListener) {
        super(itemView);
        itemView.setOnClickListener(this);
        this.onRecyclerViewClickListener = onRecyclerViewClickListener;
        sponsorName = itemView.findViewById(R.id.name_sponsor);
        sponsorImage = itemView.findViewById(R.id.image_sponsor);
    }

    public void bind(JSONObject sponsor){
        try {
            sponsorName.setText(sponsor.getString("text"));
            Log.d("SponsorViewHolder", sponsor.getString("image"));
            setImage(ImageUtils.base64ToBitmap(sponsor.getString("image")));
            //TODO: implementarlo per l'immagine
        } catch (JSONException e) { e.printStackTrace(); }
    }

    public void setImage(Bitmap bitmapImage){
        if (bitmapImage != null && bitmapImage.getWidth() != 0 && bitmapImage.getHeight() != 0) {
            sponsorImage.setImageBitmap(bitmapImage);
        }
    }

    @Override
    public void onClick(View view) {
        onRecyclerViewClickListener.onRecyclerViewClick(view, getAdapterPosition());
    }
}
