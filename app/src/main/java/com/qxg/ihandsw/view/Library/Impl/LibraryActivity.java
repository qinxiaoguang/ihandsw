package com.qxg.ihandsw.view.Library.Impl;

import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.qxg.ihandsw.R;
import com.qxg.ihandsw.model.bean.SeatInfo;
import com.qxg.ihandsw.presenter.BasePresenter;
import com.qxg.ihandsw.presenter.Library.Impl.LibraryPresenter;
import com.qxg.ihandsw.view.BaseActivity;
import com.qxg.ihandsw.view.Library.ILibraryView;
import com.qxg.ihandsw.widget.CardWidget.ChangePwdDialog;
import com.qxg.ihandsw.widget.LibraryWidget.BookLoginDialog;
import com.qxg.ihandsw.widget.LibraryWidget.LibraryLoginDialog;
import com.qxg.ihandsw.widget.LibraryWidget.NowBorrowDialog;
import com.qxg.ihandsw.widget.LibraryWidget.OrderSeatDialog;
import com.qxg.ihandsw.widget.LibraryWidget.ShowReleaseDialog;
import com.qxg.ihandsw.widget.LibraryWidget.ShowSearchWebViewDialog;
import com.qxg.ihandsw.widget.LibraryWidget.ShowSeatInfoDialog;
import com.qxg.ihandsw.widget.ShowImgDialog;
import com.roger.catloadinglibrary.CatLoadingView;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LibraryActivity extends BaseActivity implements ILibraryView {

    @Inject
    LibraryPresenter presenter;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.query_seat_info)
    LinearLayout querySeatInfo;
    @BindView(R.id.toolbar_layout)
    LinearLayout toolbarLayout;
    @BindView(R.id.order_seat)
    LinearLayout orderSeat;
    @BindView(R.id.search_book)
    LinearLayout searchBook;
    @BindView(R.id.now_borrow)
    LinearLayout nowBorrow;
    @BindView(R.id.change_pwd)
    LinearLayout changePwd;
    @BindView(R.id.release_seat)
    LinearLayout releaseSeat;


    private CatLoadingView loadingView;

    @Override
    public int setContentView() {
        return R.layout.activity_library;
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
        loadingView = new CatLoadingView();
        loadingView.setCancelable(false);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("图书馆");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //开始获取座位图片
    }

    @Override
    public void initInjector() {
        ButterKnife.bind(this);
        mActivityComponent.inject(this);
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
    public BasePresenter getPresenter() {
        return presenter;
    }

    @OnClick({R.id.order_seat, R.id.search_book,R.id.change_pwd, R.id.now_borrow, R.id.query_seat_info,R.id.book_change_pwd,R.id.release_seat})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.query_seat_info:
                //presenter.startSeeSeatInfo();
                showSeatInfo();  //改版，直接显示座位信息
                break;
            case R.id.order_seat:
                //预约座位
                //首先判断用户是否登录
                presenter.startOrderSeat();
                break;
            case R.id.search_book:
                //馆藏查询
                showSearchWebView();
                break;
            case R.id.now_borrow:
                //当前借阅
                presenter.startNowBorrow();
                break;
            case R.id.change_pwd:
                presenter.startChangePwd();
                break;
            case R.id.book_change_pwd:
                presenter.startBookChangePwd();
                break;
            case R.id.release_seat:
                presenter.startReleaseSeat();
                break;
        }
    }

    @Override
    public void showLogin(int flag) {
        //显示登录信息
        LibraryLoginDialog.newInstance(flag).show(getSupportFragmentManager(), "");
    }

    @Override
    public void showBookLogin(int flag) {
        BookLoginDialog.newInstance(flag).show(getSupportFragmentManager(),"");
    }

    private void showSearchWebView(){
        new ShowSearchWebViewDialog(this).initView().show();
    }

    @Override
    public void showOrderDialog() {
        //显示预约的提示框
        OrderSeatDialog dialog = new OrderSeatDialog(this);
        dialog.initView();
        dialog.show();
    }

    @Override
    public void showLoading() {
        loadingView.show(getSupportFragmentManager(), "");
    }

    @Override
    public void hideLoading() {
        loadingView.dismiss();
    }

    @Override
    public void showChangePwdDialog() {
        ChangePwdDialog dialog = new ChangePwdDialog(this,ChangePwdDialog.LIBRARY_CHANGE_PWD);
        dialog.setCanceledOnTouchOutside(false);
        dialog.initView();
        dialog.show();
    }

    @Override
    public void showNowBorrowDialog() {
        //开始显示borroDialog
        NowBorrowDialog dialog = new NowBorrowDialog(this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.initView();
        dialog.show();
    }

    @Override
    public void showBookChangePwdDialog() {
        ChangePwdDialog dialog = new ChangePwdDialog(this,ChangePwdDialog.BOOK_CHANGE_PWD);
        dialog.setCanceledOnTouchOutside(false);
        dialog.initView();
        dialog.show();
    }

    @Override
    public void showSeatInfo() {
        new ShowImgDialog(this).initView().show();
    }

    @Override
    public void showSeatInfo(List<SeatInfo> seatInfos) {
        //开始显示
        new ShowSeatInfoDialog(this,seatInfos).initView().show();
    }

    @Override
    public void showReleaseDialog() {
        ShowReleaseDialog dialog = new ShowReleaseDialog(this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.initView();
        dialog.show();
    }
}
