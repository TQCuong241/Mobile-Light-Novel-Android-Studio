package com.example.apptruyenchu.account;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.apptruyenchu.R;
import com.example.apptruyenchu.toastSetting.CustomToast;
import com.example.apptruyenchu.toolMail.Send;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import android.view.inputmethod.InputMethodManager;

public class CreateAccount extends AppCompatActivity {
    EditText editTextEmail, editTextPassword, editTextUserName, editTextVerificationCode, editTextConfirmPassword;
    Button buttonRegister, buttonVerify;
    TextView textViewMessage, thongBaoLogin;
    FirebaseAuth auth;
    String verificationCode;
    InputMethodManager imm;
    ImageButton btnback, btnViewPass1, btnViewPass;
    boolean isPasswordVisible = false;
    boolean isConfirmPasswordVisible = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_account);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        khai_bao();

        buttonRegister.setOnClickListener(v -> {
            if (kiem_tra_input()) {
                create_account();
                View view = getCurrentFocus();
                if(view != null){
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });

        btnback.setOnClickListener(v -> finish());
        buttonVerify.setOnClickListener(v -> verifyCode());
        btnViewPass.setOnClickListener(v -> {
            if (isPasswordVisible) {
                editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                btnViewPass.setImageResource(R.drawable.show);
            } else {
                editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                btnViewPass.setImageResource(R.drawable.hide);
            }
            editTextPassword.setSelection(editTextPassword.getText().length());
            isPasswordVisible = !isPasswordVisible;
        });

        btnViewPass1.setOnClickListener(v -> {
            if (isConfirmPasswordVisible) {
                editTextConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                btnViewPass1.setImageResource(R.drawable.show);
            } else {
                editTextConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                btnViewPass1.setImageResource(R.drawable.hide);
            }
            editTextConfirmPassword.setSelection(editTextConfirmPassword.getText().length());
            isConfirmPasswordVisible = !isConfirmPasswordVisible;
        });

        editTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Pair<String, String> passwordStrength = checkPasswordStrength(s.toString());
                thongBaoLogin.setText(passwordStrength.first);
                thongBaoLogin.setTextColor(Color.parseColor(passwordStrength.second));
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void khai_bao() {
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextUserName = findViewById(R.id.editTextUserName);
        editTextVerificationCode = findViewById(R.id.editTextVerificationCode);
        buttonRegister = findViewById(R.id.buttonRegister);
        buttonVerify = findViewById(R.id.buttonVerify);
        textViewMessage = findViewById(R.id.textViewMessage);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        auth = FirebaseAuth.getInstance();
        btnback = findViewById(R.id.btnback);
        btnViewPass1 = findViewById(R.id.btnViewPass1);
        btnViewPass = findViewById(R.id.btnViewPass);
        thongBaoLogin = findViewById(R.id.thongBaoLogin);
    }

    private Pair<String, String> checkPasswordStrength(String password) {
        if (password.length() < 6) {
            return new Pair<>("Yếu", "#FF0000");
        }

        boolean hasUpperCase = !password.equals(password.toLowerCase());
        boolean hasLowerCase = !password.equals(password.toUpperCase());
        boolean hasDigits = password.matches(".*\\d.*");
        boolean hasSpecialChar = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]+.*");

        int strength = 0;
        if (hasUpperCase) strength++;
        if (hasLowerCase) strength++;
        if (hasDigits) strength++;
        if (hasSpecialChar) strength++;

        switch (strength) {
            case 1:
            case 2:
                return new Pair<>("Yếu", "#FF0000");
            case 3:
                return new Pair<>("Trung Bình", "#FFFF00");
            case 4:
                return new Pair<>("Mạnh", "#00FF00");
            default:
                return new Pair<>("Yếu", "#FF0000");
        }
    }


    private boolean kiem_tra_input() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();
        String userName = editTextUserName.getText().toString().trim();

        if (email.isEmpty() && password.isEmpty() && confirmPassword.isEmpty() && userName.isEmpty()) {
            CustomToast.showToast(this, "Vui lòng nhập thông tin");
            return false;
        } else if (email.isEmpty()) {
            CustomToast.showToast(this, "Vui lòng nhập email");
            return false;
        } else if (password.isEmpty()) {
            CustomToast.showToast(this, "Vui lòng nhập <Mật khẩu>");
            return false;
        } else if (confirmPassword.isEmpty()) {
            CustomToast.showToast(this, "Vui lòng nhập <Nhập lại mật khẩu>");
            return false;
        } else if (!password.equals(confirmPassword)) {
            CustomToast.showToast(this, "Mật khẩu và mật khẩu nhập lại không khớp");
            return false;
        } else if (userName.isEmpty()) {
            CustomToast.showToast(this, "Vui lòng nhập tên người dùng");
            return false;
        }

        return true;
    }

    private void create_account() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        if(password.length() < 8){
            CustomToast.showToast(this, "Mật khẩu phải có ít nhất 8 ký tự");
            return;
        }
        verificationCode = sendVerificationCode();
        Send.sendEmail(email, "Xác thực tài khoản","Mã xác thực của bạn là: " + verificationCode);
        CustomToast.showToast(this, "Vui lòng kiểm tra email để xác thực tài khoản");
        editTextVerificationCode.setVisibility(View.VISIBLE);
        buttonVerify.setVisibility(View.VISIBLE);
        buttonRegister.setVisibility(View.GONE);
        textViewMessage.setText("Nhập mã xác thực lấy từ mail gồm 6 số");
    }

    private void verifyCode() {
        String enteredCode = editTextVerificationCode.getText().toString().trim();
        if (enteredCode.equals(verificationCode)) {
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            create_user_in_database();
                            CustomToast.showToast(this, "Xác thực thành công! Tài khoản đã được tạo.");
                            startActivity(new Intent(CreateAccount.this, Login.class));
                        } else {
                            xu_ly(task.getException());
                        }
                    });
        } else {
            CustomToast.showToast(this, "Mã xác thực không đúng.");
        }
    }

    private void create_user_in_database() {
        String userId = auth.getCurrentUser().getUid();
        String email = editTextEmail.getText().toString().trim();
        String userName = editTextUserName.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");

        Map<String, Object> user = new HashMap<>();
        user.put("Email", email);
        user.put("UserName", userName);
        user.put("Password", password);
        user.put("DateJoined", time_now());
        user.put("ImgUser", "");
        user.put("GioiTinh", "");
        user.put("Address", "");
        user.put("Date", "");
        user.put("isAdmin", false);

        usersRef.child(userId).setValue(user).addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                CustomToast.showToast(this, "Đăng ký thất bại");
            }
        });
    }

    private String sendVerificationCode() {
        int min = 100000;
        int max = 999999;
        int randomCode = new Random().nextInt((max - min) + 1) + min;
        return String.valueOf(randomCode);
    }

    private void xu_ly(Exception exception) {
        if (exception instanceof FirebaseAuthUserCollisionException) {
            CustomToast.showToast(this, "Email đã tồn tại");
        } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
            CustomToast.showToast(this, "Email hoặc mật khẩu không hợp lệ");
        } else {
            CustomToast.showToast(this, "Đăng ký thất bại, vui lòng kiểm tra mạng!");
        }
    }

    private String time_now() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        return sdf.format(new Date());
    }
}
