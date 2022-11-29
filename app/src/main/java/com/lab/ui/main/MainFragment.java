package com.lab.ui.main;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.lab.R;

public class MainFragment extends Fragment {

    private MainViewModel mViewModel;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        Button start_bt = v.findViewById(R.id.start_btn);

        start_bt.setOnClickListener(view -> {
            getFragmentManager().beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.container, GameFragment.newInstance())
                    .commit();
        });

        Button stngs_bt = v.findViewById(R.id.settings_btn);

        stngs_bt.setOnClickListener(view -> {
            getFragmentManager().beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.container, SettingsFragment.newInstance())
                    .commit();
        });
    }
}