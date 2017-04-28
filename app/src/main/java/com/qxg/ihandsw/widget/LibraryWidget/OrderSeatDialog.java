package com.qxg.ihandsw.widget.LibraryWidget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.qxg.ihandsw.R;
import com.qxg.ihandsw.adapter.FloorInfoAdapter;
import com.qxg.ihandsw.model.LibraryModel;
import com.qxg.ihandsw.model.bean.OrderFloorInfo;
import com.qxg.ihandsw.widget.CardWidget.BaseBottomDialog;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.View.GONE;

/**
 * Created by Qking on 2016/11/7.
 */
@SuppressLint("ValidFragment")
public class OrderSeatDialog extends BaseBottomDialog {
    @BindView(R.id.floor)
    TextView floor;
    @BindView(R.id.seat)
    TextView seat;
    @BindView(R.id.cancel)
    Button cancel;
    @BindView(R.id.loading)
    ProgressBar loading;
    @BindView(R.id.cancelLayout)
    View cancelLayout;
    @BindView(R.id.orderLayout)
    View orderLayout;
    @BindView(R.id.floor_list_view)
    RecyclerView floorListView;


    public OrderSeatDialog(Context context) {
        super(context);
    }


    @Override
    public void initView() {
        super.initView();
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_order_seat, null);
        ButterKnife.bind(this, view);
        libraryModel.setOnGetOrderRecord(new LibraryModel.OnGetOrderRecordListener() {
            @Override
            public void onGetSucess(final String floorNum, final String seatNum) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        floor.setText("楼层:" + floorNum);
                        seat.setText("座位：" + seatNum);
                        loading.setVisibility(GONE);
                        cancelLayout.setVisibility(View.VISIBLE);
                        orderLayout.setVisibility(GONE);
                    }
                });
            }

            @Override
            public void onGetFailed(String msg) {
                //
                Toast.makeText(context, "出错了。。请稍后重试~", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void noRecord() {
                //开始显示座位信息

                libraryModel.setOnGetFloorInfoListener(new LibraryModel.OnGetFloorInfoListener() {
                    @Override
                    public void onSucess(final List<OrderFloorInfo> floorInfos) {
                        //加载成功，设置上去
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                floorListView.setAdapter(new FloorInfoAdapter((Activity) context, floorInfos,OrderSeatDialog.this));
                                floorListView.setLayoutManager(new LinearLayoutManager(context));
                                loading.setVisibility(GONE);
                                cancelLayout.setVisibility(View.GONE);
                                orderLayout.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                });
                libraryModel.getFloorInfo();
            }
        });
        libraryModel.getOrderRecord();//查找预约记录

        setCanceledOnTouchOutside(false);
        setContentView(view);
    }


    @OnClick({R.id.cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancel:
                new AlertDialog.Builder(context)
                        .setTitle("请注意")
                        .setMessage("将要跳转网页，确定取消预约？")
                        .setPositiveButton("是", new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(context,ShowWebViewActivity.class);
                                intent.putExtra("flag",ShowWebViewActivity.LOAD_UN_ORDER);
                                context.startActivity(intent);
                                dismiss();
                            }
                        })
                        .setNegativeButton("否", null)
                        .show();
                break;
        }
    }
}
