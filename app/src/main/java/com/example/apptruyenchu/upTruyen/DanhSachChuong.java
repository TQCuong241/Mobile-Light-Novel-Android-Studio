package com.example.apptruyenchu.upTruyen;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.apptruyenchu.R;
import com.example.apptruyenchu.toastSetting.CustomToast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DanhSachChuong extends AppCompatActivity {
    private ListView listView;
    private List<String> chuongTitles; // Danh sách tiêu đề chương
    private ArrayAdapter<String> adapter;
    private String userId, truyenId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_danh_sach_chuong);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        userId = getIntent().getStringExtra("user_id");
        truyenId = getIntent().getStringExtra("truyen_id");
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
        // Khởi tạo ListView
        listView = findViewById(R.id.listView);
        chuongTitles = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, chuongTitles);
        listView.setAdapter(adapter);

        if (truyenId != null) {
            fetchChapters(truyenId);
        } else {
            CustomToast.showToast(this, "Chưa có truyện nào");
        }

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedChuongTitle = chuongTitles.get(position);
            String selectedChuongId = String.valueOf(position + 1); // Giả sử ID chương tương ứng với vị trí trong danh sách

            // Hiển thị AlertDialog để chỉnh sửa chương
            showEditChapterDialog(selectedChuongId, selectedChuongTitle);
        });

        Button addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(v -> showAddChapterDialog()); // Gọi hàm hiện dialog
    }

    private void showAddChapterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_chapter, null);
        builder.setView(dialogView);

        EditText inputTenChuong = dialogView.findViewById(R.id.input_ten_chuong);
        EditText inputNoiDungChuong = dialogView.findViewById(R.id.input_noi_dung_chuong);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String tenChuong = inputTenChuong.getText().toString().trim();
            String noiDungChuong = inputNoiDungChuong.getText().toString().trim();

            if (!tenChuong.isEmpty() && !noiDungChuong.isEmpty()) {
                saveChuongToFirebase(tenChuong, noiDungChuong);
            } else {
                Toast.makeText(DanhSachChuong.this, "Vui lòng nhập tên và nội dung chương", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());

        // Tạo và hiển thị AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void fetchChapters(String truyenId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("DataTruyen").child(truyenId).child("DataChuong");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chuongTitles.clear(); // Xóa danh sách hiện tại

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chuong chuong = snapshot.getValue(Chuong.class);
                    if (chuong != null) {
                        chuongTitles.add(chuong.TenChuong); // Thêm tiêu đề chương vào danh sách
                    }
                }

                // Kiểm tra xem có chương nào không
                if (chuongTitles.isEmpty()) {
                    chuongTitles.add("Chưa có chương nào"); // Thêm thông báo
                }

                adapter.notifyDataSetChanged(); // Cập nhật adapter
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("DanhSachChuong", "Database error: " + databaseError.getMessage());
                Toast.makeText(DanhSachChuong.this, "Lỗi: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveChuongToFirebase(String tenChuong, String noiDungChuong) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("DataTruyen").child(truyenId).child("DataChuong");
        DatabaseReference truyenReference = FirebaseDatabase.getInstance().getReference("DataTruyen").child(truyenId);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int currentCount = (int) dataSnapshot.getChildrenCount();
                String chuongId = String.valueOf(currentCount + 1);

                Chuong newChuong = new Chuong(chuongId, noiDungChuong, tenChuong);

                // Lưu chương mới vào Firebase với ID tương ứng
                databaseReference.child(chuongId).setValue(newChuong)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(DanhSachChuong.this, "Thêm chương thành công!", Toast.LENGTH_SHORT).show();

                            long currentTimeMillis = System.currentTimeMillis();
                            truyenReference.child("Update_Truyen_Now").setValue(currentTimeMillis);
                            fetchChapters(truyenId);
                        })
                        .addOnFailureListener(e -> Toast.makeText(DanhSachChuong.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DanhSachChuong.this, "Lỗi: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEditChapterDialog(String chuongId, String existingTitle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_chapter, null);
        builder.setView(dialogView);

        EditText inputTenChuong = dialogView.findViewById(R.id.input_ten_chuong);
        EditText inputNoiDungChuong = dialogView.findViewById(R.id.input_noi_dung_chuong);

        inputTenChuong.setText(existingTitle);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String newTenChuong = inputTenChuong.getText().toString().trim();
            String newNoiDungChuong = inputNoiDungChuong.getText().toString().trim();

            if (!newTenChuong.isEmpty() && !newNoiDungChuong.isEmpty()) {
                updateChuongInFirebase(chuongId, newTenChuong, newNoiDungChuong);
            } else {
                Toast.makeText(DanhSachChuong.this, "Vui lòng nhập tên và nội dung chương", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateChuongInFirebase(String chuongId, String newTenChuong, String newNoiDungChuong) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("DataTruyen").child(truyenId).child("DataChuong").child(chuongId);

        databaseReference.child("TenChuong").setValue(newTenChuong);
        databaseReference.child("Data_Chuong").setValue(newNoiDungChuong)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(DanhSachChuong.this, "Cập nhật chương thành công!", Toast.LENGTH_SHORT).show();
                    fetchChapters(truyenId);
                })
                .addOnFailureListener(e -> Toast.makeText(DanhSachChuong.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
