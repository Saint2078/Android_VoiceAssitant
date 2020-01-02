package com.tenke.app.asr;

import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.tenke.app.R;
import com.tenke.app.asr.history.HistoryFragment;

public class AsrActivity extends AppCompatActivity {
    HistoryFragment mHistoryFragment ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asr);
        mHistoryFragment = (HistoryFragment) findOrCreateFragment(HistoryFragment.class);
        getSupportFragmentManager().beginTransaction().add(mHistoryFragment,mHistoryFragment.getClass().getName()).add(R.id.content,mHistoryFragment).commit();
    }

    private Fragment findOrCreateFragment(Class<? extends Fragment> fragmentClass){
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(fragmentClass.getName());
        if(fragment == null){
            try {
                fragment = fragmentClass.newInstance();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return fragment;
    }
}
