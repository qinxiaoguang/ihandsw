package com.qxg.ihandsw.view.Card.Impl;

import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.qxg.ihandsw.R;
import com.qxg.ihandsw.presenter.BasePresenter;
import com.qxg.ihandsw.presenter.Card.Impl.CardPresenter;
import com.qxg.ihandsw.view.BaseActivity;
import com.qxg.ihandsw.view.Card.ICardView;
import com.qxg.ihandsw.widget.CardWidget.BaseBottomDialog;
import com.qxg.ihandsw.widget.CardWidget.CardFeeDialog;
import com.qxg.ihandsw.widget.CardWidget.ChangePwdDialog;
import com.qxg.ihandsw.widget.CardWidget.ChoseQueryFee;
import com.qxg.ihandsw.widget.CardWidget.ElectricFeeDialog;
import com.qxg.ihandsw.widget.CardWidget.LossCardInfoDialog;
import com.qxg.ihandsw.widget.CardWidget.LossOrUnlossDialog;
import com.qxg.ihandsw.widget.CardWidget.NetFeeDialog;
import com.qxg.ihandsw.widget.CardWidget.UpLossDialog;
import com.qxg.ihandsw.widget.ShowCheckDialog;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CardActivity extends BaseActivity implements ICardView {

    @Inject
    CardPresenter presenter;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.loading_info)
    ProgressBar loadingInfo;
    @BindView(R.id.first_name)
    TextView firstName;
    @BindView(R.id.name)
    TextView nameTV;
    @BindView(R.id.balance)
    TextView balanceTV;
    @BindView(R.id.info_layout)
    View infoLayout;

    @Override
    public int setContentView() {
        return R.layout.activity_card;
    }

    @Override
    protected void beforeSetView() {
        super.beforeSetView();
        setTransStatusBar(true);
    }

    @Override
    public void attachView() {
        presenter.attachView(this);
    }

    @Override
    protected void custom() {
        super.custom();
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("一卡通");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //每次重新加载的时候都要验证，然后才能执行后边的冲卡等等的操作
        presenter.getCardUserInfo();
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
    public void initInjector() {
        ButterKnife.bind(this);
        mActivityComponent.inject(this);
    }


    @Override
    public BasePresenter getPresenter() {
        return presenter;
    }


    @Override
    public void startLoading() {
        //开始加载用户信息
    }

    @Override
    public void reLogin() {
        //显示验证码，重新登录
        final ShowCheckDialog showCheckDialog = ShowCheckDialog.newInstance();
        showCheckDialog.setCancelable(false);
        showCheckDialog.setOnValidateCheckListener(new ShowCheckDialog.OnValidateCheckListener() {
            @Override
            public void onSucess() {
                //验证成功后，怎么办？
                //当然是把showCheckDialog消失掉了！
                showCheckDialog.dismiss();
                //接着就是重新加载校园卡信息
                presenter.getCardUserInfo();
            }
        });
        showCheckDialog.show(getSupportFragmentManager(), "");
    }

    @Override
    public void loadSucess(String name, String balance) {
        //加载成功，将progressbar消除
        loadingInfo.setVisibility(View.GONE);
        firstName.setText(name.substring(0, 1));
        nameTV.setText(name);
        balanceTV.setText("校园卡余额：" + balance);
        infoLayout.setVisibility(View.VISIBLE);
    }


    @OnClick({R.id.card_fee, R.id.net_fee, R.id.electric_fee, R.id.card_lost, R.id.card_register, R.id.loss_report, R.id.pwd_alter,R.id.fee_query})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.card_fee:
                //交卡费
                showBottomDialog(new CardFeeDialog(this));
                break;
            case R.id.net_fee:
                showBottomDialog(new NetFeeDialog(this));
                break;
            case R.id.electric_fee:
                showBottomDialog(new ElectricFeeDialog(this));
                break;
            case R.id.card_lost:
                showBottomDialog(new LossCardInfoDialog(this));
                break;
            case R.id.card_register:
                showBottomDialog(new UpLossDialog(this));
                break;
            case R.id.loss_report:
                showBottomDialog(new LossOrUnlossDialog(this));
                break;
            case R.id.pwd_alter:
                showBottomDialog(new ChangePwdDialog(this,ChangePwdDialog.CARD_CHANGE_PWD));
                break;
            case R.id.fee_query:
                //蹦出来选项，当日查询，3天查询，7天查询等选项。
                showBottomDialog(new ChoseQueryFee(this));
                break;
        }
    }

    private void showBottomDialog(BaseBottomDialog dialog){
        dialog.initView();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
}
