package com.qxg.ihandsw.view.Library;

import com.qxg.ihandsw.model.bean.SeatInfo;
import com.qxg.ihandsw.view.IView;

import java.util.List;

/**
 * Created by Qking on 2016/11/7.
 */

public interface ILibraryView extends IView {
    public void showLogin(int flag);
    public void showBookLogin(int flag); //图书馆图书登录
    public void showOrderDialog();

    public void showLoading();
    public void hideLoading();

    public void showChangePwdDialog();
    public void showNowBorrowDialog();
    public void showBookChangePwdDialog();

    public void showSeatInfo();
    public void showSeatInfo(List<SeatInfo> seatInfos);
    public void showReleaseDialog();
}
