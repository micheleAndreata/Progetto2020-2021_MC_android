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

    private ImageView userPictureView;
    private TextView usernameView;
    private TextView contentView;
    private ImageView imageView;

    public ChannelViewHolder(@NonNull View itemView) {
        super(itemView);
        userPictureView = itemView.findViewById(R.id.userPictureView);
        usernameView = itemView.findViewById(R.id.usernameView);
        contentView = itemView.findViewById(R.id.contentView);
        imageView = itemView.findViewById(R.id.imageView);
    }

    public void bind(Post post){

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

    public void setUserPicture(String base64Picture){
        try {
            if (base64Picture != null){
                byte[] decodedPicture = Base64.decode(base64Picture, Base64.DEFAULT);
                Bitmap bitmapPicture = BitmapFactory.decodeByteArray(decodedPicture, 0, decodedPicture.length);
                if (bitmapPicture != null && bitmapPicture.getWidth() != 0 && bitmapPicture.getHeight() != 0) {
                    bitmapPicture = Bitmap.createScaledBitmap(bitmapPicture, 150, 150, false);
                    userPictureView.setImageBitmap(bitmapPicture);
                }
                else
                    Log.d(LOG_TAG, "setUserPicture: bitmapPicture is null or one or more dimensions is 0");

            }
            else
                Log.d(LOG_TAG, "setUserPicture: base64Picture is null");
        }
        catch (IllegalArgumentException e) {
            Log.d(LOG_TAG, "setUserPicture: image is not correctly encoded in Base64");
        }
    }

    public void setImage(String base64Image){
        imageView.setVisibility(View.VISIBLE);
        contentView.setVisibility(View.GONE);
        try {
            if (base64Image != null){
                byte[] decodedImage = Base64.decode(base64Image, Base64.DEFAULT);
                Bitmap bitmapImage = BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.length);
                if (bitmapImage != null && bitmapImage.getWidth() != 0 && bitmapImage.getHeight() != 0)
                    imageView.setImageBitmap(bitmapImage);
                else
                    Log.d(LOG_TAG, "setImage: bitmapImage is null or one or more dimensions is 0");
            }
            else
                Log.d(LOG_TAG, "setImage: base64Image is null");
        }
        catch (IllegalArgumentException e) {
            Log.d(LOG_TAG, "setImage: image is not correctly encoded in Base64");
        }
    }

    public void setPosition(double[] latLon){
        imageView.setVisibility(View.GONE);
        contentView.setVisibility(View.VISIBLE);
        contentView.setText(Arrays.toString(latLon));
    }

    public void setText(String text){
        imageView.setVisibility(View.GONE);
        contentView.setVisibility(View.VISIBLE);
        contentView.setText(text);
    }
}
