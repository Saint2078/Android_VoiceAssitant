package com.tenke.lib_common.recyclerView;

import androidx.databinding.ObservableField;
import android.graphics.drawable.Drawable;
import android.view.View;


public class DetailedListItemViewModel<T> {
    public final ObservableField<CharSequence> name = new ObservableField<>();
    public final ObservableField<CharSequence> detail = new ObservableField<>();
    public final ObservableField<CharSequence> info = new ObservableField<>();
    public final ObservableField<Drawable> image = new ObservableField<>();
    public final ObservableField<String> imageUrl = new ObservableField<>();
    public final ObservableField<Drawable> indicator = new ObservableField<>();
    public final ObservableField<CharSequence> index = new ObservableField<>();

    private OnItemClickedListener<T> mListener;
    private OnItemLongClickListener<T> mLongClickListener;
    private OnItemClickedListener<T> mOnMoreClickListener;
    protected T mItem;

    public DetailedListItemViewModel() {

    }

    public DetailedListItemViewModel(T item) {
        mItem = item;
    }

    public DetailedListItemViewModel(OnItemClickedListener<T> listener) {
        this.mListener = listener;
    }

    public DetailedListItemViewModel(T item, OnItemClickedListener<T> listener) {
        mItem = item;
        this.mListener = listener;
    }

    public void notifyItemData(T itemData) {
        mItem = itemData;
    }

    public void setListener(OnItemClickedListener<T> listener) {
        mListener = listener;
    }

    public void setLongClickListener(OnItemLongClickListener<T> listener) {
        mLongClickListener = listener;
    }

    public void onItemClicked() {
        if (mListener != null) {
            mListener.onItemClicked(mItem);
        }
    }

    public boolean onItemLongClick(View view) {
        if (mLongClickListener != null) {
            mLongClickListener.onItemLongClicked(mItem);
        }
        return true;
    }

    public void setOnMoreClickedListener(OnItemClickedListener<T> listener) {
        mOnMoreClickListener = listener;
    }

    public void onItemMoreIconClick() {
        if (mOnMoreClickListener != null) {
            mOnMoreClickListener.onItemClicked(mItem);
        }
    }
}
