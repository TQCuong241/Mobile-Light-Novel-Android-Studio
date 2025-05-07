package com.example.apptruyenchu.upTruyen;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide; // Sử dụng Glide để tải hình ảnh
import com.example.apptruyenchu.R;
import com.example.apptruyenchu.toastSetting.CustomToast;

import java.util.List;

public class TruyenAdapter extends ArrayAdapter<Truyen> {
    private Context context;
    private List<Truyen> truyenList;

    public TruyenAdapter(Context context, List<Truyen> truyenList) {
        super(context, 0, truyenList);
        this.context = context;
        this.truyenList = truyenList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_truyen, parent, false);
        }

        Truyen truyen = truyenList.get(position);

        ImageView imgTruyen = convertView.findViewById(R.id.imageViewTruyen);
        TextView tenTruyen = convertView.findViewById(R.id.textTenTruyen);

        tenTruyen.setText(truyen.TenTruyen);

        Glide.with(context)
                .load(truyen.Img_Url_Truyen)
                .into(imgTruyen);

        convertView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DanhSachChuong.class);
            intent.putExtra("truyen_id", truyen.idTruyen);
            intent.putExtra("user_id", truyen.userId);
            context.startActivity(intent);
        });

        return convertView;
    }
}
