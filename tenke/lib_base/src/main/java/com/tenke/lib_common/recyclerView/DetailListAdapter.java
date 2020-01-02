package com.tenke.lib_common.recyclerView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.tenke.baselibrary.databinding.DetailedListItemBinding;

public abstract class DetailListAdapter extends RecyclerView.Adapter<DetailedListItemViewHolder> {

    @NonNull
    @Override
    public DetailedListItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        DetailedListItemBinding dataBinding = DetailedListItemBinding.inflate(inflater, viewGroup, false);
        DetailedListItemViewHolder viewHolder = new DetailedListItemViewHolder(dataBinding);
        return viewHolder;
    }

}
