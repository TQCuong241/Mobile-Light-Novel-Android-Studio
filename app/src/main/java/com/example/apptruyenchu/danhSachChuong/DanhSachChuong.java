package com.example.apptruyenchu.danhSachChuong;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.apptruyenchu.R;
import com.example.apptruyenchu.docTruyen.DocTruyen;
import com.example.apptruyenchu.toastSetting.CustomToast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DanhSachChuong extends AppCompatActivity {
    private ListView lv;
    private ArrayAdapter<String> adapter;
    private String userId, truyenId;
    private List<DBChuong> danhSachChuong; // Lưu danh sách đối tượng DBChuong

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_danh_sach_chuong2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        khai_bao();
        danhSachChuong = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                new ArrayList<>()); // Khởi tạo adapter tạm thời
        lv.setAdapter(adapter);

        if (truyenId != null) {
            ds_chuong(truyenId);
        } else {
            CustomToast.showToast(this, "Chưa có truyện nào, vui lòng quay lại");
        }

        lv.setOnItemClickListener((parent, view, position, id) -> {
            DBChuong selectedChuong = danhSachChuong.get(position); // Lấy DBChuong tại vị trí được chọn
            String chuongId = selectedChuong.Chuong_id;
            Intent intent = new Intent(DanhSachChuong.this, DocTruyen.class);
            intent.putExtra("user_id", userId);
            intent.putExtra("truyen_id", truyenId);
            intent.putExtra("chapter_id", chuongId);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
            finish();
        });
    }

    private void khai_bao() {
        lv = findViewById(R.id.lv);
        userId = getIntent().getStringExtra("user_id");
        truyenId = getIntent().getStringExtra("truyen_id");
    }

    private void ds_chuong(String truyenId) {
        DatabaseReference dsChuong = FirebaseDatabase.getInstance().getReference("DataTruyen").child(truyenId).child("DataChuong");
        dsChuong.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                danhSachChuong.clear();
                List<String> tenChuongList = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    DBChuong chuong = ds.getValue(DBChuong.class);
                    if (chuong != null) {
                        danhSachChuong.add(chuong);
                        tenChuongList.add(chuong.TenChuong); // Thêm tên chương để hiển thị trong ListView
                    }
                }
                if (danhSachChuong.isEmpty()) {
                    CustomToast.showToast(DanhSachChuong.this, "Chưa có chương nào");
                }
                adapter.clear();
                adapter.addAll(tenChuongList); // Cập nhật adapter với danh sách tên chương
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi nếu cần
            }
        });
    }
}
