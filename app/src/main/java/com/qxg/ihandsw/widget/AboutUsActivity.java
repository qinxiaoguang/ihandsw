package com.qxg.ihandsw.widget;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.transition.Explode;
import android.transition.Fade;
import android.view.MenuItem;
import android.view.Window;

import com.qxg.ihandsw.R;
import com.qxg.ihandsw.utils.BuildUtil;

public class AboutUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(BuildUtil.isLargeThanAPI21()){
            Fade fade = new Fade();
            Explode explode = new Explode();
            fade.setDuration(100);
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            getWindow().setEnterTransition(fade);
            getWindow().setExitTransition(fade);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        getSupportActionBar().setTitle("关于我们");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
