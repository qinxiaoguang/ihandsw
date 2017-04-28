package com.qxg.ihandsw.injector.component;

import com.qxg.ihandsw.injector.module.ActivityModule;
import com.qxg.ihandsw.injector.scope.PerActivity;
import com.qxg.ihandsw.utils.ToastUtil;
import com.qxg.ihandsw.view.Card.Impl.CardActivity;
import com.qxg.ihandsw.view.Home.Impl.HomeActivity;
import com.qxg.ihandsw.view.Library.Impl.LibraryActivity;
import com.qxg.ihandsw.view.SmallTool.Impl.SmallToolActivity;
import com.qxg.ihandsw.view.Splash.Impl.SplashActivity;

import dagger.Component;
import retrofit2.Retrofit;

/**
 * Created by Qking on 2016/10/10.
 */

@PerActivity
@Component(dependencies = ApplicationComponent.class,modules = ActivityModule.class)
public interface ActivityComponent {
    void inject(SplashActivity activity);
    void inject(HomeActivity activity);
    void inject(CardActivity activity);
    void inject(LibraryActivity activity);
    void inject(SmallToolActivity activity);
    ToastUtil getToastUtil();
    Retrofit getRetrofit();
}
