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
            viewHolder.customer_TV = convertView.findViewById(R.id.customer_TV);
            viewHolder.quantity_TV = convertView.findViewById(R.id.quantity_TV);
            viewHolder.cat_num_TV = convertView.findViewById(R.id.cat_num_TV);
            viewHolder.name_TV = convertView.findViewById(R.id.nameTV);
            viewHolder.price_TV = convertView.findViewById(R.id.Price_TV);
            viewHolder.date_TV = convertView.findViewById(R.id.orderDate_TV);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.customer_TV.setText(listItemMain.getCustomer());
        viewHolder.quantity_TV.setText(listItemMain.getQuantity());
        viewHolder.cat_num_TV.setText(listItemMain.getCatNum());
        viewHolder.name_TV.setText(listItemMain.getName());
        viewHolder.price_TV.setText(String.valueOf(listItemMain.getPrice()));
        viewHolder.date_TV.setText(String.valueOf(listItemMain.getData()));

        return convertView;
    }

    private static class ViewHolder {
        TextView customer_TV;
        TextView cat_num_TV;
        TextView name_TV;
        TextView quantity_TV;
        TextView date_TV;
        TextView price_TV;
    }
}


