package com.example.apptruyenchu.account;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.apptruyenchu.MainActivity;
import com.example.apptruyenchu.R;
import com.example.apptruyenchu.love.DanhSachLove;
import com.example.apptruyenchu.napTien.NapTien;
import com.example.apptruyenchu.toastSetting.CustomToast;
import com.example.apptruyenchu.upTruyen.TruyenDaDang;
import com.example.apptruyenchu.upTruyen.UpTruyen;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.app.Activity;
import android.net.Uri;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Profile extends Fragment {
    private SharedPreferences sharedPreferences;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private String userId;
    TextView profile_name, tvEmail, tvGioiTinh, tvDiaChi, tvDate;
    ImageView profile_image;
    ImageButton btn_logout, btn_update_image, btn_edit;
    Button btn_up_truyen, btn_xem_truyen, btn_naptien;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_profile, container, false);
        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        khai_bao(view);

        if (getArguments() != null) {
            userId = getArguments().getString("user_id");
        }

        btn_naptien.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), NapTien.class);
            intent.putExtra("user_id", userId);
            startActivity(intent);
        });

        storageReference = FirebaseStorage.getInstance().getReference("profile_images").child(userId);
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String userName = snapshot.child("UserName").getValue(String.class);
                String url_img = snapshot.child("ImgUser").getValue(String.class);
                String email = snapshot.child("Email").getValue(String.class);
                Boolean isAdmin = snapshot.child("isAdmin").getValue(Boolean.class);
                String date = snapshot.child("Date").getValue(String.class);
                String gender = snapshot.child("GioiTinh").getValue(String.class);
                String address = snapshot.child("Address").getValue(String.class);

                if (isAdmin != null && isAdmin) {
                    btn_up_truyen.setVisibility(View.VISIBLE);
                    btn_xem_truyen.setVisibility(View.VISIBLE);
                }
                else {
                    btn_up_truyen.setVisibility(View.GONE);
                    btn_xem_truyen.setVisibility(View.GONE);
                }

                profile_name.setText(userName);
                tvEmail.setText(email);
                if(gender == ""){
                    gender = "Chưa cập nhật";
                }
                if(address == ""){
                    address = "Chưa cập nhật";
                }
                if(date == ""){
                    date = "Chưa cập nhật";
                }
                tvGioiTinh.setText(gender);
                tvDiaChi.setText(address);
                tvDate.setText(date);

                if(getActivity() != null)
                {
                    if(url_img != null){
                        Glide.with(getActivity())
                                .load(url_img).into(profile_image);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btn_logout.setOnClickListener(v -> {
            sharedPreferences = getActivity().getSharedPreferences("LoginPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isLoggedIn", false);
            editor.apply();

            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();

        });

        btn_up_truyen.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), UpTruyen.class);
            intent.putExtra("user_id", userId);
            startActivity(intent);
        });

        btn_xem_truyen.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), TruyenDaDang.class);
            intent.putExtra("user_id", userId);
            startActivity(intent);
        });

        btn_edit.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfile.class);
            intent.putExtra("user_id", userId);
            startActivity(intent);
        });

        btn_update_image.setOnClickListener(v -> openFileChooser());

        return view;
    }

    // Phương thức chọn ảnh từ thư viện
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Kết quả sau khi chọn ảnh
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            profile_image.setImageURI(imageUri);  // Hiển thị ảnh đã chọn

            uploadImageToFirebase();
        }
    }

    // Tải ảnh lên Firebase Storage
    private void uploadImageToFirebase() {
        if (imageUri != null) {
            StorageReference fileReference = storageReference.child(System.currentTimeMillis() + ".jpg");

            fileReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Cập nhật đường dẫn ảnh đại diện mới vào Firebase Database
                        databaseReference.child("ImgUser").setValue(uri.toString());
                        Toast.makeText(getActivity(), "Cập nhật ảnh đại diện thành công", Toast.LENGTH_SHORT).show();
                    }))
                    .addOnFailureListener(e -> {
                        Toast.makeText(getActivity(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void khai_bao(View view){
        profile_name = view.findViewById(R.id.profile_name);
        tvEmail = view.findViewById(R.id.tvEmail);
        profile_image = view.findViewById(R.id.profile_image);
        btn_logout = view.findViewById(R.id.btn_logout);
        btn_up_truyen = view.findViewById(R.id.btn_up_truyen);
        btn_xem_truyen  = view.findViewById(R.id.btn_xem_truyen);
        btn_update_image = view.findViewById(R.id.btn_update_image);
        btn_edit = view.findViewById(R.id.btn_edit);
        tvGioiTinh = view.findViewById(R.id.tvGioiTinh);
        tvDiaChi = view.findViewById(R.id.tvDiaChi);
        tvDate = view.findViewById(R.id.tvDate);
        btn_naptien = view.findViewById(R.id.btn_naptien);
    }
}
