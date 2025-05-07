package com.example.apptruyenchu.lichSu;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.apptruyenchu.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LichSu extends Fragment {
    private ListView lvLichSu;
    private DatabaseReference databaseReference;
    private ArrayList<LS> lichSuList;
    private AdapterLichSu adapter;
    private String user_id;
    private int itemsAdded = 0; // Khai báo biến itemsAdded
    ImageView progressBar;
    TextView tvLoading;
    AnimationDrawable loadingAnimation;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_lich_su, container, false);

        // Áp dụng padding cho view để phù hợp với hệ thống
        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Khởi tạo các thành phần
        khai_bao(view);
        loadLichSu();

        // Xử lý nút quay lại

        return view;
    }

    private void khai_bao(View view) {
        lvLichSu = view.findViewById(R.id.lvLichSu);
        lichSuList = new ArrayList<>();
        progressBar = view.findViewById(R.id.progressBar);
        tvLoading = view.findViewById(R.id.tvLoading);
        // Lấy user_id từ arguments
        Bundle bundle = getArguments();
        if (bundle != null) {
            user_id = bundle.getString("user_id");
        }

        // Khởi tạo tham chiếu đến cơ sở dữ liệu
        databaseReference = FirebaseDatabase.getInstance().getReference("LichSuDoc").child(user_id);
        adapter = new AdapterLichSu(getContext(), lichSuList);
        lvLichSu.setAdapter(adapter);
    }

    private void loadLichSu() {
        progressBar.setVisibility(View.VISIBLE);
        tvLoading.setVisibility(View.VISIBLE);
        progressBar.setBackgroundResource(R.drawable.frame_animation);
        loadingAnimation = (AnimationDrawable) progressBar.getBackground();
        loadingAnimation.start();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                lichSuList.clear(); // Xóa danh sách cũ
                itemsAdded = 0; // Reset số mục đã thêm
                int totalItems = (int) dataSnapshot.getChildrenCount(); // Số lượng mục trong snapshot

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String truyen_id = snapshot.getKey();
                    String chuong_id = snapshot.child("Chuong_id").getValue(String.class);
                    Long lastReadTime = snapshot.child("last_read_time").getValue(Long.class);

                    DatabaseReference truyenRef = FirebaseDatabase.getInstance().getReference("DataTruyen").child(truyen_id);
                    truyenRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot truyenSnapshot) {
                            if (truyenSnapshot.exists()) {
                                String tenTruyen = truyenSnapshot.child("TenTruyen").getValue(String.class);
                                String imgUrl = truyenSnapshot.child("Img_Url_Truyen").getValue(String.class);
                                Long slViewLong = truyenSnapshot.child("ViewAll").getValue(Long.class);
                                Long slStarLong = truyenSnapshot.child("TBSoSao").getValue(Long.class);
                                String tacGia = truyenSnapshot.child("TacGiaID").getValue(String.class);
                                String slView = slViewLong != null ? String.valueOf(slViewLong) : "0";
                                String slStar = slStarLong != null ? String.valueOf(slStarLong) : "0";

                                // Tạo đối tượng lichSu
                                LS lichSu = new LS(tenTruyen, chuong_id, imgUrl, slView, slStar, tacGia, truyen_id, user_id, lastReadTime);
                                lichSuList.add(lichSu);
                                itemsAdded++;

                                // Kiểm tra nếu đã thêm đủ mục
                                if (itemsAdded == totalItems) {
                                    lichSuList.sort((ls1, ls2) -> {
                                        Long time1 = ls1.getLastReadTime();
                                        Long time2 = ls2.getLastReadTime();

                                        // Sắp xếp theo thời gian đọc
                                        return (time2 != null ? time2 : 0) > (time1 != null ? time1 : 0) ? 1 : -1;
                                    });

                                    adapter.notifyDataSetChanged();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Xử lý lỗi nếu cần
                        }
                    });
                }
                loadingAnimation.stop();
                progressBar.setVisibility(View.GONE);
                tvLoading.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý lỗi nếu cần
            }
        });
    }
}
