//package com.tenke.app;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.DividerItemDecoration;
//import androidx.recyclerview.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TextView;
//
//import com.tenke.lib_common.ApplicationContextLink;
//import com.tenke.lib_common.DataManager.DataManager;
//import com.tenke.lib_common.recyclerView.DetailedListItemViewHolder;
//import com.tenke.lib_common.recyclerView.DetailedListItemViewModel;
//import com.tenke.lib_common.recyclerView.OnItemClickedListener;
//import com.tenke.baselibrary.databinding.DetailedListItemBinding;
//import com.yhao.floatwindow.FloatWindow;
//import com.yhao.floatwindow.IFloatWindow;
//
//import java.util.List;
//
//public class FloatWindowManager {
//    private static final String CORR_TAG = "CORR";
//    private static final String HISTORY_TAG = "history";
//
//    private FloatWindow.B fb = FloatWindow.with(ApplicationContextLink.LinkToApplication());
//
//    private FloatWindowManager(){
//
//    }
//
//    public void showCorrWindows(final String origin, final String result){
//        IFloatWindow iFloatWindow = FloatWindow.get(CORR_TAG);
//        if(iFloatWindow!=null){
//            if(!iFloatWindow.isShowing()){
//                iFloatWindow.show();
//            }
//        }else {
//            fb.setView(R.layout.floatwindow_corr)
//                    .setTag(CORR_TAG)
//                    .build();
//            iFloatWindow = FloatWindow.get(CORR_TAG);
//        }
//        final TextView originTV = iFloatWindow.getView().findViewById(R.id.origin);
//        originTV.setText(origin);
//        final EditText resultTV = iFloatWindow.getView().findViewById(R.id.result);
//        resultTV.setText(result);
//        Button button = iFloatWindow.getView().findViewById(R.id.confirm);
//
//        final IFloatWindow finalIFloatWindow = iFloatWindow;
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AsrMap.getInstance().addPairs(originTV.getText().toString(),resultTV.getText().toString());
//                finalIFloatWindow.hide();
//            }
//        });
//
//        Button button2 = iFloatWindow.getView().findViewById(R.id.cancel);
//        button2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finalIFloatWindow.hide();
//            }
//        });
//    }
//
//    public void showHistory(){
//        IFloatWindow iFloatWindow = FloatWindow.get(HISTORY_TAG);
//        if(iFloatWindow!=null){
//            if(!iFloatWindow.isShowing()){
//                iFloatWindow.show();
//            }
//        }else {
//
//            fb.setView(R.layout.floatwindow_history)
//                    .setTag(HISTORY_TAG)
//                    .build();
//            iFloatWindow = FloatWindow.get(HISTORY_TAG);
//        }
//        RecyclerView recyclerView = iFloatWindow.getView().findViewById(R.id.recyclerview);
//        HistoryAdapter adapter = new HistoryAdapter();
//        recyclerView.setAdapter(adapter);
//        adapter.setData((List<String>) DataManager.getInstance().get(HISTORY_TAG));
//        recyclerView.addItemDecoration(new DividerItemDecoration(ApplicationContextLink.LinkToApplication(), DividerItemDecoration.VERTICAL));
//    }
//
//    public static FloatWindowManager getInstance(){
//        return Holder.INSTANCE;
//    }
//
//    private static final class Holder{
//        private static final FloatWindowManager INSTANCE = new FloatWindowManager();
//    }
//
//    private class HistoryAdapter extends RecyclerView.Adapter<DetailedListItemViewHolder> implements OnItemClickedListener<Integer>{
//        private List<String> mData;
//
//        public void setData(List<String> data){
//            this.mData = data;
//            notifyDataSetChanged();
//        }
//
//        @NonNull
//        @Override
//        public DetailedListItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
//            LayoutInflater inflater = LayoutInflater.from(ApplicationContextLink.LinkToApplication());
//            DetailedListItemBinding dataBinding = DetailedListItemBinding.inflate(inflater, viewGroup, false);
//            DetailedListItemViewHolder viewHolder = new DetailedListItemViewHolder(dataBinding);
//            return viewHolder;
//        }
//
//        @Override
//        public void onBindViewHolder(@NonNull DetailedListItemViewHolder detailedListItemViewHolder, int i) {
//            final String command= mData.get(i);
//            DetailedListItemViewModel<Integer> viewModel = new DetailedListItemViewModel<>(i, this);
//            viewModel.name.set(command);
//            detailedListItemViewHolder.mDetailedListItemBinding.setViewmodel(viewModel);
//
//        }
//
//        @Override
//        public int getItemCount() {
//            return mData == null ? 0 : mData.size();
//        }
//
//        @Override
//        public void onItemClicked(Integer item) {
//
//        }
//    }
//
////    @BindingAdapter("app:history")
////    public static void setItems(RecyclerView recyclerView, List<String> items) {
////        HistoryAdapter adapter = (HistoryAdapter) recyclerView.getAdapter();
////        if (adapter != null) {
////            adapter.setData(items);
////        }
////    }
//}
