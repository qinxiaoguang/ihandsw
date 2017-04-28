package com.qxg.ihandsw.presenter.Library;

/**
 * Created by Qking on 2016/11/7.
 */

public interface ILibraryPresenter  {
    public void startOrderSeat();
    public void startChangePwd();
    public void startNowBorrow();
    public void startBookChangePwd();

    public void startSeeSeatInfo(); //查询座位情况
    public void startReleaseSeat(); //开始释放座位操作
}
