package com.example.apptruyenchu.toastSetting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.apptruyenchu.R;

public class CustomToast {

    // Phương thức tiện ích để hiển thị Toast tùy chỉnh
    public static void showToast(Context context, String message) {
        // Nạp layout tùy chỉnh từ R.layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.custom_toast, null);

        // Tìm TextView và ImageView từ layout
        TextView text = layout.findViewById(R.id.toast_text);
        text.setText(message);

        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }
}
