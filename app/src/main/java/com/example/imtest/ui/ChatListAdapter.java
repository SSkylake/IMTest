package com.example.imtest.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.imtest.entity.FriendEntity;
import com.example.imtest.R;

import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatViewHolder> {

    private List<FriendEntity> friendList;
    private Context context;


    public ChatListAdapter(List<FriendEntity> friendList, Context context) {
        this.friendList = friendList;
        this.context = context;
    }


    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_item,viewGroup,false);
        return new ChatViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder chatViewHolder, int i) {

        final FriendEntity entity = friendList.get(i);
        chatViewHolder.chatText.setText(entity.getRecentChat());
        chatViewHolder.chatUsername.setText(entity.getTheOther());
        chatViewHolder.chatTime.setText(entity.getDate());

        if(entity.getNonReadingCount() == 0){
            chatViewHolder.chatCount.setVisibility(View.INVISIBLE);
        }else{
            chatViewHolder.chatCount.setVisibility(View.VISIBLE);
            chatViewHolder.chatCount.setText(String.valueOf(entity.getNonReadingCount()));
        }


        chatViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onClick(v,entity);

            }
        });

    }

    static class ChatViewHolder extends RecyclerView.ViewHolder{


        ImageView chatHead;
        TextView chatTime;
        TextView chatUsername;
        TextView chatText;
        TextView chatCount;


        ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            chatHead = itemView.findViewById(R.id.chat_head_img);
            chatTime = itemView.findViewById(R.id.chat_time);
            chatUsername = itemView.findViewById(R.id.chat_username);
            chatText = itemView.findViewById(R.id.chat_recent);
            chatCount = itemView.findViewById(R.id.non_reading_num);
        }
    }

    public interface OnItemClickListener{
        void onClick(View v, FriendEntity entity);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setFriendList(List<FriendEntity> friendList) {
        this.friendList = friendList;
        notifyDataSetChanged();
    }
}
