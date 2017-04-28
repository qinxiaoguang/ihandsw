package com.qxg.ihandsw.presenter.Home.Fragments.home.Impl;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;

import com.flaviofaria.kenburnsview.KenBurnsView;
import com.flaviofaria.kenburnsview.Transition;
import com.qxg.ihandsw.R;
import com.qxg.ihandsw.adapter.AnimatorListenerAdapter;
import com.qxg.ihandsw.presenter.BasePresenter;
import com.qxg.ihandsw.presenter.Home.Fragments.home.IFragmentHomePresenter;
import com.qxg.ihandsw.view.Home.Fragments.home.IFHomeView;
import com.qxg.ihandsw.view.IView;

import javax.inject.Inject;

/**
 * Created by Qking on 2016/11/1.
 */

public class FragmentHomePresenter extends BasePresenter implements IFragmentHomePresenter {

    IFHomeView homeFragmentView;
    Activity activity;

    int imagesRes[] = {
            R.drawable.bp1,
            R.drawable.bp2,
            R.drawable.bp3,
            R.drawable.bp4,
            R.drawable.bp5,
            R.drawable.bp6,
            R.drawable.bp7,
            R.drawable.bp8,
            R.drawable.bp9,
            R.drawable.bp10,
            R.drawable.bp11,
            R.drawable.bp12,
            R.drawable.bp13,
            R.drawable.bp14,
            R.drawable.bp15,
            R.drawable.bp16,
            R.drawable.bp17,
            R.drawable.bp18,
            R.drawable.bp19,
            R.drawable.bp20,
            R.drawable.bp21,
            R.drawable.bp22,
            R.drawable.bp23
    };
    LruCache<Integer,Bitmap> bitmapCache;  //cache


    @Inject
    public FragmentHomePresenter(Activity activity){
        bitmapCache = new LruCache<>(8);
        this.activity = activity;
    }

    @Override
    public void attachView(IView iView) {
        homeFragmentView = (IFHomeView) iView;
    }


    //开始图片轮播动画
    @Override
    public void startImageAnim(final KenBurnsView images) {
        images.setTransitionListener(new KenBurnsView.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {}

            @Override
            public void onTransitionEnd(Transition transition) {
                ValueAnimator animator = ValueAnimator.ofFloat(1f,0f);
                animator.setDuration(1000);
                animator.addListener(new AnimatorListenerAdapter(){
                    @Override
                    public void onAnimationEnd(Animator animator) {
                        super.onAnimationEnd(animator);
                        //替换照片
                        images.pause();
                        int random = (int)(Math.random()*imagesRes.length);
                        Bitmap bitmap = bitmapCache.get(random);
                        if(bitmap == null){
                            bitmap = BitmapFactory.decodeResource(activity.getResources(),imagesRes[random]);
                            bitmapCache.put(random,bitmap);
                        }
                        images.setImageBitmap(bitmap);
                        /*images.setImageResource(imagesRes[random]);*/
                        images.resume();
                        ValueAnimator animator1 = ValueAnimator.ofFloat(0f,1f);
                        animator1.setDuration(500);
                        animator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                float now2 = (float) valueAnimator.getAnimatedValue();
                                images.setAlpha(now2);
                            }
                        });
                        animator1.start();
                    }
                });

                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        float now = (float) valueAnimator.getAnimatedValue();
                        images.setAlpha(now);
                    }
                });

                animator.start();
            }
        });
    }
}
