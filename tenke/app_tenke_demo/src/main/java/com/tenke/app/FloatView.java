package com.tenke.app;

import android.content.Context;
import androidx.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;

import com.tenke.lib_common.ApplicationContextLink;

public class FloatView {
    private LayoutInflater mInflater;

    public View inflate(@LayoutRes int resource){
        return mInflater.inflate(resource,null);
    }

    private FloatView(){
        mInflater = (LayoutInflater) ApplicationContextLink.LinkToApplication().
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public static FloatView getInstance(){
        return Holder.INSTANCE;
    }

    private static final class Holder{
        private static final FloatView INSTANCE = new FloatView();
    }

}
