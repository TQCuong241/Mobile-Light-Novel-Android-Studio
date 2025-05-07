package com.example.apptruyenchu.love;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.apptruyenchu.R;
import com.example.apptruyenchu.chuong.MainMota;
import com.example.apptruyenchu.toastSetting.CustomToast;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AdapterLove extends ArrayAdapter<Love> {
    private final Context context;
    private final ArrayList<Love> loveList;
    private DatabaseReference mDatabase;

    public AdapterLove(@NonNull Context context, ArrayList<Love> loveList) {
        super(context, 0, loveList);
        this.context = context;
        this.loveList = loveList;
        mDatabase = FirebaseDatabase.getInstance().getReference("LoveTruyen");
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_love, parent, false);
        }

        Love love = loveList.get(position);

        TextView tvTenTruyen = convertView.findViewById(R.id.textTenTruyen);
        TextView tvTacGia = convertView.findViewById(R.id.textTacGia);
        TextView tvView = convertView.findViewById(R.id.textView);
        TextView tvStar = convertView.findViewById(R.id.textStar);
        ImageView imageViewTruyen = convertView.findViewById(R.id.imageViewTruyen);
        ImageButton btnDelete = convertView.findViewById(R.id.btndelete);

        tvTenTruyen.setText(love.getTenTruyen());
        tvTacGia.setText(love.getTacGia());
        tvView.setText(love.getSlView());
        tvStar.setText(love.getSlStar());

        if(love.getImgUrl() != null){
            Glide.with(context)
                    .load(love.getImgUrl())
                    .into(imageViewTruyen);
        }

        convertView.setOnClickListener(v -> {
            Context context = getContext();
            Intent intent = new Intent(context, MainMota.class);
            intent.putExtra("user_id", love.getUser_id());
            intent.putExtra("truyen_id", love.getTruyenId());
            context.startActivity(intent);
        });

        btnDelete.setOnClickListener(v -> {
            String userId = love.getUser_id();
            String truyenId = love.getTruyenId();

            if (truyenId != null) {
                mDatabase.child(userId).child(truyenId).removeValue()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                notifyDataSetChanged();
                                CustomToast.showToast(getContext(), "Đã xóa khỏi danh sách yêu thích");
                            } else {
                            }
                        });
            }
        });

        return convertView;
    }
}
