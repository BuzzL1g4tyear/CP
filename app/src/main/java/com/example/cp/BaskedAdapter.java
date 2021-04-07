package com.example.cp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BaskedAdapter extends ArrayAdapter<ListItem> {
    private final LayoutInflater inflater;
    private List<ListItem> listItem = new ArrayList<>();
    private List<ListItem> listItemCopy = new ArrayList<>();

    public BaskedAdapter(@NonNull Context context, int resource, List<ListItem> list, LayoutInflater inflater) {
        super(context, resource, list);

        listItemCopy.addAll(list);

        this.inflater = inflater;
        this.listItem = list;
    }
// todo item.Data.toLowerCase().contains(text)
    public void filter(@NotNull String text) {
        listItem.clear();
        if (text.isEmpty()) {
            listItem.addAll(listItemCopy);
        } else {
            text = text.toLowerCase();
            for (ListItem item : listItemCopy) {
                if (item.CatNum.toLowerCase().contains(text) || item.Customer.toLowerCase().contains(text)
                        || String.valueOf(item.Quantity).toLowerCase().contains(text) || item.Name.toLowerCase().contains(text)
                        ||  String.valueOf(item.Price).toLowerCase().contains(text)) {
                    listItem.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        ListItem listItemMain = listItem.get(position);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.basket_items, null, false);
            viewHolder = new ViewHolder();
            viewHolder.bGroup_TV = convertView.findViewById(R.id.bGroup_TV);
            viewHolder.bQuantity_TV = convertView.findViewById(R.id.bQuantity_TV);
            viewHolder.bCatNum_TV = convertView.findViewById(R.id.bCatNum_TV);
            viewHolder.bNameItem_TV = convertView.findViewById(R.id.bNameItem_TV);
            viewHolder.bPrice_TV = convertView.findViewById(R.id.bPrice_TV);
            viewHolder.bBrand_TV = convertView.findViewById(R.id.bBrand_TV);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.bGroup_TV.setText(listItemMain.getGroup());
        viewHolder.bQuantity_TV.setText(String.valueOf(listItemMain.getQuantity()));
        viewHolder.bCatNum_TV.setText(listItemMain.getCatNum());
        viewHolder.bNameItem_TV.setText(listItemMain.getName());
        viewHolder.bPrice_TV.setText(String.valueOf(listItemMain.getPrice()));
        viewHolder.bBrand_TV.setText(String.valueOf(listItemMain.getBrand()));

        return convertView;
    }

    private static class ViewHolder {
        TextView bGroup_TV;
        TextView bCatNum_TV;
        TextView bNameItem_TV;
        TextView bQuantity_TV;
        TextView bBrand_TV;
        TextView bPrice_TV;
    }
}


