package com.qxg.ihandsw.widget.SmallToolWidget;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import com.qxg.ihandsw.R;
import com.qxg.ihandsw.adapter.SchoolDateAdapter;
import com.qxg.ihandsw.model.SmallToolModel;
import com.qxg.ihandsw.model.bean.SchoolDate;
import com.qxg.ihandsw.widget.CardWidget.BaseBottomDialog;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Qking on 2016/11/15.
 */

public class SchoolDateDialog extends BaseBottomDialog {

    @BindView(R.id.school_date_list_view)
    RecyclerView schoolDateListView;
    @BindView(R.id.loading)
    ProgressBar loading;

    public SchoolDateDialog(Context context) {
        super(context);
    }

    @Override
    public void initView() {
        super.initView();
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_school_date, null);
        ButterKnife.bind(this, view);


        smallToolModel.setOnLoadSchoolDateListener(new SmallToolModel.OnLoadSchoolDateListener() {
            @Override
            public void onFailed() {
                loading.setVisibility(View.GONE);
            }

            @Override
            public void onSucess(List<SchoolDate> dates) {
                //成功返回数据
                schoolDateListView.setAdapter(new SchoolDateAdapter(context, dates));
                schoolDateListView.setLayoutManager(new LinearLayoutManager(context));
                loading.setVisibility(View.GONE);
                schoolDateListView.setVisibility(View.VISIBLE);
            }
        });
        smallToolModel.loadSchoolDate();

        setContentView(view);
    }
}
