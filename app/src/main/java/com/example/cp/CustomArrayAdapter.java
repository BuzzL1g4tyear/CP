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
                if (item.Name.toLowerCase().contains(text) || item.CatNum.toLowerCase().contains(text)
                        || item.Brand.toLowerCase().contains(text) || item.Group.toLowerCase().contains(text)
                        || item.Available.toLowerCase().contains(text) || String.valueOf(item.Price).toLowerCase().contains(text) ) {
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
            viewHolder.group_TV = convertView.findViewById(R.id.group_TV);
            viewHolder.brand_TV = convertView.findViewById(R.id.brand_TV);
            viewHolder.cat_num_TV = convertView.findViewById(R.id.cat_num_TV);
            viewHolder.name_TV = convertView.findViewById(R.id.nameTV);
            viewHolder.price_TV = convertView.findViewById(R.id.Price_TV);
            viewHolder.available_TV = convertView.findViewById(R.id.available_TV);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.group_TV.setText(listItemMain.getGroup());
        viewHolder.brand_TV.setText(listItemMain.getBrand());
        viewHolder.cat_num_TV.setText(listItemMain.getCatNum());
        viewHolder.name_TV.setText(listItemMain.getName());
        viewHolder.price_TV.setText(String.valueOf(listItemMain.getPrice()));
        viewHolder.available_TV.setText(listItemMain.getAvailable());

        return convertView;
    }

    private static class ViewHolder {
        TextView group_TV;
        TextView brand_TV;
        TextView cat_num_TV;
        TextView name_TV;
        TextView price_TV;
        TextView available_TV;
    }

}


