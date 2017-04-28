package com.qxg.ihandsw.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qxg.ihandsw.R;
import com.qxg.ihandsw.model.bean.WorkArrange;

import java.util.List;

/**
 * Created by Qking on 2016/11/15.
 */

public class WorkArrangeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<WorkArrange> arranges;
    public WorkArrangeAdapter(Context context, List<WorkArrange> arranges){
        this.context = context;
        this.arranges = arranges;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_work_arrange,null);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        WorkArrange arrange = arranges.get(position);
        ((MyHolder)holder).name.setText("名称: " + arrange.name);
        ((MyHolder)holder).time.setText("时间: "+ arrange.date + " " + arrange.time);
        ((MyHolder)holder).address.setText("地点: " + arrange.address);
        ((MyHolder)holder).joinPeople.setText("参加人员: " + arrange.joinPeople);
        ((MyHolder)holder).responsible.setText("负责部门: "+ arrange.responsible);
    }

    @Override
    public int getItemCount() {
        return arranges.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{
        public TextView name;
        public TextView time;
        public TextView address;
        public TextView joinPeople;
        public TextView responsible;
        public MyHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            time = (TextView) itemView.findViewById(R.id.time);
            address = (TextView) itemView.findViewById(R.id.address);
            joinPeople = (TextView) itemView.findViewById(R.id.joinPeople);
            responsible = (TextView) itemView.findViewById(R.id.responsible);
        }
    }
}
