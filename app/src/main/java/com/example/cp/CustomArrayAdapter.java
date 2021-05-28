package com.example.cp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CustomArrayAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 0, VIEW_TYPE_LOAD = 1;
    loadMore load_more;
    boolean isLoading;
    int visible = 1;
    int lastVisible, totalCount;

    private List<ListItem> listItem;
    private List<ListItem> listItemCopy;
    private OnClickListener mOnClickListener;

    public CustomArrayAdapter(List<ListItem> items, @NotNull RecyclerView recyclerView) {
        listItem = items;
        listItemCopy = listItem;

        LinearLayoutManager linearLayoutManager
                = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull @NotNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                assert linearLayoutManager != null;
                totalCount = linearLayoutManager.getItemCount();
                lastVisible = linearLayoutManager.findLastVisibleItemPosition();
                if (!isLoading && totalCount <= (lastVisible + visible)) {
                    if (load_more != null) {
                        load_more.onLoadMore();
                        isLoading = true;
                    }
                }
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return listItem.get(position) == null ? VIEW_TYPE_LOAD : VIEW_TYPE_ITEM;
    }

    public void setLoad_more(loadMore load_more) {
        this.load_more = load_more;
    }

    public void filter(@NotNull String text) {
        listItem = new ArrayList<>();
        if (text.isEmpty()) {
            listItem = listItemCopy;
        } else {
            text = text.toLowerCase();
            for (ListItem item : listItemCopy) {
                if (item.Name.toLowerCase().contains(text) || item.CatNum.toLowerCase().contains(text)
                        || item.Brand.toLowerCase().contains(text) || item.Group.toLowerCase().contains(text)
                        || item.Available.toLowerCase().contains(text) || String.valueOf(item.Price).toLowerCase().contains(text)) {
                    listItem.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    public interface OnClickListener {
        void onItemClick(int position);
    }

    public void setOnClickListener(OnClickListener listener) {
        mOnClickListener = listener;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.item_list, parent, false);
            return new ItemViewHolder(view, mOnClickListener);
        } else if (viewType == VIEW_TYPE_LOAD) {
            View view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.item_loading, parent, false);
            return new ItemViewHolder(view, mOnClickListener);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NotNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            ListItem listItemMain = listItem.get(position);
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            itemViewHolder.sGroup_TV.setText(listItemMain.getGroup());
            itemViewHolder.sBrand_TV.setText(listItemMain.getBrand());
            itemViewHolder.sCat_num_TV.setText(listItemMain.getCatNum());
            itemViewHolder.sName_TV.setText(listItemMain.getName());
            itemViewHolder.sPrice_TV.setText(String.valueOf(listItemMain.getPrice()));
            itemViewHolder.sAvailable_TV.setText(listItemMain.getAvailable());
        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    public void setLoaded() {
        isLoading = false;
    }

    @Override
    public int getItemCount() {
        return listItem.size();
    }

    public static class LoadingViewHolder extends RecyclerView.ViewHolder {

        public ProgressBar progressBar;

        public LoadingViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView sGroup_TV;
        TextView sBrand_TV;
        TextView sCat_num_TV;
        TextView sName_TV;
        TextView sPrice_TV;
        TextView sAvailable_TV;

        public ItemViewHolder(View view, final OnClickListener listener) {
            super(view);

            sGroup_TV = view.findViewById(R.id.group_TV);
            sBrand_TV = view.findViewById(R.id.brand_TV);
            sCat_num_TV = view.findViewById(R.id.cat_num_TV);
            sName_TV = view.findViewById(R.id.nameTV);
            sPrice_TV = view.findViewById(R.id.Price_TV);
            sAvailable_TV = view.findViewById(R.id.available_TV);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}