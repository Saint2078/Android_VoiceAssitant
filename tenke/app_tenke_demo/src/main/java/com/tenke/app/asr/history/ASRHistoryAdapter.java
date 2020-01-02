//package com.tenke.app.asr.history;
//
//import androidx.annotation.NonNull;
//
//import com.tenke.lib_common.recyclerView.DetailListAdapter;
//import com.tenke.lib_common.recyclerView.DetailedListItemViewHolder;
//import com.tenke.lib_common.recyclerView.DetailedListItemViewModel;
//import com.tenke.lib_common.recyclerView.OnItemClickedListener;
//
//import java.util.ArrayList;
//
//public class ASRHistoryAdapter extends DetailListAdapter implements OnItemClickedListener<Integer> {
//    private ArrayList<String> mData;
//
//
//    void setData(@NonNull ArrayList<String> data){
//        mData =  data;
//        notifyDataSetChanged();
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull DetailedListItemViewHolder detailedListItemViewHolder, int i) {
//        final String value = mData.get(i);
//        DetailedListItemViewModel<Integer> viewModel = new DetailedListItemViewModel<>(i,this);
//        viewModel.name.set(value);
//        detailedListItemViewHolder.mDetailedListItemBinding.setViewmodel(viewModel);
//    }
//
//    @Override
//    public int getItemCount() {
//        return mData == null ? 0 : mData.size();
//    }
//
//    @Override
//    public void onItemClicked(Integer item) {
//
//    }
//}
