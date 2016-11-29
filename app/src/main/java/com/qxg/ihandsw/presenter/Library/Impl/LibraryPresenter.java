package com.qxg.ihandsw.presenter.Library.Impl;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import com.qxg.ihandsw.utils.Log;

import com.qxg.ihandsw.Config;
import com.qxg.ihandsw.model.BookModel;
import com.qxg.ihandsw.model.LibraryModel;
import com.qxg.ihandsw.model.bean.SeatInfo;
import com.qxg.ihandsw.presenter.BasePresenter;
import com.qxg.ihandsw.presenter.Library.ILibraryPresenter;
import com.qxg.ihandsw.view.IView;
import com.qxg.ihandsw.view.Library.ILibraryView;
import com.qxg.ihandsw.widget.LibraryWidget.BookLoginDialog;
import com.qxg.ihandsw.widget.LibraryWidget.LibraryLoginDialog;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by Qking on 2016/11/7.
 */

public class LibraryPresenter extends BasePresenter implements ILibraryPresenter {

    private ILibraryView libraryView;
    private Activity activity;
    private LibraryModel libraryModel;
    private BookModel bookModel;
    private Handler handler;

    @Inject
    public LibraryPresenter(Activity activity) {
        this.activity = activity;
        libraryModel = new LibraryModel(activity);
        bookModel = new BookModel(activity);
        handler = new Handler();
    }

    @Override
    public void attachView(IView iView) {
        libraryView = (ILibraryView) iView;
    }

    @Override
    public void startOrderSeat() {
        //首先判断用户是否已经登录
        SharedPreferences sp =
                activity.getSharedPreferences(Config.LIBRARY_INFO_FILE_NAME, Context.MODE_PRIVATE);
        String pwd = sp.getString(Config.LIBRARY_PWD,"");

        if(!pwd.isEmpty()){
            //开始之前，要先自动登录一遍，然后再开始提示框预约
            libraryView.showLoading();
            libraryModel.setOnLoginListener(new LibraryModel.OnLoginListener() {
                @Override
                public void onSucess(final boolean flag, String msg) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.i("返回的登录结果",flag+"");
                            if (flag){
                                libraryView.hideLoading();
                                //显示预约的提示框吧
                                libraryView.showOrderDialog();
                            }else{
                                libraryView.hideLoading();
                                //重新显示登录框
                                libraryView.showLogin(LibraryLoginDialog.ORDER_SEAT_ONCLICK);
                            }
                        }
                    });
                }
            });
            Log.i("--->保存的密码是",pwd);
            libraryModel.login(pwd);
            //就开始出提示框预约吧
        }else{
            //否则提示登录
            libraryView.showLogin(LibraryLoginDialog.ORDER_SEAT_ONCLICK);
        }
    }

    @Override
    public void startChangePwd() {
        SharedPreferences sp =
                activity.getSharedPreferences(Config.LIBRARY_INFO_FILE_NAME, Context.MODE_PRIVATE);
        String pwd = sp.getString(Config.LIBRARY_PWD, "");
        if(!pwd.isEmpty()){
            //开始之前，要先自动登录一遍
            libraryView.showLoading();
            libraryModel.setOnLoginListener(new LibraryModel.OnLoginListener() {
                @Override
                public void onSucess(final boolean flag, String msg) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.i("返回的登录结果",flag+"");
                            if (flag){
                                libraryView.hideLoading();
                                //显示更改信息的提示框
                                libraryView.showChangePwdDialog();
                            }else{
                                //重新显示登录框
                                libraryView.showLogin(LibraryLoginDialog.CHANGE_PWD_ONCLICK);
                            }
                        }
                    });
                }
            });
            Log.i("--->保存的密码是",pwd);
            libraryModel.login(pwd);
        }else{
            //否则提示登录
            libraryView.showLogin(LibraryLoginDialog.CHANGE_PWD_ONCLICK);
        }
    }


    @Override
    public void startNowBorrow() {
        //首先判断用户是否登录，此处使用的是bookSp
        SharedPreferences bookSp =
                activity.getSharedPreferences(Config.BOOK_INFO_FILE_NAME,Context.MODE_PRIVATE);

        String pwd = bookSp.getString(Config.BOOK_PWD,"");

        if(!pwd.isEmpty()){
            //已经登录那么就重新登录一次
            String check = bookSp.getString(Config.BOOK_LOGIN_CHECK,"");
            BookModel bookModel = new BookModel(activity);
            bookModel.setOnLoginListener(new BookModel.OnLoginListener() {
                @Override
                public void onSucess() {
                    //成功登录后，就展示当前的借阅吧
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            libraryView.hideLoading();
                            libraryView.showNowBorrowDialog();
                        }
                    });
                }

                @Override
                public void onFailed(String msg) {
                    //登录失败
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            libraryView.hideLoading();
                            libraryView.showBookLogin(BookLoginDialog.NOW_BORROW_PWD_ONCLICK);//重新登录
                        }
                    });
                }
            });
            libraryView.showLoading();
            bookModel.login(pwd,check);
        }else{
            libraryView.showBookLogin(BookLoginDialog.NOW_BORROW_PWD_ONCLICK);
        }

    }

    @Override
    public void startBookChangePwd() {
        SharedPreferences sp =
                activity.getSharedPreferences(Config.BOOK_INFO_FILE_NAME, Context.MODE_PRIVATE);
        String pwd = sp.getString(Config.BOOK_PWD, "");
        String check = sp.getString(Config.BOOK_LOGIN_CHECK,"");
        if(!pwd.isEmpty()){
            //开始之前，要先自动登录一遍
            libraryView.showLoading();

            bookModel.setOnLoginListener(new BookModel.OnLoginListener() {
                @Override
                public void onSucess() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            libraryView.hideLoading();
                            libraryView.showBookChangePwdDialog();
                        }
                    });
                }

                @Override
                public void onFailed(String msg) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            libraryView.hideLoading();
                            libraryView.showBookLogin(BookLoginDialog.CHANGE_PWD_ONCLICK);
                        }
                    });
                }
            });
            Log.i("--->保存的密码是",pwd);
            bookModel.login(pwd,check);
        }else{
            //否则提示登录
            libraryView.showBookLogin(BookLoginDialog.CHANGE_PWD_ONCLICK);
        }
    }

    @Override
    public void startSeeSeatInfo() {
        //查询座位情况，首先上官网，取得数据和COOKIE后再进行输入自己号登录，然后去座位情况中取得数据。
        libraryView.showLoading();
        LibraryModel libraryModel = new LibraryModel(activity);
        libraryModel.setOnSeeSeatInfo(new LibraryModel.OnSeeSeatInfo() {
            @Override
            public void onFailed() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("登录JUN失败","--->");
                        //显示本校的座位信息吧
                        libraryView.hideLoading();
                        libraryView.showSeatInfo();
                    }
                });
            }

            @Override
            public void onSucess(final List<SeatInfo> seatInfos) {
                //成功返回数据后，让view更新吧
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        libraryView.showSeatInfo(seatInfos);
                        libraryView.hideLoading();
                        //首先把小猫给弄消失了
                    }
                });
            }
        });
        libraryModel.seeSeatInfoOrRealease(LibraryModel.SEE_SEAT_INFO);
    }

    @Override
    public void startReleaseSeat() {
        //首先判断用户是否已经登录
        SharedPreferences sp =
                activity.getSharedPreferences(Config.LIBRARY_INFO_FILE_NAME, Context.MODE_PRIVATE);
        String pwd = sp.getString(Config.LIBRARY_PWD,"");

        if(!pwd.isEmpty()){
            //开始之前，要先自动登录一遍,然后再提示释放座位对话框
            libraryView.showLoading();
            LibraryModel libraryModel = new LibraryModel(activity);
            libraryModel.setOnLoginListener(new LibraryModel.OnLoginListener() {
                @Override
                public void onSucess(final boolean flag, String msg) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.i("返回的登录结果",flag+"");
                            if (flag){
                                //libraryView.hideLoading();
                                //如果登录成功，再接着使用JNU登录一遍
                                //如果登录成功，就显示ReleaseDialog的对话框
                                libraryView.hideLoading();
                                libraryView.showReleaseDialog();
                            }else{
                                libraryView.hideLoading();
                                //重新显示登录框
                                libraryView.showLogin(LibraryLoginDialog.RELEASE_SEAT_ONCLICK);
                            }
                        }
                    });
                }
            });
            Log.i("--->保存的密码是",pwd);
            libraryModel.login(pwd);
        }else{
            //否则提示登录
            libraryView.showLogin(LibraryLoginDialog.RELEASE_SEAT_ONCLICK);
        }
    }
}
