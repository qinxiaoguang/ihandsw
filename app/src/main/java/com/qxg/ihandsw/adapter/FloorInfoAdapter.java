package com.qxg.ihandsw.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qxg.ihandsw.R;
import com.qxg.ihandsw.model.bean.OrderFloorInfo;
import com.qxg.ihandsw.widget.LibraryWidget.ShowWebViewActivity;

import java.util.List;

/**
 * Created by Qking on 2016/11/8.
 */

public class FloorInfoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<OrderFloorInfo> floorInfos;
    private Activity activity;
    private Dialog dialog;

    public FloorInfoAdapter(Activity activity, List<OrderFloorInfo> floorInfos){
        this.activity = activity;
        this.floorInfos = floorInfos;
    }

    public FloorInfoAdapter(Activity activity, List<OrderFloorInfo> floorInfos, Dialog dialog){
        this(activity,floorInfos);
        this.dialog = dialog;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.adapter_floor_info,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final OrderFloorInfo info = floorInfos.get(position);
        ((MyHolder)holder).floor.setText(info.floor);
        ((MyHolder)holder).all.setText(info.all);
        ((MyHolder)holder).rest.setText(info.rest);
        ((MyHolder)holder).layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //点击事件
                Intent intent = new Intent(activity, ShowWebViewActivity.class);
                intent.putExtra("flag",ShowWebViewActivity.LOAD_ORDER);
                intent.putExtra("roomId",info.roomId);
                intent.putExtra("date",info.date);
                intent.putExtra("param",info.param);
                activity.startActivity(intent);
                //new ShowWebViewDialog(activity,ShowWebViewDialog.LOAD_ORDER,info.roomId,info.date).initView().show();
                if(dialog!=null)dialog.dismiss();
            }
        });
    }

    @Override
    public int getItemCount() {
        return floorInfos.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder{
        public TextView floor;
        public TextView all;
        public TextView rest;
        public View layout;

        public MyHolder(View itemView) {
            super(itemView);
            floor = (TextView) itemView.findViewById(R.id.floor);
            all = (TextView) itemView.findViewById(R.id.all);
            rest = (TextView) itemView.findViewById(R.id.rest);
            layout = itemView.findViewById(R.id.layout);
        }
    }
}
