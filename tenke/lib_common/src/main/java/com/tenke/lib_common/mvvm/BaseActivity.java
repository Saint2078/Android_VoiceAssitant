package com.tenke.lib_common.mvvm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class BaseActivity extends AppCompatActivity {

    public  <T extends Fragment> T findOrCreatFragment(int layoutid,@NonNull Class<T> framentclass){
        T fragment = (T) getSupportFragmentManager().findFragmentByTag(framentclass.getName());
        try {
            if (fragment == null){
                fragment = framentclass.newInstance();
                getSupportFragmentManager().beginTransaction().add(layoutid,fragment,framentclass.getName()).commit();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return fragment;
    }

    public void show(BaseFragment fragment){
        getSupportFragmentManager().beginTransaction().show(fragment).commit();
    }

}
