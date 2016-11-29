package com.qxg.ihandsw.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.IconRoundCornerProgressBar;
import com.akexorcist.roundcornerprogressbar.common.BaseRoundCornerProgressBar;
import com.qxg.ihandsw.R;
import com.qxg.ihandsw.model.bean.SeatInfo;

import java.util.List;

/**
 * Created by Qking on 2016/11/11.
 */

public class SeatsInfoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    Context context;
    List<SeatInfo> seatInfos;

    public SeatsInfoAdapter(Context context, List<SeatInfo> seatInfos){
        this.context = context;
        this.seatInfos = seatInfos;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_item_seat_info,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        SeatInfo seatInfo = seatInfos.get(position);
        ((MyHolder)holder).tl.setText(seatInfo.floor + ":余" + seatInfo.rest);
        ((MyHolder)holder).tr.setText("总" + seatInfo.all);
        int all = Integer.parseInt(seatInfo.all);
        int res = Integer.parseInt(seatInfo.rest);

        ((MyHolder)holder).p.setMax(all);
        ((MyHolder)holder).p.setProgress(res);

        if(res<10){
            ((MyHolder)holder).p.setProgressColor(Color.parseColor("#FF4081"));
            ((IconRoundCornerProgressBar)((MyHolder)holder).p).setIconBackgroundColor(Color.parseColor("#fe216c"));
        }else{
            ((MyHolder)holder).p.setProgressColor(Color.parseColor("#33B5E5"));
        }

        //Log.i("res/all",((MyHolder)holder).p.getProgress() + "/" + ((MyHolder)holder).p.getMax());
        ((MyHolder)holder).p.postInvalidate();
    }

    @Override
    public int getItemCount() {
        return seatInfos.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder {

        public TextView tl;
        public TextView tr;
        public BaseRoundCornerProgressBar p;

        public MyHolder(View itemView) {
            super(itemView);
            tl = (TextView) itemView.findViewById(R.id.tl);
            tr = (TextView) itemView.findViewById(R.id.tr);
            p = (BaseRoundCornerProgressBar) itemView.findViewById(R.id.p);
        }
    }
}
