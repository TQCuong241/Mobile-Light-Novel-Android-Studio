package com.example.apptruyenchu.account;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
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

public class EditProfile extends AppCompatActivity {
    private ImageButton btnBack;
    private EditText etName, etEmail, etYearOfBirth, etAddress;
    private RadioGroup rgGioiTinh;
    private RadioButton rbNam, rbNu, rbKhac;
    private Button btnSave;
    private String userId;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        khai_bao();
        show();
        btnBack.setOnClickListener(v -> finish());

        etYearOfBirth.addTextChangedListener(new TextWatcher() {
            private String current = "";
            private final String ddmmyyyy = "dd-MM-yyyy";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    String clean = s.toString().replaceAll("[^\\d]", "");
                    StringBuilder formatted = new StringBuilder();

                    for (int i = 0; i < clean.length(); i++) {
                        formatted.append(clean.charAt(i));
                        if ((i == 1 || i == 3) && i < clean.length() - 1) {
                            formatted.append("-");
                        }
                    }

                    current = formatted.toString();
                    etYearOfBirth.setText(current);
                    etYearOfBirth.setSelection(current.length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString();
            String email = etEmail.getText().toString();
            String yearOfBirth = etYearOfBirth.getText().toString();
            String address = etAddress.getText().toString();
            String gioiTinh = "";

            int selectedId = rgGioiTinh.getCheckedRadioButtonId();
            if (selectedId == R.id.rb_male) {
                gioiTinh = "Nam";
            } else if (selectedId == R.id.rb_female) {
                gioiTinh = "Nữ";
            } else if (selectedId == R.id.rb_other) {
                gioiTinh = "Khác";
            }

            databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
            databaseReference.child("UserName").setValue(name);
            databaseReference.child("Email").setValue(email);
            databaseReference.child("Date").setValue(yearOfBirth);
            databaseReference.child("Address").setValue(address);
            databaseReference.child("GioiTinh").setValue(gioiTinh);
            CustomToast.showToast(this, "Cập nhật thành công");
            finish();
        });
    }

    private void khai_bao(){
        btnBack = findViewById(R.id.btnBack);
        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etYearOfBirth = findViewById(R.id.et_year_of_birth);
        etAddress = findViewById(R.id.et_address);
        btnSave = findViewById(R.id.btn_save);
        rbNu = findViewById(R.id.rb_female);
        rbNam = findViewById(R.id.rb_male);
        rbKhac = findViewById(R.id.rb_other);
        rgGioiTinh = findViewById(R.id.rg_gender);
        userId = getIntent().getStringExtra("user_id");
    }

    private void show() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Lấy các thông tin từ snapshot
                String userName = snapshot.child("UserName").getValue(String.class);
                String email = snapshot.child("Email").getValue(String.class);
                String yearOfBirth = snapshot.child("Date").getValue(String.class);
                String address = snapshot.child("Address").getValue(String.class);
                String gioiTinh = snapshot.child("GioiTinh").getValue(String.class);

                // Thiết lập thông tin vào các EditText
                if (userName != null) {
                    etName.setText(userName);
                }
                if (email != null) {
                    etEmail.setText(email);
                }
                if (yearOfBirth != null) {
                    etYearOfBirth.setText(yearOfBirth);
                }
                if (address != null) {
                    etAddress.setText(address);
                }

                if (gioiTinh != null) {
                    if (gioiTinh.equals("Nữ")) {
                        rbNu.setChecked(true);
                    } else if (gioiTinh.equals("Nam")) {
                        rbNam.setChecked(true);
                    } else {
                        rbKhac.setChecked(true);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }


}
