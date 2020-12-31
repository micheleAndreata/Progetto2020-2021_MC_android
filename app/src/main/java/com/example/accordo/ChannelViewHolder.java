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

import java.util.Arrays;

public class ChannelViewHolder extends RecyclerView.ViewHolder {

    private ImageView userPicture;
    private TextView username;
    private TextView content;
    private ImageView imageView;

    public ChannelViewHolder(@NonNull View itemView) {
        super(itemView);
        userPicture = itemView.findViewById(R.id.userPictureView);
        username = itemView.findViewById(R.id.username);
        content = itemView.findViewById(R.id.content);
        imageView = itemView.findViewById(R.id.imageView);
    }

    public void bind(Post post){
        username.setText(post.getName() + " " + post.getUid());

        try {
            String base64Picture = post.getUserPicture();
            if (base64Picture != null){
                byte[] decodedPicture = Base64.decode(base64Picture, Base64.DEFAULT);
                Bitmap bitmapPicture = BitmapFactory.decodeByteArray(decodedPicture, 0, decodedPicture.length);
                Log.d("ViewHolder", "" + ((float)bitmapPicture.getWidth()/bitmapPicture.getHeight()));
                if ((float)bitmapPicture.getWidth()/bitmapPicture.getHeight() == 1)
                    userPicture.setImageBitmap(bitmapPicture);
            }
        }
        catch (IllegalArgumentException e) {Log.d("ViewHolder", "bind: image is not correctly encoded in Base64");}

        if (post instanceof PostTypeImage) {
            imageView.setVisibility(View.VISIBLE);
            content.setVisibility(View.GONE);
            try {
                String base64Image = ((PostTypeImage) post).getImage();
                if (base64Image != null){
                    byte[] decodedImage = Base64.decode(base64Image, Base64.DEFAULT);
                    Bitmap bitmapImage = BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.length);
                    imageView.setImageBitmap(bitmapImage);
                }
            }
            catch (IllegalArgumentException e) { Log.d("ViewHolder", "bind: image is not correctly encoded in Base64");}
        }
        else if (post instanceof PostTypePosition) {
            imageView.setVisibility(View.GONE);
            content.setVisibility(View.VISIBLE);
            content.setText(Arrays.toString(((PostTypePosition) post).getLatLon()));
        }
        else if (post instanceof PostTypeText) {
            imageView.setVisibility(View.GONE);
            content.setVisibility(View.VISIBLE);
            content.setText(((PostTypeText) post).getText());
        }
    }
}
