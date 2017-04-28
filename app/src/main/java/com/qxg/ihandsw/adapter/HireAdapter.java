package com.qxg.ihandsw.adapter;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qxg.ihandsw.R;
import com.qxg.ihandsw.model.bean.HireInfo;
import com.qxg.ihandsw.widget.SmallToolWidget.ShowStringWebView;

import java.util.List;

/**
 * Created by Qking on 2016/11/15.
 */

public class HireAdapter extends RecyclerView.Adapter<HireAdapter.MyHolder> {

    Context context;
    List<HireInfo> hires;
    Handler handler;

    public HireAdapter(Context context, List<HireInfo> hires){
        this.context = context;
        this.hires = hires;
        handler = new Handler(context.getMainLooper());
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_hire_info,null);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyHolder holder, int position) {
        final HireInfo hire = hires.get(position);
        holder.name.setText("" + hire.name);
        holder.date.setText("发布时间: "+ hire.date);
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //加载指定url的信息
                new ShowStringWebView(context,hire.href).initView().show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return hires.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView date;
        public View layout;

        public MyHolder(View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.layout);
            name = (TextView) itemView.findViewById(R.id.name);
            date = (TextView) itemView.findViewById(R.id.date);
        }
    }
}
