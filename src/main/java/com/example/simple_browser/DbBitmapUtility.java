package com.example.simple_browser;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

public class DbBitmapUtility {

    //convert from bitmap to byteArray
    public byte[] getBytes(Bitmap bitmap) {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    //convert from byteArray to bitmap

    public Bitmap getImage(byte[] image) {

        return BitmapFactory.decodeByteArray(image, 0, image.length);

    }


}
