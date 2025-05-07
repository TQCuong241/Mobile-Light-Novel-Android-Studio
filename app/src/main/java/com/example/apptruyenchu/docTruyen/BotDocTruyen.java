package com.example.apptruyenchu.docTruyen;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
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
import com.example.apptruyenchu.toastSetting.CustomToast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class BotDocTruyen extends AppCompatActivity {
    String truyenID, userID, chapterID;
    private TextToSpeech textToSpeech;
    TextView inNoiDungHoc, nameChuong;
    ImageButton btnStopRun, btnTien, btnLui, btnBack;
    private String[] sentences; // Mảng câu
    private int currentSentenceIndex = 0; // Chỉ số câu hiện tại
    ImageView img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_bot_doc_truyen);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        khai_bao();
        btnBack.setOnClickListener(v -> finish());
        DatabaseReference imgUrlRef = FirebaseDatabase.getInstance().getReference("DataTruyen")
                .child(truyenID).child("Img_Url_Truyen");
        imgUrlRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String imgUrl = snapshot.getValue(String.class);
                if(imgUrl != null){
                    Glide.with(BotDocTruyen.this)
                            .load(imgUrl)
                            .into(img);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        btnLui.setOnClickListener(v -> {
            int previousChapterID = Integer.parseInt(chapterID) - 1; // Giảm chỉ số chương
            if (previousChapterID >= 0) { // Kiểm tra chương trước có tồn tại
                chapterID = String.valueOf(previousChapterID);
                fetchChapterData(); // Tải dữ liệu chương mới
            } else {
                CustomToast.showToast(BotDocTruyen.this, "Đã ở chương đầu tiên.");
            }
        });

        btnTien.setOnClickListener(v -> {
            int nextChapterID = Integer.parseInt(chapterID) + 1; // Tăng chỉ số chương
            chapterID = String.valueOf(nextChapterID);
            fetchChapterData(); // Tải dữ liệu chương mới
        });

        btnStopRun.setOnClickListener(v -> {
            if (textToSpeech != null) {
                if (textToSpeech.isSpeaking()) {
                    textToSpeech.stop(); // Dừng phát âm
                    btnStopRun.setImageResource(R.drawable.play);
                } else {
                    if (currentSentenceIndex < sentences.length) {
                        String sentence = sentences[currentSentenceIndex];
                        textToSpeech.speak(sentence, TextToSpeech.QUEUE_FLUSH, null, String.valueOf(currentSentenceIndex));
                        btnStopRun.setImageResource(R.drawable.pause); // Đặt nút thành "pause"
                    }
                }
            }
        });

        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                Locale vietnameseLocale = Locale.forLanguageTag("vi-VN");
                int result = textToSpeech.isLanguageAvailable(vietnameseLocale);

                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    CustomToast.showToast(this, "Dữ liệu giọng nói tiếng Việt chưa được cài đặt. Vui lòng cài đặt.");
                } else {
                    textToSpeech.setLanguage(vietnameseLocale);
                    textToSpeech.setSpeechRate(1.35f);
                    fetchChapterData();
                }
            } else {
                CustomToast.showToast(this, "Lỗi khởi tạo TextToSpeech");
            }
        });
    }

    private void fetchChapterData() {
        DatabaseReference chapterRef = FirebaseDatabase.getInstance().getReference("DataTruyen")
                .child(truyenID)
                .child("DataChuong")
                .child(chapterID);

        chapterRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String tenChuong = snapshot.child("TenChuong").getValue(String.class);
                    String noiDung = snapshot.child("Data_Chuong").getValue(String.class);
                    nameChuong.setText(tenChuong);
                    textToSpeech.speak(tenChuong, TextToSpeech.QUEUE_FLUSH, null, "tenChuong");

                    sentences = noiDung.split("\n");
                    currentSentenceIndex = 0;

                    if (sentences.length > 0) {
                        String firstSentence = sentences[currentSentenceIndex];
                        inNoiDungHoc.setText(firstSentence);
                        textToSpeech.speak(firstSentence, TextToSpeech.QUEUE_FLUSH, null, String.valueOf(currentSentenceIndex));
                    }

                    textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onStart(String utteranceId) {
                        }

                        @Override
                        public void onDone(String utteranceId) {
                            currentSentenceIndex++; // Tăng chỉ số khi hoàn thành
                            if (currentSentenceIndex < sentences.length) {
                                runOnUiThread(() -> {
                                    inNoiDungHoc.setText(sentences[currentSentenceIndex]); // Cập nhật nội dung hiển thị
                                    textToSpeech.speak(sentences[currentSentenceIndex], TextToSpeech.QUEUE_FLUSH, null, String.valueOf(currentSentenceIndex));
                                });
                            }
                            else {
                                int nextChapterID = Integer.parseInt(chapterID) + 1; // Tăng chỉ số chương
                                chapterID = String.valueOf(nextChapterID);
                                fetchChapterData();
                            }
                        }

                        @Override
                        public void onError(String utteranceId) {
                            // Không làm gì
                        }
                    });
                    int chapterIDInt = Integer.parseInt(chapterID);
                    saveToLichSuDoc(userID, truyenID, chapterIDInt);
                    updateViewAll();
                } else {
                    CustomToast.showToast(BotDocTruyen.this, "Nội dung chương không tồn tại.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                CustomToast.showToast(BotDocTruyen.this, "Lỗi khi lấy dữ liệu chương");
            }
        });
    }

    private void khai_bao(){
        truyenID = getIntent().getStringExtra("truyen_id");
        chapterID = getIntent().getStringExtra("chapter_id");
        userID = getIntent().getStringExtra("user_id");
        inNoiDungHoc = findViewById(R.id.inNoiDungHoc);
        nameChuong = findViewById(R.id.nameChuong);
        btnStopRun = findViewById(R.id.btnStopRun);
        img = findViewById(R.id.img);
        btnTien = findViewById(R.id.btnTien);
        btnLui = findViewById(R.id.btnLui);
        btnBack = findViewById(R.id.btnBack);
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
            }
        });
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (textToSpeech != null) {
            textToSpeech.stop(); // Dừng phát âm
            textToSpeech.shutdown(); // Giải phóng tài nguyên
        }
    }

}
