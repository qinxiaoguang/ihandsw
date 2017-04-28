package com.qxg.ihandsw.view.Card;

import com.qxg.ihandsw.view.IView;

/**
 * Created by Qking on 2016/11/3.
 */

public interface ICardView extends IView{
    public void startLoading();
    public void reLogin();
    public void loadSucess(String name,String balance);
}
