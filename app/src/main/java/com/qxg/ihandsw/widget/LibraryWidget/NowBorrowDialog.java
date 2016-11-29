package com.qxg.ihandsw.widget.LibraryWidget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.qxg.ihandsw.R;
import com.qxg.ihandsw.adapter.NowBorrowAdapter;
import com.qxg.ihandsw.model.BookModel;
import com.qxg.ihandsw.model.bean.NowBorrow;
import com.qxg.ihandsw.widget.CardWidget.BaseBottomDialog;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.View.GONE;

/**
 * Created by Qking on 2016/11/9.
 */
@SuppressLint("ValidFragment")
public class NowBorrowDialog extends BaseBottomDialog {
    @BindView(R.id.loading_now_borrow)
    ProgressBar loadingNowBorrow;
    @BindView(R.id.now_borrow_list_view)
    RecyclerView nowBorrowListView;
    @BindView(R.id.notice)
    TextView notice;
    @BindView(R.id.check_name_tv)
    TextView checkNameTv;
    @BindView(R.id.stepToCheck)
    Button stepToCheck;
    @BindView(R.id.check_layout)
    LinearLayout checkLayout;

    public NowBorrowDialog(Context context) {
        super(context);
    }

    @Override
    public void initView() {
        super.initView();
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_now_borrow, null);
        ButterKnife.bind(this, view);

        bookModel.setOnLoadNowBorrow(new BookModel.OnLoadNowBorrow() {
            @Override
            public void onSucess(final List<NowBorrow> borrows) {
                //成功后，进行显示
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        loadingNowBorrow.setVisibility(GONE);
                        if (borrows.size() == 0) {
                            notice.setVisibility(View.VISIBLE);
                        } else {
                            notice.setVisibility(GONE);
                            nowBorrowListView.setAdapter(new NowBorrowAdapter((Activity) context, borrows));
                            nowBorrowListView.setLayoutManager(new LinearLayoutManager(context));
                        }
                    }
                });

            }

            @Override
            public void checkName() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        loadingNowBorrow.setVisibility(GONE);
                        notice.setVisibility(GONE);
                        nowBorrowListView.setVisibility(GONE);
                        checkLayout.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
        bookModel.loadNowBorrow();

        setContentView(view);
    }

    @OnClick(R.id.stepToCheck)
    public void onClick() {
        //跳转网页核实
        new AlertDialog.Builder(context)
                .setMessage("将要跳转网页进行登录，是否继续?")
                .setPositiveButton("是", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Uri uri = Uri.parse("http://202.194.40.71:8080/reader/login.php");
                        Intent it = new Intent(Intent.ACTION_VIEW, uri);
                        context.startActivity(it);
                    }
                })
                .setNegativeButton("否",null)
                .show();
    }
}
