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

public class CustomArrayAdapter extends RecyclerView.Adapter<CustomArrayAdapter.ViewHolder> {

    private List<ListItem> listItem;
    private List<ListItem> listItemCopy;
    private OnClickListener mOnClickListener;

    public CustomArrayAdapter(List<ListItem> items) {
        listItem = items;
        listItemCopy = listItem;
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
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        return new ViewHolder(view, mOnClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ListItem listItemMain = listItem.get(position);
        holder.sGroup_TV.setText(listItemMain.getGroup());
        holder.sBrand_TV.setText(listItemMain.getBrand());
        holder.sCat_num_TV.setText(listItemMain.getCatNum());
        holder.sName_TV.setText(listItemMain.getName());
        holder.sPrice_TV.setText(String.valueOf(listItemMain.getPrice()));
        holder.sAvailable_TV.setText(listItemMain.getAvailable());
    }

    @Override
    public int getItemCount() {
        return listItem.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView sGroup_TV;
        TextView sBrand_TV;
        TextView sCat_num_TV;
        TextView sName_TV;
        TextView sPrice_TV;
        TextView sAvailable_TV;

        public ViewHolder(View view, final OnClickListener listener) {
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