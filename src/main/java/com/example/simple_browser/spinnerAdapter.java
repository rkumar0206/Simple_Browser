package com.example.simple_browser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class spinnerAdapter extends ArrayAdapter {

    public spinnerAdapter(Context context, ArrayList<spinner_Item> spinnerlist) {
        super(context, 0, spinnerlist);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.custom_spinner1, parent, false
            );
        }

        ImageView imageViewFlag = convertView.findViewById(R.id.spinner_items);


        spinner_Item currentItem = (spinner_Item) getItem(position);

        if (currentItem != null) {
            imageViewFlag.setImageResource(currentItem.geticonImage());

        }
        return convertView;
    }
}
