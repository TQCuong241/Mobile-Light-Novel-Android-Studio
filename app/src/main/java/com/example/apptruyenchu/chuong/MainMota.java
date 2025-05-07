package com.example.apptruyenchu.chuong;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.apptruyenchu.R;
import com.example.apptruyenchu.comment.Comment;
import com.example.apptruyenchu.danhSachChuong.DanhSachChuong;
import com.example.apptruyenchu.docTruyen.DocTruyen;
import com.example.apptruyenchu.toastSetting.CustomToast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class MainMota extends AppCompatActivity {
    private DatabaseReference DBTruyen;
    private TextView textTenTruyen, textView, textMota, textTacGia, textNgayDang;
    private ImageView imgTruyenView, btnLove, dschuong;
    private Button buttonDocTruyen;
    private ImageButton btnBack, btncomment;
    private String userId;
    private String truyenId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_mota);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        userId = intent.getStringExtra("user_id");
        truyenId = intent.getStringExtra("truyen_id");
        checkIfLoved();
        DBTruyen = FirebaseDatabase.getInstance().getReference("DataTruyen").child(truyenId);
        DBTruyen.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                show_data(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        khaiBao();

        btnBack.setOnClickListener(v ->{
            finish();
        });

        btncomment.setOnClickListener(v ->{
            Intent commentIntent = new Intent(MainMota.this, Comment.class);
            commentIntent.putExtra("user_id", userId);
            commentIntent.putExtra("truyen_id", truyenId);
            startActivity(commentIntent);
        });

        buttonDocTruyen.setOnClickListener(v -> {
            doc_truyen();
        });

        btnLove.setOnClickListener(v -> {
            xuly_love();
        });

        dschuong.setOnClickListener(v -> {
            Intent intent_dschuong = new Intent(MainMota.this, DanhSachChuong.class);
            intent_dschuong.putExtra("user_id", userId);
            intent_dschuong.putExtra("truyen_id", truyenId);
            startActivity(intent_dschuong);
        });

    }

    private void khaiBao() {
        textTenTruyen = findViewById(R.id.textTenTruyen);
        imgTruyenView = findViewById(R.id.imgTruyenView);
        textView = findViewById(R.id.textView);
        textMota = findViewById(R.id.textMota);
        textTacGia = findViewById(R.id.textTacGia);
        textNgayDang = findViewById(R.id.textNgayDang);
        buttonDocTruyen = findViewById(R.id.btnDocTruyen);
        btnBack = findViewById(R.id.btnBack);
        btncomment = findViewById(R.id.btncomment);
        btnLove = findViewById(R.id.btnLove);
        dschuong = findViewById(R.id.dschuong);
    }

    private void checkIfLoved() {
        DatabaseReference loveRef = FirebaseDatabase.getInstance().getReference("LoveTruyen").child(userId).child(truyenId);
        loveRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    btnLove.setImageResource(R.drawable.love);
                } else {
                    btnLove.setImageResource(R.drawable.notlove);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void show_data(DataSnapshot snapshot){
        String tenTruyen = snapshot.child("TenTruyen").getValue(String.class);
        String imgUrl = snapshot.child("Img_Url_Truyen").getValue(String.class);
        String tacGia = snapshot.child("TacGiaID").getValue(String.class);
        String moTa = snapshot.child("Mota").getValue(String.class);
        Long luotXem = snapshot.child("ViewAll").getValue(Long.class);
        String ngayDang = snapshot.child("Update_Truyen").getValue(String.class);

        textView.setText(String.valueOf(luotXem));
        textMota.setText(moTa);
        textTacGia.setText(tacGia);
        textNgayDang.setText("Ngày đăng truyện: " + ngayDang);
        textTenTruyen.setText(tenTruyen);

        if (imgUrl != null) {
            Glide.with(MainMota.this)
                    .load(imgUrl)
                    .into(imgTruyenView);
        }
        else {
            imgTruyenView.setImageResource(R.drawable.noimg);
        }
    }

    private void doc_truyen(){
        DatabaseReference lichSuDocRef = FirebaseDatabase.getInstance().getReference("LichSuDoc").child(userId).child(truyenId);

        lichSuDocRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Intent docTruyenIntent = new Intent(MainMota.this, DocTruyen.class);

                if (snapshot.exists()) {
                    String Chuong_id = snapshot.child("Chuong_id").getValue(String.class);
                    if (Chuong_id != null) {
                        docTruyenIntent.putExtra("chapter_id", Chuong_id);
                    }
                }
                else {
                    docTruyenIntent.putExtra("chapter_id", "1");
                }
                docTruyenIntent.putExtra("user_id", userId);
                docTruyenIntent.putExtra("truyen_id", truyenId);
                startActivity(docTruyenIntent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void xuly_love(){
        DatabaseReference loveRef = FirebaseDatabase.getInstance().getReference("LoveTruyen").child(userId);

        loveRef.child(truyenId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    loveRef.child(truyenId).removeValue().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            btnLove.setImageResource(R.drawable.notlove);
                            CustomToast.showToast(MainMota.this, "Đã xóa khỏi danh sách yêu thích");
                        }
                    });
                } else {
                    loveRef.child(truyenId).child("last_read_time").setValue(System.currentTimeMillis()).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            btnLove.setImageResource(R.drawable.love);
                            CustomToast.showToast(MainMota.this, "Đã thêm vào danh sách yêu thích");
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}
