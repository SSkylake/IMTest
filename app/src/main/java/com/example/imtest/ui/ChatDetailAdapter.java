package com.example.imtest.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.imtest.entity.ChatHistoryEntity;
import com.example.imtest.R;

import java.util.List;

public class ChatDetailAdapter extends RecyclerView.Adapter<ChatDetailAdapter.ViewHolder> {

    private List<ChatHistoryEntity> historyEntityList;

    private Context context;

    public ChatDetailAdapter(List<ChatHistoryEntity> historyEntityList, Context context) {
        this.historyEntityList = historyEntityList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        ViewHolder viewHolder = null;
        if (i == 0) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_receive, viewGroup, false);
            viewHolder = new ViewHolder(view);
            viewHolder.setImgHead((ImageView) view.findViewById(R.id.re_head));
            viewHolder.setTvText((TextView) view.findViewById(R.id.re_text));
            viewHolder.setTvTime((TextView) view.findViewById(R.id.re_time));

        } else {

            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_send, viewGroup, false);
            viewHolder = new ViewHolder(view);
            viewHolder.setImgHead((ImageView) view.findViewById(R.id.send_head));
            viewHolder.setTvText((TextView) view.findViewById(R.id.send_text));
            viewHolder.setTvTime((TextView) view.findViewById(R.id.send_time));
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        ChatHistoryEntity entity = historyEntityList.get(i);

        viewHolder.tvText.setText(entity.getText());
        switch (entity.getState()) {
            case ChatHistoryEntity.Type.confirmed:
                viewHolder.tvTime.setText(entity.getDate());
                break;
            case ChatHistoryEntity.Type.failed:
                viewHolder.tvTime.setText("Failed");
                viewHolder.tvTime.setTextColor(context.getColor(R.color.colorAccent));
                break;
            case ChatHistoryEntity.Type.unConfirmed:
                viewHolder.tvTime.setText("Sending");
                break;
            default:
        }

    }

    @Override
    public int getItemCount() {
        return historyEntityList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return historyEntityList.get(position).isSendOrReceive() ? 1 : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgHead;
        TextView tvText;
        TextView tvTime;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void setImgHead(ImageView imgHead) {
            this.imgHead = imgHead;
        }

        public void setTvText(TextView tvText) {
            this.tvText = tvText;
        }

        public void setTvTime(TextView tvTime) {
            this.tvTime = tvTime;
        }
    }

    public void setHistoryEntityList(List<ChatHistoryEntity> historyEntityList) {
        this.historyEntityList = historyEntityList;
        notifyDataSetChanged();
    }
}
