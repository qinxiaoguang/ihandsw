package com.qxg.ihandsw.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qxg.ihandsw.R;
import com.qxg.ihandsw.model.bean.QueryFee;

import java.util.List;

/**
 * Created by qxg on 17-2-20.
 */

public class QueryFeeAdapter extends RecyclerView.Adapter<QueryFeeAdapter.MyHolder>{

    Context context;
    List<QueryFee> feeLists;

    public QueryFeeAdapter(Context context, List<QueryFee> feeLists){
        this.context = context;
        this.feeLists = feeLists;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_query_fee,null);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        ((MyHolder)holder).t.setText(feeLists.get(position).toString());
    }

    @Override
    public int getItemCount() {
        return feeLists.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder{

        TextView t;
        public MyHolder(View itemView) {
            super(itemView);
            t = (TextView) itemView.findViewById(R.id.record);
        }
    }
}
