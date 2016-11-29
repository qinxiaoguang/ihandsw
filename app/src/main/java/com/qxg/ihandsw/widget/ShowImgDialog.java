package com.qxg.ihandsw.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.qxg.ihandsw.R;
import com.qxg.ihandsw.model.LibraryModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Response;

/**
 * Created by Qking on 2016/11/7.
 */

public class ShowImgDialog extends Dialog {
    Context context;
    private Handler handler;

    LibraryModel libraryModel;
    @BindView(R.id.imgview)
    SubsamplingScaleImageView imgView;
    @BindView(R.id.loading)
    ProgressBar loading;


    public ShowImgDialog(Context context) {
        super(context,R.style.FullScreenDialog);
        this.context = context;
        handler = new Handler(context.getMainLooper());
        libraryModel = new LibraryModel((Activity) context);
    }

    public ShowImgDialog initPhoneTableView(int resourceId){
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_show_img,null);
        ButterKnife.bind(this,view);
        loading.setVisibility(View.GONE);
        imgView.setImage(ImageSource.bitmap(BitmapFactory.decodeResource(context.getResources(),resourceId)));
        imgView.setVisibility(View.VISIBLE);

        setContentView(view);
        return this;
    }

    public ShowImgDialog initView(){

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_show_img,null);
        ButterKnife.bind(this,view);

        //获取座位信息
        libraryModel.setOnGetSeatInfoListener(new LibraryModel.OnGetSeatInfoListener() {
            @Override
            public void onSucess(Response res) {
                //返回的是Bitmap
                final Bitmap bitmap = BitmapFactory.decodeStream(res.body().byteStream());
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                int resWidth = width - 120;
                int resHeight = height - 80;
                final Bitmap result = Bitmap.createBitmap(bitmap, 30, 60, resWidth, resHeight);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        loading.setVisibility(View.GONE);
                        imgView.setImage(ImageSource.bitmap(result));
                        imgView.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
        libraryModel.getSeatInfo();
        setCancelable(true);
        setContentView(view);
        return this;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
