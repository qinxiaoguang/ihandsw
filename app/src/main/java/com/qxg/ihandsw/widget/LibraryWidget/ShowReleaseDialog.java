package com.qxg.ihandsw.widget.LibraryWidget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.qxg.ihandsw.R;
import com.qxg.ihandsw.model.LibraryModel;
import com.qxg.ihandsw.widget.CardWidget.BaseBottomDialog;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Qking on 2016/11/14.
 */

public class ShowReleaseDialog extends BaseBottomDialog {
    @BindView(R.id.loading)
    ProgressBar loading;
    @BindView(R.id.notice)
    TextView notice;
    @BindView(R.id.release_seat_btn)
    Button releaseBtn;

    private  String viewState;
    private  String validation;
    private  String cookie;

    public ShowReleaseDialog(Context context) {
        super(context);
    }

    @Override
    public void initView() {
        super.initView();
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_release_seat, null);
        ButterKnife.bind(this, view);

        releaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(viewState==null || validation == null || cookie == null){
                    notice.setText("系统异常");
                }else{
                    //取消操作
                    showLoading();
                    libraryModel.setGoReleaseSeatListener(new LibraryModel.GoReleaseSeatListener() {
                        @Override
                        public void onFailed() {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    hideLoading();
                                    Toast.makeText(context,"请重新点击释放座位查看是否释放成功~",Toast.LENGTH_LONG).show();
                                    dismiss();
                                }
                            });
                        }

                        @Override
                        public void onSucess() {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    hideLoading();
                                    Toast.makeText(context,"释放成功，请重新点击释放座位确保成功~",Toast.LENGTH_SHORT).show();
                                    dismiss();
                                }
                            });
                        }
                    });
                    libraryModel.releaseSeat(viewState,validation,cookie);
                }
            }
        });
        //初始化

        libraryModel.setOnReleaseSeatListener(new LibraryModel.OnReleaseSeatListener() {
            @Override
            public void onFailed() {
                //读取失败
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        loading.setVisibility(View.GONE);
                        notice.setVisibility(View.VISIBLE);
                        notice.setText("系统异常，请到机器前释放，或稍后重试~");
                    }
                });
            }

            @Override
            public void onNoSeat() {
                //当前没有选座
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        loading.setVisibility(View.GONE);
                        notice.setVisibility(View.VISIBLE);
                        notice.setText("您当前没有座位~");
                    }
                });
            }

            @Override
            public void onSucess(final String nowState, String viewState, String validation, String cookie) {
                //当前有选座，给按钮添加点击功能，并将上述的信息保存下来
                ShowReleaseDialog.this.viewState = viewState;
                ShowReleaseDialog.this.validation = validation;
                ShowReleaseDialog.this.cookie = cookie;

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        loading.setVisibility(View.GONE);
                        notice.setVisibility(View.VISIBLE);
                        notice.setText(nowState);
                        releaseBtn.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
        libraryModel.seeSeatInfoOrRealease(LibraryModel.RELEASE);
        setContentView(view);
    }
}
