package com.wq.purchaseinfo.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wq.purchaseinfo.R;
import com.wq.purchaseinfo.activity.NoticeActivity;
import com.wq.purchaseinfo.entity.Notice;

import java.util.List;

public class HomeAdapt extends RecyclerView.Adapter {
    private Context context;
    private List<String> data;
    private List<Notice> nList;

    public HomeAdapt(Context context, List<String> data, List<Notice> nList){
        this.context = context;
        this.data = data;
        this.nList = nList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.allitem_title_view,null);
        return new TextHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final TextHolder tHolder = (TextHolder) holder;
        tHolder.textView.setText(data.get(position));
        tHolder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NoticeActivity.actionStart(tHolder.textView.getContext(), nList.get(position).getTitle(), nList.get(position).getContent());
//                Toast.makeText(context,"点击了记录"+ position,Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private class TextHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        public TextHolder(View v) {
            super(v);
            textView = v.findViewById(R.id.title);
        }
    }
}
