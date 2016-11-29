package com.qxg.ihandsw.widget.SmallToolWidget;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.qxg.ihandsw.R;
import com.qxg.ihandsw.adapter.OfficeNotifyAdapter;
import com.qxg.ihandsw.model.SmallToolModel;
import com.qxg.ihandsw.model.bean.OfficeNotifyBean;
import com.qxg.ihandsw.widget.CardWidget.BaseBottomDialog;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;

/**
 * Created by Qking on 2016/11/15.
 */

public class OfficeNotifyDialog extends BaseBottomDialog {

    @BindView(R.id.office_notify_list_view)
    RecyclerView officeNotifyListView;
    @BindView(R.id.loading)
    ProgressBar loading;
    @BindView(R.id.layout)
    LinearLayout layout;

    public OfficeNotifyDialog(Context context) {
        super(context);
    }

    @Override
    public void initView() {
        super.initView();
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_office_notify, null);
        ButterKnife.bind(this, view);

        //开始读取近期通知
        smallToolModel.setOnLoadOfficeNotifyListener(new SmallToolModel.OnLoadOfficeNotifyListener() {
            @Override
            public void onFailed() {
                loading.setVisibility(GONE);
            }

            @Override
            public void onSucess(List<OfficeNotifyBean> notifys) {
                //读出结果
                officeNotifyListView.setAdapter(new OfficeNotifyAdapter(context, notifys));
                officeNotifyListView.setLayoutManager(new LinearLayoutManager(context));
                loading.setVisibility(GONE);
                layout.setVisibility(View.VISIBLE);
            }
        });
        smallToolModel.loadOfficeNotify();
        setContentView(view);
    }
}
