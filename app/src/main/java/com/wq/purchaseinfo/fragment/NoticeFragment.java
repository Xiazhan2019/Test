package com.wq.purchaseinfo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.wq.purchaseinfo.R;

public class NoticeFragment extends Fragment {
    private View view;

    @Nullable
    @Override
    //加载创建的news_content_frag布局
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.notice_frag,container,false);
        return view;
    }
    public void refresh(String filtersTitle, String filtersContent){
        View visibilityLayout = view.findViewById(R.id.visibility_layout);
        visibilityLayout.setVisibility(View.VISIBLE);
        TextView newsTitleText = (TextView) view.findViewById(R.id.filters_title);
        TextView newsContentText = (TextView) view.findViewById(R.id.filters_content);
        newsTitleText.setText(filtersTitle);//刷新新闻的标题
        newsContentText.setText(filtersContent);//刷新新闻的内容
    }
}
