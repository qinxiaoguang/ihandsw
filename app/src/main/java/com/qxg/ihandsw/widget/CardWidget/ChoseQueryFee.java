package com.qxg.ihandsw.widget.CardWidget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.qxg.ihandsw.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by qxg on 17-2-20.
 */

public class ChoseQueryFee extends BaseBottomDialog {

    public ChoseQueryFee(Context context) {
        super(context);
    }

    @Override
    public void initView() {
        super.initView();
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_query_fee, null);
        ButterKnife.bind(this,view);

        setContentView(view);
    }

    @OnClick({R.id.today_query, R.id.three_day_query, R.id.seven_day_query})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.today_query:
                //查询当日流水
                showBottomDialog(new QueryFeeResultDialog(context,1));
                break;
            case R.id.three_day_query:
                //查询三日流水
                showBottomDialog(new QueryFeeResultDialog(context,3));
                break;
            case R.id.seven_day_query:
                //查询七日流水
                showBottomDialog(new QueryFeeResultDialog(context,7));
                break;


        }
    }

    private void showBottomDialog(BaseBottomDialog dialog){
        dialog.initView();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
}
