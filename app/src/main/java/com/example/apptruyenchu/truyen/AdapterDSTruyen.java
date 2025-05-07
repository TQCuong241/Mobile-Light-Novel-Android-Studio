package com.example.apptruyenchu.truyen;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide; // Thêm Glide vào đây
import com.example.apptruyenchu.R;

import java.util.ArrayList;

public class AdapterDSTruyen extends ArrayAdapter<Truyen> {
    private final Context context;
    private final ArrayList<Truyen> truyenList;

    public AdapterDSTruyen(Context context, ArrayList<Truyen> truyenList) {
        super(context, R.layout.item_truyen, truyenList);
        this.context = context;
        this.truyenList = truyenList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_truyen, parent, false);
        }

        ImageView imageViewTruyen = convertView.findViewById(R.id.imageViewTruyen);
        TextView textViewTenTruyen = convertView.findViewById(R.id.textTenTruyen);
        TextView textView = convertView.findViewById(R.id.textView);
        TextView textStar = convertView.findViewById(R.id.textStar);
        TextView textTacGia = convertView.findViewById(R.id.textTacGia);

        Truyen truyen = truyenList.get(position);
        textView.setText(truyen.getViewAll());
        textTacGia.setText("#  " + truyen.getTacGia());
        textViewTenTruyen.setText(truyen.getTenTruyen());
        textStar.setText(truyen.getStar());

        String imgUrl = truyen.getImgUrl();
        if (imgUrl != null) {
            Glide.with(context)
                    .load(imgUrl)
                    .into(imageViewTruyen);
        }

        return convertView;
    }
}
