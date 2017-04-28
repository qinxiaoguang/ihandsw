package com.qxg.ihandsw.widget.SmallToolWidget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.qxg.ihandsw.Config;
import com.qxg.ihandsw.R;
import com.qxg.ihandsw.model.SmallToolModel;
import com.qxg.ihandsw.utils.Log;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Qking on 2016/11/15.
 */

public class ShowSchoolDateImgDialog extends Dialog {
    Context context;
    String url;
    SmallToolModel model;
    @BindView(R.id.loading)
    ProgressBar loading;
    @BindView(R.id.imgview)
    SubsamplingScaleImageView imgview;

    public ShowSchoolDateImgDialog(Context context, String url) {
        super(context, R.style.FullScreenDialog);
        this.context = context;
        this.url = url;
        model = new SmallToolModel((Activity) context);
    }

    public ShowSchoolDateImgDialog initView() {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_show_img, null);
        ButterKnife.bind(this, view);

        //开始获取图片数据

        model.setOnLoadDateImgListener(new SmallToolModel.OnLoadDateImgListener() {
            @Override
            public void onFailed() {
                loading.setVisibility(View.GONE);
                Log.i("获取失败","--->");
            }

            @Override
            public void onSucess(Bitmap bitmap) {
                //获取成功后，就可以将图片显示出来了
                loading.setVisibility(View.GONE);
                imgview.setVisibility(View.VISIBLE);
                imgview.setImage(ImageSource.bitmap(bitmap));
            }
        });
        model.loadDateImg(Config.TERM_DATE_IMG_URL + url + ".jpg");
        setContentView(view);
        return this;
    }
}
