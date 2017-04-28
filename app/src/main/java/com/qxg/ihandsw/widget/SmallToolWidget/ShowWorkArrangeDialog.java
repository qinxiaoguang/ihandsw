package com.qxg.ihandsw.widget.SmallToolWidget;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import com.qxg.ihandsw.R;
import com.qxg.ihandsw.adapter.WorkArrangeAdapter;
import com.qxg.ihandsw.model.SmallToolModel;
import com.qxg.ihandsw.model.bean.WorkArrange;
import com.qxg.ihandsw.widget.CardWidget.BaseBottomDialog;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Qking on 2016/11/15.
 */

public class ShowWorkArrangeDialog extends BaseBottomDialog {

    @BindView(R.id.loading)
    ProgressBar loading;
    @BindView(R.id.work_arrange_list_view)
    RecyclerView workArrangeListView;

    public ShowWorkArrangeDialog(Context context) {
        super(context);
    }

    @Override
    public void initView() {
        super.initView();
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_work_arrange, null);
        ButterKnife.bind(this, view);

        //load近期工作安排
        smallToolModel.setOnLoadWorkArrangeListener(new SmallToolModel.OnLoadWorkArrangeListener() {
            @Override
            public void onFailed() {

            }

            @Override
            public void onSucess(List<WorkArrange> arranges) {
                //读取成功，就开始显示吧
                loading.setVisibility(View.GONE);
                workArrangeListView.setAdapter(new WorkArrangeAdapter(context,arranges));
                workArrangeListView.setLayoutManager(new LinearLayoutManager(context));
                workArrangeListView.setVisibility(View.VISIBLE);
            }
        });
        smallToolModel.loadWorkArrange();
        setContentView(view);
    }
}
