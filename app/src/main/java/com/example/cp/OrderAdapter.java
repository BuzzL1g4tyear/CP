package com.example.cp;

import android.annotation.SuppressLint;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    private List<ListItem> listItem;

    public OrderAdapter(List<ListItem> list) {
        listItem = list;
    }

    @NonNull
    @Override
    public OrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_adapter, parent, false);
        return new OrderAdapter.ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull OrderAdapter.ViewHolder holder, int position) {
        ListItem listItemMain = listItem.get(position);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat newDateFormat =
                new SimpleDateFormat("dd.MM.yyyy");
        String result = newDateFormat.format(listItemMain.getDate());
        holder.orStatus.setText(listItemMain.getOrStatus().trim());
        holder.orNum.setText(listItemMain.getNum().trim());
        holder.oDate.setText(result);
        holder.oCatNum.setText(listItemMain.getCatNum().trim());
        holder.oName.setText(listItemMain.getName().trim());
        holder.oQuant.setText(String.valueOf(listItemMain.getQuantity()));
        holder.oStatus.setText(listItemMain.getStatus().trim());
    }

    @Override
    public int getItemCount() {
        return listItem.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView oDate;
        TextView oCatNum;
        TextView oName;
        TextView oStatus;
        TextView oQuant;
        TextView orNum;
        TextView orStatus;

        public ViewHolder(View view) {
            super(view);

            orNum = view.findViewById(R.id.or_num);
            orStatus = view.findViewById(R.id.or_status);
            oDate = view.findViewById(R.id.o_date);
            oCatNum = view.findViewById(R.id.o_catNum);
            oName = view.findViewById(R.id.o_name);
            oQuant = view.findViewById(R.id.o_quant);
            oStatus = view.findViewById(R.id.o_status);
        }
    }
}
