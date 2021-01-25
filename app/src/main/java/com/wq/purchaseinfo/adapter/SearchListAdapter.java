package com.wq.purchaseinfo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wq.purchaseinfo.R;
import com.wq.purchaseinfo.activity.SearchActivity;
import com.wq.purchaseinfo.entity.Trace;

import java.util.ArrayList;
import java.util.List;

public class SearchListAdapter extends BaseAdapter {
    private Context context;
    private List<com.wq.purchaseinfo.entity.Trace> traces = new ArrayList<>(1);
    private static final int TYPE_TOP = 0x0000;
    private static final int TYPE_NORMAL= 0x0001;

    public SearchListAdapter(SearchActivity context, List<com.wq.purchaseinfo.entity.Trace> traces) {
        this.context = context;
        this.traces = traces;
    }

    @Override
    public int getCount() {
        return traces.size();
    }

    @Override
    public Trace getItem(int position) {
        return traces.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
      //  final Trace trace = getItem(position);
        final com.wq.purchaseinfo.entity.Trace trace = getItem(position);
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.search_view, parent, false);
            holder.tvAcceptTime = (TextView) convertView.findViewById(R.id.tvAcceptTime);
            holder.tvAcceptStation = (TextView) convertView.findViewById(R.id.tvAcceptStation);
            holder.tvTopLine = (TextView) convertView.findViewById(R.id.tvTopLine);
            holder.tvDot = (TextView) convertView.findViewById(R.id.tvDot);
            convertView.setTag(holder);
        }

        if (getItemViewType(position) == TYPE_TOP) {
            // 第一行头的竖线不显示
            holder.tvTopLine.setVisibility(View.INVISIBLE);
            // 字体颜色加深
            holder.tvAcceptTime.setTextColor(0xff555555);
            holder.tvAcceptStation.setTextColor(0xff555555);
            holder.tvDot.setBackgroundResource(R.drawable.timeline_dot_first);
        } else if (getItemViewType(position) == TYPE_NORMAL) {
            holder.tvTopLine.setVisibility(View.VISIBLE);
            holder.tvAcceptTime.setTextColor(0xff999999);
            holder.tvAcceptStation.setTextColor(0xff999999);
            holder.tvDot.setBackgroundResource(R.drawable.timeline_dot_normal);
        }


        holder.tvAcceptTime.setText(trace.getAcceptTime());
        holder.tvAcceptStation.setText(trace.getAcceptStation());
        return convertView;
    }

    @Override
    public int getItemViewType(int id) {
        if (id == 0) {
            return TYPE_TOP;
        }
        return TYPE_NORMAL;
    }

    static class ViewHolder {
        public TextView tvAcceptTime, tvAcceptStation;
        public TextView tvTopLine, tvDot;
    }
}