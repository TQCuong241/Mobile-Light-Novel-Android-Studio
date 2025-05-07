package com.example.apptruyenchu.upTruyen;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView; // Import ImageView
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.apptruyenchu.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class UpTruyen extends AppCompatActivity {
    EditText etTenTruyen, etTenTacGia, etMoTaTruyen;
    Button btnDangTruyen, btnChonHinh;
    ImageView imageView; // Khai báo ImageView
    DatabaseReference databaseReference;
    Spinner spinnerTheLoai;
    String userId;
    ImageButton btnBack;
    Uri imageUri; // Để lưu trữ URI hình ảnh đã chọn
    private static final int PICK_IMAGE_REQUEST = 1; // Mã yêu cầu để chọn hình ảnh

    // Tạo HashMap để ánh xạ thể loại và ID
    private final HashMap<String, String> theLoaiMap = new HashMap<String, String>() {{
        put("Huyền huyễn", "1");
        put("Đô thị", "2");
        put("Kiếm hiệp", "3");
        put("Khoa huyễn", "4");
        put("Võng du", "5");
        put("Huyền Nghi", "6");
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_up_truyen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("DataTruyen");
        userId = getIntent().getStringExtra("user_id");
        khai_bao();
        btnBack.setOnClickListener(v -> finish());
        btnChonHinh.setOnClickListener(v -> openGallery());
        btnDangTruyen.setOnClickListener(v -> uploadImage());
    }

    private void khai_bao() {
        etTenTruyen = findViewById(R.id.etTenTruyen);
        etTenTacGia = findViewById(R.id.etTenTacGia);
        spinnerTheLoai = findViewById(R.id.spinnerTheLoai); // Khởi tạo Spinner
        etMoTaTruyen = findViewById(R.id.etMoTaTruyen);
        btnDangTruyen = findViewById(R.id.btnDangTruyen);
        btnChonHinh = findViewById(R.id.btnChonHinh);
        imageView = findViewById(R.id.imageView); // Khởi tạo ImageView
        btnBack = findViewById(R.id.btnBack);
        // Sử dụng layout tùy chỉnh cho Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.the_loai_array, R.layout.spinner_item); // Sử dụng layout tùy chỉnh
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTheLoai.setAdapter(adapter);
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Chọn hình ảnh"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }
    }

    private void uploadImage() {
        if (imageUri != null) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference("images/" + System.currentTimeMillis() + ".jpg");
            storageReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        luuTruyen(imageUrl);
                    }))
                    .addOnFailureListener(e -> Toast.makeText(UpTruyen.this, "Lỗi tải hình ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "Vui lòng chọn hình ảnh!", Toast.LENGTH_SHORT).show();
        }
    }

    private void luuTruyen(String imageUrl) {
        String tenTruyen = etTenTruyen.getText().toString().trim();
        String tenTacGia = etTenTacGia.getText().toString().trim();
        String theLoai = theLoaiMap.get(spinnerTheLoai.getSelectedItem().toString().trim());
        String moTa = etMoTaTruyen.getText().toString().trim();
        long currentTimeMillis = System.currentTimeMillis();
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(currentDate);

        if (tenTruyen.isEmpty() || tenTacGia.isEmpty() || theLoai.isEmpty() || moTa.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        String truyenID = databaseReference.push().getKey();

        // Tạo Map để lưu trữ dữ liệu
        Map<String, Object> truyenData = new HashMap<>();
        truyenData.put("TenTruyen", tenTruyen);
        truyenData.put("TacGiaID", tenTacGia);
        truyenData.put("the_loai", theLoai);
        truyenData.put("Mota", moTa);
        truyenData.put("Img_Url_Truyen", imageUrl);
        truyenData.put("Update_Truyen", formattedDate);
        truyenData.put("Update_Truyen_Now", currentTimeMillis);
        truyenData.put("ViewAll", 0);
        truyenData.put("TBSoSao", 5.0);
        truyenData.put("userId", userId);

        // Lưu trữ dữ liệu vào Firebase
        databaseReference.child(truyenID).setValue(truyenData)
                .addOnCompleteListener(task -> {
                    Toast.makeText(UpTruyen.this, "Đăng truyện thành công!", Toast.LENGTH_SHORT).show();
                    // Xóa dữ liệu trong các EditText
                    etTenTruyen.setText("");
                    etTenTacGia.setText("");
                    etMoTaTruyen.setText("");
                    imageView.setImageURI(null); // Xóa hình ảnh trong ImageView
                })
                .addOnFailureListener(e -> Toast.makeText(UpTruyen.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
