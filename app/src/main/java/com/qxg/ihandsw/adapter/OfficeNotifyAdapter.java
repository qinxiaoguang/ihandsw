package com.qxg.ihandsw.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qxg.ihandsw.R;
import com.qxg.ihandsw.model.bean.OfficeNotifyBean;

import java.util.List;

/**
 * Created by Qking on 2016/11/15.
 */

public class OfficeNotifyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    Context context;
    List<OfficeNotifyBean> notifys;

    public OfficeNotifyAdapter(Context context, List<OfficeNotifyBean> notifys) {
        this.context = context;
        this.notifys = notifys;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_office_notify,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final OfficeNotifyBean notify = notifys.get(position);

        ((MyHolder)holder).title.setText(notify.title.trim());
        ((MyHolder)holder).time.setText(notify.time);
        ((MyHolder)holder).layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(notify.url);
                intent.setData(content_url);
                context.startActivity(intent);
            }
        });
        //Log.i("title-->",notify.title);
        //Log.i("time-->",notify.time);
    }

    @Override
    public int getItemCount() {
        return notifys.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{

        public TextView title;
        public TextView time;
        public View layout;
        public MyHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            time = (TextView) itemView.findViewById(R.id.time);
            layout = itemView.findViewById(R.id.layout);
        }
    }
}
