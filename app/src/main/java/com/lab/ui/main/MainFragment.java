package com.lab.ui.main;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.lab.R;
import com.lab.model.Meteor;

public class MainFragment extends Fragment {

    private MainViewModel mViewModel;
    private ListView list;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mViewModel.setContext(this.getContext());
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
        list = v.findViewById(R.id.list);
        mViewModel.getMeteors().observe(getViewLifecycleOwner(), this::onMeteorsChanged);
    }

    private void onMeteorsChanged(Meteor meteors[]) {
        if(meteors == null) return;
        ArrayAdapter<Meteor> adapter = new ArrayAdapter<Meteor>(this.getContext(), android.R.layout.simple_list_item_1, meteors);
        list.setAdapter(adapter);
        list.setOnItemClickListener((adapterView, view, i, l) ->
        {
            getFragmentManager().beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.container, MeteorInfoFragment.newInstance(meteors[i]))
                    .commit();
        });
    }
}