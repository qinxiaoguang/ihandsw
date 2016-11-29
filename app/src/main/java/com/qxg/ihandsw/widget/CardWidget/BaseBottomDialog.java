package com.qxg.ihandsw.widget.CardWidget;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;

import com.qxg.ihandsw.model.BookModel;
import com.qxg.ihandsw.model.CardModel;
import com.qxg.ihandsw.model.LibraryModel;
import com.qxg.ihandsw.model.SmallToolModel;
import com.qxg.ihandsw.model.UserModel;
import com.roger.catloadinglibrary.CatLoadingView;

/**
 * Created by Qking on 2016/11/6.
 */

public class BaseBottomDialog extends BottomSheetDialog {

    public CatLoadingView loadingView;
    public Context context;
    public Handler handler;
    public CardModel cardModel;
    public UserModel userModel;
    public LibraryModel libraryModel;
    public BookModel bookModel;
    public SmallToolModel smallToolModel;
    public BaseBottomDialog(Context context) {
        super(context);
        this.context = context;
        loadingView = new CatLoadingView();
        loadingView.setCancelable(false);
        handler = new Handler(context.getMainLooper());
        cardModel = new CardModel((Activity) context);
        userModel = new UserModel((Activity)context);
        libraryModel = new LibraryModel((Activity)context);
        bookModel = new BookModel((Activity)context);
        smallToolModel = new SmallToolModel((Activity)context);
    }

    public void initView(){

    }

    public void showLoading() {
        loadingView.show(((AppCompatActivity) context).getSupportFragmentManager(), "");
    }

    public void hideLoading() {
        loadingView.dismiss();
    }

}
