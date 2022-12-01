package com.lab.ui.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.lab.R;
import com.lab.model.Meteor;

import java.util.ArrayList;

public class MeteorInfoFragment extends Fragment {

    private static final String METEOR_NAME = "METEOR_NAME";
    private static final String METEOR_YEAR= "METEOR_YEAR";
    private static final String METEOR_RECCLASS = "METEOR_RECCLASS";
    private static final String METEOR_MASS= "METEOR_MASS";
    private static final String METEOR_FALL = "METEOR_FALL";

    public MeteorInfoFragment() {
        // Required empty public constructor
    }

    public static MeteorInfoFragment newInstance(Meteor meteor) {
        MeteorInfoFragment fragment = new MeteorInfoFragment();
        Bundle args = new Bundle();
        args.putString(METEOR_NAME, meteor.getName());
        args.putString(METEOR_YEAR, meteor.getYear().substring(0,4));
        args.putString(METEOR_RECCLASS, meteor.getRecclass());
        args.putFloat(METEOR_MASS, meteor.getMass());
        args.putString(METEOR_FALL, meteor.getFall());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            TextView name = view.findViewById(R.id.name);
            name.setText(getArguments().getString(METEOR_NAME));
            ListView list = view.findViewById(R.id.list);
            ArrayList<String> aList = new ArrayList<>();
            aList.add(getString(R.string.meteor_year, getArguments().getString(METEOR_YEAR)));
            aList.add(getString(R.string.meteor_reclass, getArguments().getString(METEOR_RECCLASS)));
            aList.add(getString(R.string.meteor_mass, getArguments().getFloat(METEOR_MASS)));
            aList.add(getString(R.string.meteor_fall, getArguments().getString(METEOR_FALL)));
            //aList.add();
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_list_item_1, aList);
            list.setAdapter(adapter);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }
}