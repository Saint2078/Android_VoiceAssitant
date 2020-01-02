package com.tenke.lib_common.recyclerView;

import androidx.recyclerview.widget.RecyclerView;

import com.tenke.baselibrary.databinding.DetailedListItemBinding;

public class DetailedListItemViewHolder extends RecyclerView.ViewHolder {
    public DetailedListItemBinding mDetailedListItemBinding;

    public DetailedListItemViewHolder(DetailedListItemBinding mBinding) {
        super(mBinding.getRoot());
        mDetailedListItemBinding = mBinding;
    }

    public DetailedListItemViewHolder(DetailedListItemBinding mBinding, DetailedListItemViewModel itemViewModel) {
        super(mBinding.getRoot());
        mDetailedListItemBinding = mBinding;
        mDetailedListItemBinding.setViewmodel(itemViewModel);
    }
}
