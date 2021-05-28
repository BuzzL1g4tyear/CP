package com.example.cp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BaskedAdapter extends RecyclerView.Adapter<BaskedAdapter.ViewHolder> {

    private List<ListItem> listItem;
    private List<ListItem> listItemCopy;
    private OnClickListener mOnClickListener;

    public BaskedAdapter(List<ListItem> list) {
        listItem = list;
        listItemCopy = listItem;
    }

    public void filter(@NotNull String text) {
        listItem = new ArrayList<>();
        if (text.isEmpty()) {
            listItem = listItemCopy;
        } else {
            text = text.toLowerCase();
            for (ListItem item : listItemCopy) {
                if (item.CatNum.toLowerCase().contains(text) || item.Brand.toLowerCase().contains(text)
                        || String.valueOf(item.Quantity).toLowerCase().contains(text) || item.Name.toLowerCase().contains(text)
                        || String.valueOf(item.Price).toLowerCase().contains(text) || item.Group.toLowerCase().contains(text)) {
                    listItem.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.basket_items, parent, false);
        return new ViewHolder(view,mOnClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ListItem listItemMain = listItem.get(position);
        holder.bGroup_TV.setText(listItemMain.getGroup());
        holder.bQuantity_TV.setText(String.valueOf(listItemMain.getQuantity()));
        holder.bCatNum_TV.setText(listItemMain.getCatNum());
        holder.bNameItem_TV.setText(listItemMain.getName());
        holder.bPrice_TV.setText(String.valueOf(listItemMain.getPrice()));
        holder.bBrand_TV.setText(String.valueOf(listItemMain.getBrand()));
    }

    public interface OnClickListener {
        void onItemClick(int position);
    }

    public void setOnClickListener(OnClickListener listener) {
        mOnClickListener = listener;
    }

    @Override
    public int getItemCount() {
        return listItem.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView bGroup_TV;
        TextView bCatNum_TV;
        TextView bNameItem_TV;
        TextView bQuantity_TV;
        TextView bBrand_TV;
        TextView bPrice_TV;

        public ViewHolder(View view,final  OnClickListener listener) {
            super(view);

            bGroup_TV = view.findViewById(R.id.bGroup_TV);
            bQuantity_TV = view.findViewById(R.id.bQuantity_TV);
            bCatNum_TV = view.findViewById(R.id.bCatNum_TV);
            bNameItem_TV = view.findViewById(R.id.bNameItem_TV);
            bPrice_TV = view.findViewById(R.id.bPrice_TV);
            bBrand_TV = view.findViewById(R.id.bBrand_TV);

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


