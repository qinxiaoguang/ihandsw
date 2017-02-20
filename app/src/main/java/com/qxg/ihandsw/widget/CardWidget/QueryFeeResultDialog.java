package com.qxg.ihandsw.widget.CardWidget;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.qxg.ihandsw.R;
import com.qxg.ihandsw.adapter.QueryFeeAdapter;
import com.qxg.ihandsw.model.CardModel;
import com.qxg.ihandsw.model.bean.QueryFee;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by qxg on 17-2-20.
 */

public class QueryFeeResultDialog extends BaseBottomDialog {

    int day = 1;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.query_result_listview)
    RecyclerView queryResultListview;
    @BindView(R.id.error)
    TextView error;

    public QueryFeeResultDialog(Context context) {
        this(context, 1);
    }

    public QueryFeeResultDialog(Context context, int day) {
        super(context);
        this.day = day;
    }

    @Override
    public void initView() {
        super.initView();
        View view = LayoutInflater.from(context).inflate(R.layout.query_fee_result, null);
        ButterKnife.bind(this,view);

        cardModel.setOnQueryFeeListener(new CardModel.onQueryFeeListener() {
            @Override
            public void onStart() {
                //啥事都不做
            }

            @Override
            public void onSucess(final List<QueryFee> feeLists) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //将progressBar弄消失
                        progressBar.setVisibility(View.GONE);

                        queryResultListview.setAdapter(new QueryFeeAdapter(context,feeLists));
                        queryResultListview.setLayoutManager(new LinearLayoutManager(context));
                    }
                });
            }

            @Override
            public void empty() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //将progressBar弄消失
                        progressBar.setVisibility(View.GONE);
                        error.setText("没有记录");
                        error.setVisibility(View.VISIBLE);

                    }
                });
            }
        });
        cardModel.queryFee(day,1);

        setContentView(view);
    }
}
