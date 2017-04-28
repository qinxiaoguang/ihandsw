package com.qxg.ihandsw.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qxg.ihandsw.R;
import com.qxg.ihandsw.model.bean.SchoolDate;
import com.qxg.ihandsw.widget.SmallToolWidget.ShowSchoolDateImgDialog;

import java.util.List;

/**
 * Created by Qking on 2016/11/15.
 */

public class SchoolDateAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    Context context;
    List<SchoolDate> dates;
    public SchoolDateAdapter(Context context, List<SchoolDate> dates){
        this.context = context;
        this.dates = dates;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_school_date,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final SchoolDate date = dates.get(position);
        ((MyHolder)holder).title.setText(date.title);
        ((MyHolder)holder).layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //进入相应的展示图片的那啥中
                new ShowSchoolDateImgDialog(context,date.url).initView().show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return dates.size();
    }
    class MyHolder extends RecyclerView.ViewHolder{
        public TextView title;
        public View layout;
        public MyHolder(View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.layout);
            title = (TextView) itemView.findViewById(R.id.title);
        }
    }
}
