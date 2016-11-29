package com.qxg.ihandsw.injector.component;

import com.qxg.ihandsw.injector.module.FragmentModule;
import com.qxg.ihandsw.injector.scope.PerFragment;
import com.qxg.ihandsw.view.Home.Fragments.forum.Impl.ForumFragment;
import com.qxg.ihandsw.view.Home.Fragments.home.Impl.HomeFragment;
import com.qxg.ihandsw.view.Home.Fragments.me.Impl.MeFragment;

import dagger.Component;

/**
 * Created by Qking on 2016/11/1.
 */

@PerFragment
@Component(dependencies = ApplicationComponent.class,modules = FragmentModule.class)
public interface FragmentComponent {
    void inject(HomeFragment fragment);
    void inject(ForumFragment fragment);
    void inject(MeFragment fragment);
}
