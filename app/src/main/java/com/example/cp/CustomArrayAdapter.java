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

public class CustomArrayAdapter extends ArrayAdapter<ListItem> {
    private final LayoutInflater inflater;
    private List<ListItem> listItem = new ArrayList<>();
    private List<ListItem> listItemCopy = new ArrayList<>();

    public CustomArrayAdapter(@NonNull Context context, int resource, List<ListItem> list, LayoutInflater inflater) {
        super(context, resource, list);

        listItemCopy.addAll(list);

        this.inflater = inflater;
        this.listItem = list;
    }

    public void filter(@NotNull String text) {
        listItem.clear();
        if (text.isEmpty()) {
            listItem.addAll(listItemCopy);
        } else {
            text = text.toLowerCase();
            for (ListItem item : listItemCopy) {
                if (item.Name.toLowerCase().contains(text) || item.Price.toLowerCase().contains(text)
                        || item.Quantity.toLowerCase().contains(text)) {
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
            convertView = inflater.inflate(R.layout.item_list, null, false);
            viewHolder = new ViewHolder();
            viewHolder.data1 = convertView.findViewById(R.id.nameTV);
            viewHolder.data2 = convertView.findViewById(R.id.quantity_TV);
            viewHolder.data3 = convertView.findViewById(R.id.Price_TV);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.data1.setText(listItemMain.getName());
        viewHolder.data2.setText(listItemMain.getQuantity());
        viewHolder.data3.setText(listItemMain.getPrice());

        return convertView;
    }

    private class ViewHolder {
        TextView data1;
        TextView data2;
        TextView data3;
    }

}


