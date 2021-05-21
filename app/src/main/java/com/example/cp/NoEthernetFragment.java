package com.example.cp;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.snackbar.Snackbar;

import static com.example.cp.shopActivity.hasConnection;

public class NoEthernetFragment extends Fragment {

    private final Handler mHandler = new Handler();

    private ImageView imageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_no_ethernet, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        imageView = requireActivity().findViewById(R.id.imageView);
        Toolbar toolbar_no_ethernet = requireActivity().findViewById(R.id.toolbarNoEthernet);
        toolbar_no_ethernet.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        toolbar_no_ethernet.setTitle(getString(R.string.no_ethernet));
        ((shopActivity) requireActivity()).setSupportActionBar(toolbar_no_ethernet);
        mHandler.postDelayed(checkEthernetCon, 5000);

        imageView.setOnClickListener(v -> {
            Snack(getString(R.string.reconnection_attempt));
            if (hasConnection(requireActivity())) {

            }
        });
    }

    Runnable checkEthernetCon = () -> hasConnection(requireContext());

    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeCallbacks(checkEthernetCon);
    }

    public void Snack(String mes) {
        View viewSnack = requireActivity().findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar
                .make(viewSnack, mes, Snackbar.LENGTH_LONG);
        snackbar.setTextColor(getResources().getColor(R.color.colorWhite));
        snackbar.show();
    }
}