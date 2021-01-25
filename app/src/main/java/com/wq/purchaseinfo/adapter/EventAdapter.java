package com.wq.purchaseinfo.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wq.purchaseinfo.R;
import com.wq.purchaseinfo.activity.AddEventActivity;

import java.util.List;
import java.util.Map;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    private List<Map<String,String>> mContentList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        View ticklerView;
        TextView contentText;
        TextView showTime;

        public ViewHolder(View view){
            super(view);
            ticklerView=view;
            contentText=(TextView) view.findViewById(R.id.show_content);
            showTime=(TextView) view.findViewById(R.id.show_time);
        }
    }

    public EventAdapter(List<Map<String,String>> contentList){
        mContentList=contentList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item,parent,false);
        //设置子项点击事件，并传递数据到添加页
        final ViewHolder holder=new ViewHolder(view);
        holder.ticklerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position=holder.getAdapterPosition();
                String content=mContentList.get(position).get("content");
                String time=mContentList.get(position).get("time");
                Intent intent=new Intent(parent.getContext(), AddEventActivity.class);
                intent.putExtra(AddEventActivity.CONTENT,content);
                intent.putExtra(AddEventActivity.TIME,time);
                parent.getContext().startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.contentText.setText(mContentList.get(position).get("content"));
        holder.showTime.setText(mContentList.get(position).get("time"));
    }

    @Override
    public int getItemCount() {
        return mContentList.size();
    }
}
