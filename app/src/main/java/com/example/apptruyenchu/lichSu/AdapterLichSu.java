package com.example.apptruyenchu.lichSu;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.apptruyenchu.R;
import com.example.apptruyenchu.chuong.MainMota;
import com.example.apptruyenchu.toastSetting.CustomToast;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AdapterLichSu extends ArrayAdapter<LS> {
    private DatabaseReference mDatabase;

    public AdapterLichSu(Context context, ArrayList<LS> lichSuList) {
        super(context, 0, lichSuList);
        mDatabase = FirebaseDatabase.getInstance().getReference("LichSuDoc");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LS lichSu = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_lich_su, parent, false);
        }

        TextView tvTenTruyen = convertView.findViewById(R.id.textTenTruyen);
        TextView tvTacGia = convertView.findViewById(R.id.textTacGia);
        TextView tvView = convertView.findViewById(R.id.textView);
        TextView tvStar = convertView.findViewById(R.id.textStar);
        TextView tvDangdoc = convertView.findViewById(R.id.textDangdoc);
        ImageView imageViewTruyen = convertView.findViewById(R.id.imageViewTruyen);
        ImageButton btnDelete = convertView.findViewById(R.id.btndelete);

        tvTenTruyen.setText(lichSu.getTenTruyen());
        tvTacGia.setText("#  " + lichSu.getTacGia());
        tvView.setText(lichSu.getSlView());
        tvStar.setText(lichSu.getSlStar());
        tvDangdoc.setText("Đang đọc: Chương " + lichSu.getChuongId());

        if (lichSu.getImgUrl() != null) {
            Glide.with(getContext())
                    .load(lichSu.getImgUrl())
                    .into(imageViewTruyen);
        } else {
            imageViewTruyen.setImageResource(R.drawable.noimg);
        }

        convertView.setOnClickListener(v -> {
            Context context = getContext();
            Intent intent = new Intent(context, MainMota.class);
            intent.putExtra("user_id", lichSu.getUserID());
            intent.putExtra("truyen_id", lichSu.getTruyenID());
            context.startActivity(intent);
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = lichSu.getUserID();
                String truyenId = lichSu.getTruyenID();

                if (truyenId != null) {
                    mDatabase.child(userId).child(truyenId).removeValue()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    CustomToast.showToast(getContext(), "Xóa truyện khỏi lịch sử thành công");
                                    remove(lichSu);
                                } else {
                                    CustomToast.showToast(getContext(), "Xóa truyện khỏi lịch sử thất bại");
                                }
                            });
                }
            }
        });

        return convertView;
    }

}
