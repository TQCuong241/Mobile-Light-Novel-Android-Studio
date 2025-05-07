package com.example.apptruyenchu.account;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;import com.example.apptruyenchu.MainActivity;
import com.example.apptruyenchu.R;
import com.example.apptruyenchu.toastSetting.CustomToast;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.bumptech.glide.Glide;

public class Login extends AppCompatActivity {
    Button loginButton;
    EditText emailEditText, passwordEditText;
    TextView registerTextView, thongBaoLogin;
    DatabaseReference usersRef;
    ImageView imgView;
    boolean isPasswordVisible = false;
    ImageButton btnback, btnViewPass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        khai_bao();

        loginButton.setOnClickListener(view -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            if (email.isEmpty() && password.isEmpty()) {
                thongBaoLogin.setText("Vui lòng nhập email và mật khẩu");
            }
            else if (email.isEmpty()) {
                thongBaoLogin.setText("Vui lòng nhập email");
            }
            else if (password.isEmpty()) {
                thongBaoLogin.setText("Vui lòng nhập mật khẩu");
            }
            else {
                login_User(email, password);
            }
        });

        registerTextView.setOnClickListener(view -> {
            Intent intent = new Intent(Login.this, CreateAccount.class);
            startActivity(intent);
        });

        Glide.with(this)
                .load("https://media3.giphy.com/media/v1.Y2lkPTc5MGI3NjExZ2pqY2RtcnN4dnkxczF6Z3BuczB2dm9xa2kwNzQ1Ym8wNnRwa2h2ZCZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9cw/tV25tpdKqdFa9x81k2/giphy.webp")
                .into(imgView);

        btnback.setOnClickListener(v -> finish());

        btnViewPass.setOnClickListener(v -> {
            if (isPasswordVisible) {
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                btnViewPass.setImageResource(R.drawable.show);
            } else {
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                btnViewPass.setImageResource(R.drawable.hide);
            }
            passwordEditText.setSelection(passwordEditText.getText().length());
            isPasswordVisible = !isPasswordVisible;
        });
    }

    private void khai_bao(){
        loginButton = findViewById(R.id.loginButton);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        registerTextView = findViewById(R.id.registerTextView);
        thongBaoLogin = findViewById(R.id.thongBaoLogin);
        usersRef = FirebaseDatabase.getInstance().getReference("Users");
        imgView = findViewById(R.id.imgView);
        btnback = findViewById(R.id.btnback);
        btnViewPass = findViewById(R.id.btnViewPass);
    }

    private void login_User(String email, String password) {
        usersRef.orderByChild("Email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        String storedPassword = userSnapshot.child("Password").getValue(String.class);
                        String userId = userSnapshot.getKey();

                        if (storedPassword != null && storedPassword.equals(password)) {
                            CustomToast.showToast(Login.this, "Đăng nhập thành công");

                            getSharedPreferences("LoginPrefs", MODE_PRIVATE)
                                    .edit()
                                    .putString("user_id", userId)
                                    .putBoolean("isLoggedIn", true)
                                    .apply();

                            Intent intent = new Intent(Login.this, MainActivity.class);
                            intent.putExtra("user_id", userId);
                            startActivity(intent);
                            finish();
                            return;
                        } else {
                            thongBaoLogin.setText("Mật khẩu không đúng");
                        }
                    }
                } else {
                    thongBaoLogin.setText("Email không tồn tại");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("LoginError", "Lỗi không lấy được data", error.toException());
            }
        });
    }
}