package com.example.apptruyenchu.upTruyen;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.apptruyenchu.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TruyenDaDang extends AppCompatActivity {
    private ListView listView;
    private TruyenAdapter truyenAdapter;
    private List<Truyen> truyenList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_truyen_da_dang);

        // Thiết lập padding cho hệ thống
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String userId = getIntent().getStringExtra("user_id");

        // Khởi tạo ListView
        listView = findViewById(R.id.listView);
        truyenList = new ArrayList<>();
        truyenAdapter = new TruyenAdapter(this, truyenList);
        listView.setAdapter(truyenAdapter);
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
        Button btnAdd = findViewById(R.id.addButton);
        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(TruyenDaDang.this, UpTruyen.class);
            intent.putExtra("user_id", userId);
            startActivity(intent);
        });
        // Lấy truyện từ Firebase
        if (userId != null) {
            fetchStoriesByUser(userId);
        } else {
            Log.e("TruyenDaDang", "userId is null");
        }

        // Xử lý sự kiện click vào item trong ListView
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Truyen selectedTruyen = truyenList.get(position);
            Toast.makeText(this, "Bạn đã chọn: " + selectedTruyen.TenTruyen, Toast.LENGTH_SHORT).show();
            // Bạn có thể bắt đầu một Activity mới để hiển thị chi tiết truyện
        });
    }

    private void fetchStoriesByUser(String userId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("DataTruyen");

        databaseReference.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                truyenList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Truyen truyen = snapshot.getValue(Truyen.class);
                    if (truyen != null) {
                        truyen.idTruyen = snapshot.getKey();
                        truyenList.add(truyen);
                    }
                }
                truyenAdapter.notifyDataSetChanged(); // Cập nhật adapter
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("TruyenDaDang", "Database error: " + databaseError.getMessage());
            }
        });
    }
}
