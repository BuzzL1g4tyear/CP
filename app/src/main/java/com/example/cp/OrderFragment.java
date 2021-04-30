package com.example.cp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class OrderFragment extends Fragment {

    ConnectionHelper connect = new ConnectionHelper();
    Connection connection = connect.getCon();

    public static final String SHOWQ = "SELECT * FROM AndroidOrdersItems WHERE = ?";
    private List<ListItem> arrayListItem;
    PreparedStatement ps;
    ResultSet rs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requireActivity().setTitle(getString(R.string.orders));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        showOrders();
        return inflater.inflate(R.layout.fragment_order, container, false);
    }

    private void showOrders() {
        Thread thread = new Thread(() -> {
            try {
                ListItem listItem;
                ps = connection.prepareStatement(SHOWQ);
                rs = ps.executeQuery(SHOWQ);

                if (rs != null) {
                    int columnCount = rs.getMetaData().getColumnCount();
                    while (rs.next()) {
                        ListItem items = new ListItem();

                        for (int i = columnCount; i <= columnCount; i++) {
//                         todo   items.setGroup(rs.getString(1).trim());
//                            items.setBrand(rs.getString(2).trim());
//                            items.setCatNum(rs.getString(3).trim());
//                            items.setName(rs.getString(4).trim());
//                            items.setPrice(rs.getFloat(5));
//                            items.setAvailable(rs.getString(6).trim());
//                            arrayListItem.add(items);

                        }
                        listItem = items;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }
}