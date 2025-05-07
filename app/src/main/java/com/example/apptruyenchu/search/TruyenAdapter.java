package com.example.apptruyenchu.search;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.apptruyenchu.R;

import java.util.List;


public class TruyenAdapter extends RecyclerView.Adapter<TruyenAdapter.TruyenViewHolder> {

    private List<Truyen> truyenList;
    private OnItemClickListener listener;  // Thêm listener vào adapter

    public TruyenAdapter(List<Truyen> truyenList, OnItemClickListener listener) {
        this.truyenList = truyenList;
        this.listener = listener;  // Gán listener
    }

    @NonNull
    @Override
    public TruyenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_truyen, parent, false);
        return new TruyenViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TruyenViewHolder holder, int position) {
        Truyen truyen = truyenList.get(position);

        holder.tenTruyen.setText(truyen.getTenTruyen());
        holder.tenTacGia.setText("#  " + truyen.getTacGia());
        holder.tbSoSao.setText(String.valueOf(truyen.getTbSoSao()));
        holder.viewAll.setText(String.valueOf(truyen.getViewAll()));

        Glide.with(holder.itemView.getContext())
                .load(truyen.getImgUrlTruyen())
                .placeholder(R.drawable.img_bg)
                .error(R.drawable.img_bg)
                .into(holder.imgTruyen);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(truyen);
            }
        });
    }

    @Override
    public int getItemCount() {
        return truyenList.size();
    }

    public static class TruyenViewHolder extends RecyclerView.ViewHolder {
        TextView tenTruyen, tenTacGia, tbSoSao, viewAll;
        ImageView imgTruyen;

        public TruyenViewHolder(@NonNull View itemView) {
            super(itemView);
            tenTruyen = itemView.findViewById(R.id.textTenTruyen);
            tenTacGia = itemView.findViewById(R.id.textTacGia);
            imgTruyen = itemView.findViewById(R.id.imageViewTruyen);
            tbSoSao = itemView.findViewById(R.id.textStar);
            viewAll = itemView.findViewById(R.id.textView);
        }
    }
}
