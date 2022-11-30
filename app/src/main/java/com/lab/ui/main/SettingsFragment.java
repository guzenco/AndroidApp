package com.lab.ui.main;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.lab.R;
import com.lab.storage.Storage;
import com.lab.storage.StorageTaskManagerFragment;

public class SettingsFragment extends Fragment {

    public final static String TAG = SettingsFragment.class.toString();
    public final static String TASK_LOAD_SETTINGS = TAG + "_TASK_LOAD_SETTINGS";
    public final static String TASK_SAVE_SETTINGS = TAG + "_TASK_SAVE_SETTINGS";

    private RadioGroup minws;
    private RadioGroup maxws;
    private RadioGroup gamet;

    private int minws_c = 0;
    private int maxws_c = 0;
    private int gamet_c = 0;

    private boolean cashed = false;

    private StorageTaskManagerFragment storageTM;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        storageTM = (StorageTaskManagerFragment) getFragmentManager().findFragmentByTag(StorageTaskManagerFragment.TAG);
        restoreData(savedInstanceState);
        loadData();
        minws = v.findViewById(R.id.min_word_size);
        int minws_a[] = getResources().getIntArray(R.array.min_word_size);
        for (int i = 0; i < minws_a.length; i++) {
            ((RadioButton) minws.getChildAt(i)).setText(getString(R.string.n_letters, minws_a[i]));
        }


        maxws = v.findViewById(R.id.max_word_size);
        int maxws_a[] = getResources().getIntArray(R.array.max_word_size);
        for (int i = 0; i < maxws_a.length; i++) {
            ((RadioButton) maxws.getChildAt(i)).setText(getString(R.string.n_letters, maxws_a[i]));
        }


        gamet = v.findViewById(R.id.game_time);
        int gamet_a[] = getResources().getIntArray(R.array.game_time);
        for (int i = 0; i < gamet_a.length; i++) {
            ((RadioButton) gamet.getChildAt(i)).setText(getString(R.string.n_mins, gamet_a[i]));
        }

        minws.setOnCheckedChangeListener((radioGroup, i) -> {
            minws_c = radioGroup.indexOfChild(v.findViewById(i));
        });

        maxws.setOnCheckedChangeListener((radioGroup, i) -> {
            maxws_c = radioGroup.indexOfChild(v.findViewById(i));
        });

        gamet.setOnCheckedChangeListener((radioGroup, i) -> {
            gamet_c = radioGroup.indexOfChild(v.findViewById(i));
        });

        Button save_btn = v.findViewById(R.id.save_btn);

        save_btn.setOnClickListener(view -> {
            storageTM.newTask(
                    TASK_SAVE_SETTINGS,
                    (storage) -> {
                        if (storage != null) {
                            storage.putInt(Storage.GameSettings.KEY_MIN_WORD_SIZE, minws_c);
                            storage.putInt(Storage.GameSettings.KEY_MAX_WORD_SIZE, maxws_c);
                            storage.putInt(Storage.GameSettings.KEY_GAME_TIME, gamet_c);
                            storage.saveStorageData();
                        }
                    },
                    () -> {
                        getActivity().onBackPressed();
                    }
            );

        });

        if (cashed) {
            minws.check(minws.getChildAt(minws_c).getId());
            maxws.check(maxws.getChildAt(maxws_c).getId());
            gamet.check(gamet.getChildAt(gamet_c).getId());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void restoreData(@Nullable Bundle savedInstanceState){
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(Storage.GameSettings.KEY_MIN_WORD_SIZE))
                minws_c = savedInstanceState.getInt(Storage.GameSettings.KEY_MIN_WORD_SIZE);
            if (savedInstanceState.containsKey(Storage.GameSettings.KEY_MAX_WORD_SIZE))
                maxws_c = savedInstanceState.getInt(Storage.GameSettings.KEY_MAX_WORD_SIZE);
            if (savedInstanceState.containsKey(Storage.GameSettings.KEY_GAME_TIME))
                gamet_c = savedInstanceState.getInt(Storage.GameSettings.KEY_GAME_TIME);
            cashed = true;
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(Storage.GameSettings.KEY_MIN_WORD_SIZE, minws_c);
        outState.putInt(Storage.GameSettings.KEY_MAX_WORD_SIZE, maxws_c);
        outState.putInt(Storage.GameSettings.KEY_GAME_TIME, gamet_c);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public void loadData() {

        if (!cashed) {
            cashed = true;
            storageTM.newTask(
                    TASK_LOAD_SETTINGS,
                    (storage) -> {
                        if (storage != null) {
                            minws_c = storage.getInt(Storage.GameSettings.KEY_MIN_WORD_SIZE, 0);
                            maxws_c = storage.getInt(Storage.GameSettings.KEY_MAX_WORD_SIZE, 0);
                            gamet_c = storage.getInt(Storage.GameSettings.KEY_GAME_TIME, 0);
                        }
                    },
                    () ->{
                        minws.check(minws.getChildAt(minws_c).getId());
                        maxws.check(maxws.getChildAt(maxws_c).getId());
                        gamet.check(gamet.getChildAt(gamet_c).getId());
                    }
            );
        }
    }
}