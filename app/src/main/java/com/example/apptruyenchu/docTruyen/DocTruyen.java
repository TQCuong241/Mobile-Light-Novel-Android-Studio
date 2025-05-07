package com.example.apptruyenchu.docTruyen;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.apptruyenchu.R;
import com.example.apptruyenchu.chuong.MainMota;
import com.example.apptruyenchu.danhSachChuong.DanhSachChuong;
import com.example.apptruyenchu.toastSetting.CustomToast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DocTruyen extends AppCompatActivity {
    TextView tenChuongTextView, noiDungTextView;
    ImageButton btnlui, btnnext;

    // Định nghĩa biến chapterID là biến thành viên
    private int chapterID;
    private String truyenID;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_doc_truyen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        khai_bao();

        String chapterIDString = getIntent().getStringExtra("chapter_id");
        if (chapterIDString != null){
            chapterID = Integer.parseInt(chapterIDString);
        }
        else {
            chapterID = Integer.parseInt("1");
        }
        userID = getIntent().getStringExtra("user_id");
        truyenID = getIntent().getStringExtra("truyen_id");
        loadChapter();

        btnnext.setOnClickListener(v -> xuly_next());
        btnlui.setOnClickListener(v -> xuly_lui());

        noiDungTextView.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(DocTruyen.this);
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dia_log, null);
            builder.setView(dialogView);
            ImageButton btnMusic = dialogView.findViewById(R.id.btnMusic);
            ImageButton btnDSChuong = dialogView.findViewById(R.id.btnDSChuong);

            btnDSChuong.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   Intent intent_dschuong = new Intent(DocTruyen.this, DanhSachChuong.class);
                   intent_dschuong.putExtra("user_id", userID);
                   intent_dschuong.putExtra("truyen_id", truyenID);
                   intent_dschuong.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                   startActivity(intent_dschuong);
                   finish();
               }
           });

            btnMusic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseReference lichSuDocRef = FirebaseDatabase.getInstance().getReference("LichSuDoc").child(userID).child(truyenID);

                    lichSuDocRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Intent docTruyenIntent = new Intent(DocTruyen.this, BotDocTruyen.class);

                            if (snapshot.exists()) {
                                String Chuong_id = snapshot.child("Chuong_id").getValue(String.class);
                                if (Chuong_id != null) {
                                    docTruyenIntent.putExtra("chapter_id", Chuong_id);
                                }
                            }
                            else {
                                docTruyenIntent.putExtra("chapter_id", "1");
                            }
                            docTruyenIntent.putExtra("user_id", userID);
                            docTruyenIntent.putExtra("truyen_id", truyenID);
                            startActivity(docTruyenIntent);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            alertDialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);

        });

    }

    private void khai_bao() {
        tenChuongTextView = findViewById(R.id.nameChuong);
        noiDungTextView = findViewById(R.id.NoiDungChuong);
        btnnext = findViewById(R.id.btnnext);
        btnlui = findViewById(R.id.btnlui);
    }

    private void loadChapter() {
        DatabaseReference chapterRef = FirebaseDatabase.getInstance().getReference("DataTruyen").child(truyenID).child("DataChuong").child(String.valueOf(chapterID));
        chapterRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String tenChuong = snapshot.child("TenChuong").getValue(String.class);
                    String noiDung = snapshot.child("Data_Chuong").getValue(String.class);
                    tenChuongTextView.setText(tenChuong);
                    noiDungTextView.setText(formatText(noiDung));

                    saveToLichSuDoc(userID, truyenID, chapterID);
                    updateViewAll();
                } else {
                    CustomToast.showToast(DocTruyen.this, "Đã hết chương mới nhất");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                CustomToast.showToast(DocTruyen.this, "Lỗi khi tải nội dung: " + error.getMessage());
            }
        });
    }

    private void updateViewAll() {
        DatabaseReference viewAllRef = FirebaseDatabase.getInstance().getReference("DataTruyen").child(truyenID);
        viewAllRef.child("ViewAll").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Long viewAll = snapshot.getValue(Long.class);
                if (viewAll != null) {
                    viewAll++;
                    viewAllRef.child("ViewAll").setValue(viewAll);
                } else {
                    viewAllRef.child("ViewAll").setValue(1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                CustomToast.showToast(DocTruyen.this, "Lỗi khi tải ViewAll: " + error.getMessage());
            }
        });
    }

    private void saveToLichSuDoc(String userID, String truyenID, int chapterID) {
        String chapterIDString = String.valueOf(chapterID);
        long currentTimeMillis = System.currentTimeMillis();
        DatabaseReference lichSuDocRef = FirebaseDatabase.getInstance().getReference("LichSuDoc").child(userID);

        lichSuDocRef.child(truyenID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    lichSuDocRef.child(truyenID).child("Chuong_id").setValue(chapterIDString);
                    lichSuDocRef.child(truyenID).child("last_read_time").setValue(currentTimeMillis);
                } else {
                    lichSuDocRef.child(truyenID).child("Chuong_id").setValue(chapterIDString);
                    lichSuDocRef.child(truyenID).child("last_read_time").setValue(currentTimeMillis);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                CustomToast.showToast(DocTruyen.this, "Lỗi khi kiểm tra lịch sử đọc: " + error.getMessage());
            }
        });
    }


    private String formatText(String text) {
        StringBuilder formattedText = new StringBuilder();
        for (String word : text.split(" ")) {
            if (word.contains("-")) {
                formattedText.append(word).append(" ");
            }
            else if (word.contains("...") || word.contains("....") || word.contains(".....") || word.contains("......")) {
                formattedText.append(word).append(" ");
            } else {
                formattedText.append(word).append(" ");
            }
        }
        return formattedText.toString().trim();
    }

    private void xuly_lui() {
        if (chapterID > 1) {
            chapterID--;
            loadChapter();
        } else {
            CustomToast.showToast(DocTruyen.this, "Đã đến chương đầu tiên");
        }
    }

    private void xuly_next() {
        DatabaseReference nextChapterRef = FirebaseDatabase.getInstance().getReference("DataTruyen").child(truyenID).child("DataChuong").child(String.valueOf(chapterID + 1));
        nextChapterRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    chapterID++;
                    loadChapter();
                } else {
                    CustomToast.showToast(DocTruyen.this, "Đã đến chương cuối cùng");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                CustomToast.showToast(DocTruyen.this, "Lỗi khi kiểm tra chương tiếp theo: " + error.getMessage());
            }
        });
    }
}
