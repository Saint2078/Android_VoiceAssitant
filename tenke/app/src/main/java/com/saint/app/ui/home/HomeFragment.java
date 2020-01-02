package com.saint.app.ui.home;

import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.saint.app.BR;
import com.saint.app.R;
import com.tenke.lib_common.mvvm.BaseFragment;
import com.tenke.lib_common.mvvm.BaseViewModel;
import com.tenke.voice.VoiceResult;
import com.tenke.voice.asr.baidu.control.BaiduASRManager;
import com.tenke.voice.asr.common.ASRStatusController;

import io.reactivex.disposables.Disposable;

public class HomeFragment extends BaseFragment<HomeViewModel> {

    @Override
    protected Class<? extends BaseViewModel> setViewModel() {
        return HomeViewModel.class;
    }

    @Override
    protected int setBR() {
        return BR.viewModel;
    }

    @Override
    protected int setLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ASRStatusController.getInstance().mASRStateMutableLiveData.observe(this, mViewModel.mASRStateObserver);
        mBinding.getRoot().findViewById(R.id.web_background).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        mViewModel.startRecord();
                        break;
                    case MotionEvent.ACTION_UP:
                        mViewModel.stopRecord();
                        break;
                }
                return false;
            }
        });
    }
}
