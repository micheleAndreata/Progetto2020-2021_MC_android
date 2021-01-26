package com.example.accordo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class ImageUtils {

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static String bitmapToBase64(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] byteArray = baos.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public static Bitmap base64ToBitmap(String base64){
        Bitmap bitmap;
        try {
            if (base64 != null){
                byte[] decodedPicture = Base64.decode(base64, Base64.DEFAULT);
                bitmap = BitmapFactory.decodeByteArray(decodedPicture, 0, decodedPicture.length);
            }
            else
                bitmap = null;
        }
        catch (IllegalArgumentException e) {
            bitmap = null;
        }
        return bitmap;
    }

    public static Bitmap reduceImageQuality(Bitmap bm) {
        int maxWidth = 300;
        int maxHeight = 300;
        int quality = 30;
        int width = bm.getWidth();
        int height = bm.getHeight();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
        Bitmap compressed = BitmapFactory.decodeStream(new ByteArrayInputStream(outputStream.toByteArray()));

        if (width > height) {
            float ratio = (float) width / maxWidth;
            width = maxWidth;
            height = (int)(height / ratio);
        } else if (height > width) {
            float ratio = (float) height / maxHeight;
            height = maxHeight;
            width = (int)(width / ratio);
        } else {
            height = maxHeight;
            width = maxWidth;
        }
        return Bitmap.createScaledBitmap(compressed, width, height, true);
    }
}
