package com.saint.app;

import android.os.Bundle;
import com.saint.app.ui.home.HomeFragment;
import com.tenke.lib_common.mvvm.BaseActivity;


public class MainActivity extends BaseActivity {

    private HomeFragment mHomePage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);
        mHomePage = findOrCreatFragment(R.id.container,HomeFragment.class);
        show(mHomePage);
    }

}
