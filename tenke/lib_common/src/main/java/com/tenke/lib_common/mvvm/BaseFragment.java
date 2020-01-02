package com.tenke.lib_common.mvvm;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;


public abstract class BaseFragment<VM extends BaseViewModel> extends Fragment {

    protected ViewDataBinding mBinding;
    protected VM mViewModel;

    public BaseFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater,setLayoutId(),container,false);
        mViewModel = (VM) ViewModelProviders.of(this).get(setViewModel());
        mBinding.setVariable(setBR(),mViewModel);
        return mBinding.getRoot();
    }

    protected abstract Class<? extends BaseViewModel> setViewModel();

    protected abstract int setBR();

    protected abstract int setLayoutId();
}
