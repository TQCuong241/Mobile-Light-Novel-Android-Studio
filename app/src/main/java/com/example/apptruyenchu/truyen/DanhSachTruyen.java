package com.example.apptruyenchu.truyen;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.apptruyenchu.R;
import com.example.apptruyenchu.chuong.MainMota;
import com.example.apptruyenchu.search.SearchTruyen;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DanhSachTruyen extends Fragment {
    ListView lvDanhSachTruyen;
    ImageButton btnSearch, btnTheLoai;
    DatabaseReference databaseReference;
    ArrayList<Truyen> truyenList;
    String user_id;
    String selectedTheLoaiId = "";
    ImageView progressBar;
    TextView tvLoading;
    AnimationDrawable loadingAnimation; // Khai báo AnimationDrawable

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_danh_sach_truyen, container, false);

        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Bundle bundle = getArguments();
        if (bundle != null) {
            user_id = bundle.getString("user_id");
        }

        btnTheLoai = view.findViewById(R.id.btnTheLoai);
        progressBar = view.findViewById(R.id.progressBar);
        lvDanhSachTruyen = view.findViewById(R.id.lvDanhSachTruyen);
        truyenList = new ArrayList<>();
        tvLoading = view.findViewById(R.id.tvLoading);
        btnSearch = view.findViewById(R.id.btnSearch);

        btnSearch.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SearchTruyen.class);
            intent.putExtra("user_id", user_id);
            startActivity(intent);
        });

        btnTheLoai.setOnClickListener(v -> {
            the_loai();
        });
        xu_ly();
        return view;
    }

    private void the_loai() {
        DatabaseReference theLoaiRef = FirebaseDatabase.getInstance().getReference("TheLoai");
        theLoaiRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> theLoaiList = new ArrayList<>();
                ArrayList<String> theLoaiIdList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String tenTheLoai = snapshot.child("ten_the_loai").getValue(String.class);
                    String theLoaiId = snapshot.getKey();
                    theLoaiList.add(tenTheLoai);
                    theLoaiIdList.add(theLoaiId);
                }

                theLoaiList.add("Sắp xếp theo lượt xem");
                theLoaiIdList.add("view");
                theLoaiList.add("Sắp xếp theo chương mới nhất");
                theLoaiIdList.add("new_chuong");

                if (getContext() != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Chọn thể loại");
                    builder.setItems(theLoaiList.toArray(new String[0]), (dialog, which) -> {
                        selectedTheLoaiId = theLoaiIdList.get(which);
                        xu_ly();
                    });
                    builder.show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý lỗi nếu cần
            }
        });
    }

    private void xu_ly() {

        progressBar.setVisibility(View.VISIBLE);
        tvLoading.setVisibility(View.VISIBLE);
        progressBar.setBackgroundResource(R.drawable.frame_animation);
        loadingAnimation = (AnimationDrawable) progressBar.getBackground();
        loadingAnimation.start();

        databaseReference = FirebaseDatabase.getInstance().getReference("DataTruyen");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                truyenList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String tenTruyen = snapshot.child("TenTruyen").getValue(String.class);
                    String imgUrl = snapshot.child("Img_Url_Truyen").getValue(String.class);
                    Long slViewLong = snapshot.child("ViewAll").getValue(Long.class);
                    Long slStarLong = snapshot.child("TBSoSao").getValue(Long.class);
                    String tacGia = snapshot.child("TacGiaID").getValue(String.class);
                    String slView = String.valueOf(slViewLong);
                    String slStar = String.valueOf(slStarLong);
                    String truyenID = snapshot.getKey();
                    String theLoai = snapshot.child("the_loai").getValue(String.class);
                    Long update_truyen_now_long = snapshot.child("Update_Truyen_Now").getValue(Long.class);
                    String update_truyen_now = String.valueOf(update_truyen_now_long);

                    if (selectedTheLoaiId.isEmpty() || (theLoai != null && theLoai.equals(selectedTheLoaiId))) {
                        Truyen truyen = new Truyen(truyenID, tenTruyen, imgUrl, slView, slStar, tacGia, update_truyen_now);
                        truyenList.add(0, truyen);
                    } else if (selectedTheLoaiId.equals("view")) {
                        Truyen truyen = new Truyen(truyenID, tenTruyen, imgUrl, slView, slStar, tacGia, update_truyen_now);
                        truyenList.add(truyen);
                        truyenList.sort((truyen1, truyen2) -> {
                            Long views1 = Long.parseLong(truyen1.getViewAll());
                            Long views2 = Long.parseLong(truyen2.getViewAll());
                            return views2.compareTo(views1);
                        });
                    } else if (selectedTheLoaiId.equals("new_chuong")) {
                        Truyen truyen = new Truyen(truyenID, tenTruyen, imgUrl, slView, slStar, tacGia, update_truyen_now);
                        truyenList.add(truyen);
                        truyenList.sort((truyen1, truyen2) -> {
                            Long views1 = Long.parseLong(truyen1.getUpdate_truyen_now());
                            Long views2 = Long.parseLong(truyen2.getUpdate_truyen_now());
                            return views2.compareTo(views1);
                        });
                    }
                }

                if (getContext() != null) {
                    AdapterDSTruyen adapter = new AdapterDSTruyen(getContext(), truyenList);
                    lvDanhSachTruyen.setAdapter(adapter);
                }

                loadingAnimation.stop();
                progressBar.setVisibility(View.GONE);
                tvLoading.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        lvDanhSachTruyen.setOnItemClickListener((parent, view1, position, id) -> {
            Truyen selectedTruyen = truyenList.get(position);
            String truyen_id = selectedTruyen.getTruyenID();

            Intent intent = new Intent(getActivity(), MainMota.class);
            intent.putExtra("truyen_id", truyen_id);
            intent.putExtra("user_id", user_id);
            startActivity(intent);
        });
    }
}
