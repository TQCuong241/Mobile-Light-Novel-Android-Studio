package com.example.apptruyenchu.comment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Comment extends AppCompatActivity {

    private ListView listViewComments;
    private EditText editTextComment;
    private Button buttonSend;
    private ImageButton btnback;
    private ArrayList<CommentItem> comments;
    private CommentAdapter adapter;
    private DatabaseReference commentsRef;
    private String truyenId;
    private String userId;
    private TextView tvName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_comment);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        truyenId = getIntent().getStringExtra("truyen_id");
        userId = getIntent().getStringExtra("user_id");

        listViewComments = findViewById(R.id.listViewComments);
        editTextComment = findViewById(R.id.editTextComment);
        buttonSend = findViewById(R.id.buttonSend);
        tvName = findViewById(R.id.tvName);
        btnback = findViewById(R.id.buttonback);

        comments = new ArrayList<>();
        adapter = new CommentAdapter(this, comments);
        listViewComments.setAdapter(adapter);

        commentsRef = FirebaseDatabase.getInstance().getReference("Comment").child(truyenId);
        loadCommentsFromFirebase();
        loadTruyenName();
        btnback.setOnClickListener(v -> finish());
        buttonSend.setOnClickListener(v -> sendComment());
    }

    private void loadTruyenName() {
        DatabaseReference tenTruyenRef = FirebaseDatabase.getInstance().getReference("DataTruyen").child(truyenId).child("TenTruyen");
        tenTruyenRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String tenTruyen = snapshot.getValue(String.class);
                tvName.setText(tenTruyen != null ? tenTruyen : "Tên truyện không có");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Comment.this, "Lỗi tải tên truyện", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendComment() {
        String commentText = editTextComment.getText().toString().trim();
        if (!commentText.isEmpty()) {
            getUserInfoById(userId, (userName, imgUser) -> {
                commentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        long commentCount = dataSnapshot.getChildrenCount();
                        String commentId = String.valueOf(commentCount + 1);

                        String updateDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
                        CommentItem newComment = new CommentItem(commentText, updateDate, userName, userId, imgUser, userId);

                        commentsRef.child(commentId).setValue(newComment).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                listViewComments.setSelection(adapter.getCount() - 1);
                                editTextComment.setText("");
                            } else {
                                Toast.makeText(Comment.this, "Lỗi gửi bình luận", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(Comment.this, "Lỗi tải bình luận: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            });
        } else {
            Toast.makeText(Comment.this, "Vui lòng nhập bình luận!", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadCommentsFromFirebase() {
        commentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                comments.clear();
                for (DataSnapshot commentSnapshot : dataSnapshot.getChildren()) {
                    String ndComment = commentSnapshot.child("nd_Comment").getValue(String.class);
                    String updateComment = commentSnapshot.child("update_Comment").getValue(String.class);
                    String userName = commentSnapshot.child("userName").getValue(String.class);
                    String commentUserId  = commentSnapshot.child("userId").getValue(String.class);
                    String imgUser = commentSnapshot.child("imgUser").getValue(String.class);

                    if (ndComment != null && updateComment != null) {
                        comments.add(new CommentItem(ndComment, updateComment, userName != null ? userName : "Ẩn danh", commentUserId, imgUser != null ? imgUser : "", userId));
                    }
                }
                adapter.notifyDataSetChanged();
                listViewComments.setSelection(adapter.getCount() - 1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Comment.this, "Lỗi tải bình luận: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getUserInfoById(String userId, UserInfoCallback callback) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userName = dataSnapshot.child("UserName").getValue(String.class);
                String imgUser = dataSnapshot.child("ImgUser").getValue(String.class);
                callback.onCallback(userName != null ? userName : "Ẩn danh", imgUser != null ? imgUser : "");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Comment", "Lỗi lấy thông tin người dùng: " + databaseError.getMessage());
                callback.onCallback("Ẩn danh", "");
            }
        });
    }

    interface UserInfoCallback {
        void onCallback(String userName, String imgUser);
    }
}
