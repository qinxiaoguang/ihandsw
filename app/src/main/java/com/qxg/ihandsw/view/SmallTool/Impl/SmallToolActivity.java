package com.qxg.ihandsw.view.SmallTool.Impl;


import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.qxg.ihandsw.R;
import com.qxg.ihandsw.presenter.BasePresenter;
import com.qxg.ihandsw.presenter.SmallTool.Impl.SmallToolPresenter;
import com.qxg.ihandsw.view.BaseActivity;
import com.qxg.ihandsw.view.SmallTool.ISmallToolView;
import com.qxg.ihandsw.widget.CardWidget.BaseBottomDialog;
import com.qxg.ihandsw.widget.ShowImgDialog;
import com.qxg.ihandsw.widget.SmallToolWidget.OfficeNotifyDialog;
import com.qxg.ihandsw.widget.SmallToolWidget.SchoolDateDialog;
import com.qxg.ihandsw.widget.SmallToolWidget.SchoolHireDialog;
import com.qxg.ihandsw.widget.SmallToolWidget.ShowWorkArrangeDialog;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SmallToolActivity extends BaseActivity implements ISmallToolView {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @Inject
    SmallToolPresenter presenter;
    @BindView(R.id.jiaowu)
    LinearLayout jiaowu;
    @BindView(R.id.school_date)
    LinearLayout schoolDate;
    @BindView(R.id.work_arrange)
    LinearLayout workArrange;
    @BindView(R.id.phone_table)
    LinearLayout phoneTable;
    @BindView(R.id.time_table)
    LinearLayout timeTable;
    @BindView(R.id.master_email)
    LinearLayout masterEmail;
    @BindView(R.id.hire_info)
    LinearLayout hireInfo;
    @BindView(R.id.activity_card)
    LinearLayout activityCard;

    @Override
    public int setContentView() {
        return R.layout.activity_small_tool;
    }

    @Override
    protected void beforeSetView() {
        super.beforeSetView();
        setTransStatusBar(true);
    }

    @Override
    protected void custom() {
        super.custom();
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("工具汇");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void attachView() {
        presenter.attachView(this);
    }

    @Override
    public void initInjector() {
        mActivityComponent.inject(this);
        ButterKnife.bind(this);
    }

    @Override
    public BasePresenter getPresenter() {
        return presenter;
    }


    @OnClick({R.id.jiaowu, R.id.school_date, R.id.work_arrange, R.id.phone_table, R.id.time_table, R.id.master_email, R.id.hire_info})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.jiaowu:
                //教务处工作通知一览表
                showBottomDialog(new OfficeNotifyDialog(this));
                break;
            case R.id.school_date:
                //校历
                showBottomDialog(new SchoolDateDialog(this));
                break;
            case R.id.work_arrange:
                showBottomDialog(new ShowWorkArrangeDialog(this));
                //近期工作安排
                break;
            case R.id.phone_table:
                //办公电话号码表
                new ShowImgDialog(this).initPhoneTableView(R.drawable.true_phone_table).show();
                break;
            case R.id.time_table:
                new ShowImgDialog(this).initPhoneTableView(R.drawable.true_time_table).show();
                //学生事务综合服务时间表
                break;
            case R.id.master_email:
                //校长信箱
                new AlertDialog.Builder(this)
                        .setMessage("校长邮箱为:xz@wh.sdu.edu.cn\n是否要向校长发送邮件?")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent data=new Intent(Intent.ACTION_SENDTO);
                                data.setData(Uri.parse("mailto:xz@wh.sdu.edu.cn"));
                                startActivity(data);
                            }
                        })
                        .setNegativeButton("取消",null)
                        .show();
                break;
            case R.id.hire_info:
                //招聘信息最后一个！！
                showBottomDialog(new SchoolHireDialog(this));
                break;
        }
    }

    private void showBottomDialog(BaseBottomDialog dialog){
        dialog.setCanceledOnTouchOutside(false);
        dialog.initView();
        dialog.show();
    }
}
