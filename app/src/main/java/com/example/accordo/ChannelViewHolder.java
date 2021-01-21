package com.example.accordo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.accordo.model.Post;
import com.example.accordo.model.PostTypeImage;
import com.example.accordo.model.PostTypePosition;
import com.example.accordo.model.PostTypeText;

import java.util.Arrays;

public class ChannelViewHolder extends RecyclerView.ViewHolder {

    private static final String LOG_TAG = "ChannelViewHolder";

    private static final int TEXT = 0;
    private static final int IMAGE = 1;
    private static final int POSITION = 2;

    private OnRecyclerViewClickListener onRecyclerViewClickListener;
    private ImageView userPictureView;
    private TextView usernameView;
    private TextView contentView;
    private ImageView imageView;
    private TextView positionView;

    public ChannelViewHolder(@NonNull View itemView,
                             OnRecyclerViewClickListener onRecyclerViewClickListener) {
        super(itemView);
        this.onRecyclerViewClickListener = onRecyclerViewClickListener;
        userPictureView = itemView.findViewById(R.id.userPictureView);
        usernameView = itemView.findViewById(R.id.usernameView);
        contentView = itemView.findViewById(R.id.contentView);
        imageView = itemView.findViewById(R.id.imageView);
        positionView = itemView.findViewById(R.id.positionView);
    }

    public void bind(Post post){
        resetViewHolder();
        usernameView.setText(post.getName());
        setUserPicture(post.getUserPicture());

        if (post instanceof PostTypeImage) {
            setImage(((PostTypeImage) post).getImage());
        }
        else if (post instanceof PostTypePosition) {
            setPosition(((PostTypePosition) post).getLatLon());
        }
        else if (post instanceof PostTypeText) {
            setText(((PostTypeText) post).getText());
        }
    }

    public void resetViewHolder(){
        userPictureView.setImageDrawable(null);
        imageView.setImageDrawable(null);
    }

    public void setUserPicture(Bitmap bitmapPicture){
        if (bitmapPicture != null && bitmapPicture.getWidth() != 0 && bitmapPicture.getHeight() != 0) {
            bitmapPicture = Bitmap.createScaledBitmap(bitmapPicture, 150, 150, false);
            userPictureView.setImageBitmap(bitmapPicture);
        }
    }

    public void setImage(Bitmap bitmapImage){
        show(IMAGE);
        if (bitmapImage != null && bitmapImage.getWidth() != 0 && bitmapImage.getHeight() != 0)
            imageView.setImageBitmap(bitmapImage);
    }

    public void setPosition(double[] latLon){
        show(POSITION);
        positionView.setOnClickListener(view -> {
            onRecyclerViewClickListener.onRecyclerViewClick(view, getAdapterPosition());
        });
    }

    public void setText(String text){
        show(TEXT);
        contentView.setText(text);
    }

    private void show(int postType){
        contentView.setVisibility(View.GONE);
        imageView.setVisibility(View.GONE);
        positionView.setVisibility(View.GONE);
        switch (postType){
            case TEXT:
                contentView.setVisibility(View.VISIBLE);
                break;
            case IMAGE:
                imageView.setVisibility(View.VISIBLE);
                break;
            case POSITION:
                positionView.setVisibility(View.VISIBLE);
                break;
        }
    }
}
