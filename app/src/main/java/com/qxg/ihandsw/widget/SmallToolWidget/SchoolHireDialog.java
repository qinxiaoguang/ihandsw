package com.qxg.ihandsw.widget.SmallToolWidget;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.qxg.ihandsw.R;
import com.qxg.ihandsw.adapter.HireAdapter;
import com.qxg.ihandsw.model.SmallToolModel;
import com.qxg.ihandsw.model.bean.HireInfo;
import com.qxg.ihandsw.utils.Log;
import com.qxg.ihandsw.widget.CardWidget.BaseBottomDialog;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Qking on 2016/11/15.
 */

public class SchoolHireDialog extends BaseBottomDialog {

    @BindView(R.id.loading)
    ProgressBar loading;
    @BindView(R.id.school_hire_list_view)
    RecyclerView schoolHireListView;
    @BindView(R.id.chose)
    Spinner chose;

    public final static int SCHOOL_HIRE = 0;
    public final static int ONLINE_HIRE = 1;

    public SchoolHireDialog(Context context) {
        super(context);
    }

    @Override
    public void initView() {
        super.initView();
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_school_hire, null);
        ButterKnife.bind(this, view);
        chose.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //i是位置
                Log.i("-->","当前位置");
                switch(i){
                    case 0:

                        loadHireInfo(SCHOOL_HIRE);
                        break;
                    case 1:
                        loadHireInfo(ONLINE_HIRE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        setContentView(view);
    }

    private void loadHireInfo(int flag){
        loading.setVisibility(View.VISIBLE);
        schoolHireListView.setVisibility(View.GONE);
        smallToolModel.setOnLoadSchoolHireInfoListener(new SmallToolModel.OnLoadSchoolHireInfoListener() {
            @Override
            public void onFailed() {
                loading.setVisibility(View.GONE);
                Toast.makeText(context, "加载失败，请稍后重试", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSucess(List<HireInfo> hires) {
                //招聘信息加载成功
                //接下来就是显示
                loading.setVisibility(View.GONE);
                schoolHireListView.setAdapter(new HireAdapter(context, hires));
                schoolHireListView.setLayoutManager(new LinearLayoutManager(context));
                schoolHireListView.setVisibility(View.VISIBLE);
            }
        });
        smallToolModel.loadSchoolHireInfo(flag);
    }
}
