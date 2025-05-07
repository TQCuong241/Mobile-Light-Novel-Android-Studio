package com.example.apptruyenchu.comment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.apptruyenchu.R;

import java.util.ArrayList;

public class CommentAdapter extends ArrayAdapter<CommentItem> {

    public CommentAdapter(Context context, ArrayList<CommentItem> comments) {
        super(context, 0, comments);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CommentItem commentItem = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.comment_item, parent, false);
        }

        LinearLayout left = convertView.findViewById(R.id.left);
        TextView textViewUserName = convertView.findViewById(R.id.textViewUserName);
        TextView textViewComment = convertView.findViewById(R.id.textViewComment);
        TextView textViewDate = convertView.findViewById(R.id.textViewDate);
        ImageView imgUser = convertView.findViewById(R.id.imgUser);

        LinearLayout right = convertView.findViewById(R.id.right);
        TextView textViewUserNameRight = convertView.findViewById(R.id.textViewUserNameRight);
        TextView textViewCommentRight = convertView.findViewById(R.id.textViewCommentRight);
        TextView textViewDateRight = convertView.findViewById(R.id.textViewDateRight);
        ImageView imgUserRight = convertView.findViewById(R.id.imgUserRight);

        left.setVisibility(View.GONE);
        right.setVisibility(View.GONE);

        if (commentItem.isCurrentUserComment()) {
            right.setVisibility(View.VISIBLE);
            textViewUserNameRight.setText(commentItem.getUserName());
            textViewCommentRight.setText(commentItem.getND_Comment());
            textViewDateRight.setText(commentItem.getRelativeTime());

            if (commentItem.getImgUser() != null && !commentItem.getImgUser().isEmpty()) {
                Glide.with(getContext())
                        .load(commentItem.getImgUser())
                        .apply(new RequestOptions().placeholder(R.drawable.user_hehe))
                        .into(imgUserRight);
            } else {
                imgUserRight.setImageResource(R.drawable.user_hehe);
            }
        } else {
            left.setVisibility(View.VISIBLE);
            textViewUserName.setText(commentItem.getUserName());
            textViewComment.setText(commentItem.getND_Comment());
            textViewDate.setText(commentItem.getRelativeTime());

            if (commentItem.getImgUser() != null && !commentItem.getImgUser().isEmpty()) {
                Glide.with(getContext())
                        .load(commentItem.getImgUser())
                        .apply(new RequestOptions().placeholder(R.drawable.user_hehe))
                        .into(imgUser);
            } else {
                imgUser.setImageResource(R.drawable.user_hehe);
            }
        }
        return convertView;
    }
}
