package com.lab;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class SettingsActivity extends AppCompatActivity {

    public final static String KEY_MIN_WORD_SIZE = "KEY_MIN_WORD_SIZE";
    public final static String KEY_MAX_WORD_SIZE = "KEY_MAX_WORD_SIZE";
    public final static String KEY_GAME_TIME = "KEY_GAME_TIME";

    private int minws_c = 0;
    private int maxws_c = 0;
    private int gamet_c = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        RadioGroup minws = findViewById(R.id.min_word_size);
        int minws_a[] = getResources().getIntArray(R.array.min_word_size);
        for(int i = 0; i < minws_a.length; i++){
            ((RadioButton) minws.getChildAt(i)).setText(getString(R.string.n_letters, minws_a[i]));
        }

        RadioGroup maxws = findViewById(R.id.max_word_size);
        int maxws_a[] = getResources().getIntArray(R.array.max_word_size);
        for(int i = 0; i < maxws_a.length; i++) {
            ((RadioButton) maxws.getChildAt(i)).setText(getString(R.string.n_letters, maxws_a[i]));
        }

        RadioGroup gamet = findViewById(R.id.game_time);
        int gamet_a[] = getResources().getIntArray(R.array.game_time);
        for(int i = 0; i < gamet_a.length; i++) {
            ((RadioButton) gamet.getChildAt(i)).setText(getString(R.string.n_mins, gamet_a[i]));
        }



        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(KEY_MIN_WORD_SIZE))
                minws_c = savedInstanceState.getInt(KEY_MIN_WORD_SIZE);
            if (savedInstanceState.containsKey(KEY_MAX_WORD_SIZE))
                maxws_c = savedInstanceState.getInt(KEY_MAX_WORD_SIZE);
            if (savedInstanceState.containsKey(KEY_GAME_TIME))
                gamet_c = savedInstanceState.getInt(KEY_GAME_TIME);
        } else {
            Intent data = getIntent();
            if(data.hasExtra(KEY_MIN_WORD_SIZE))
                minws_c = data.getIntExtra(KEY_MIN_WORD_SIZE, 0);
            if(data.hasExtra(KEY_MAX_WORD_SIZE))
                maxws_c = data.getIntExtra(KEY_MAX_WORD_SIZE, 0);
            if(data.hasExtra(KEY_GAME_TIME))
                gamet_c = data.getIntExtra(KEY_GAME_TIME, 0);
        }

        minws.check(minws.getChildAt(minws_c).getId());
        maxws.check(maxws.getChildAt(maxws_c).getId());
        gamet.check(gamet.getChildAt(gamet_c).getId());

        minws.setOnCheckedChangeListener((radioGroup, i) -> {
            minws_c = radioGroup.indexOfChild(findViewById(i));
        });

        maxws.setOnCheckedChangeListener((radioGroup, i) -> {
            maxws_c = radioGroup.indexOfChild(findViewById(i));
        });

        gamet.setOnCheckedChangeListener((radioGroup, i) -> {
            gamet_c = radioGroup.indexOfChild(findViewById(i));
        });

        Button save_btn = findViewById(R.id.save_btn);

        save_btn.setOnClickListener(view -> {
            onDataProcessed();
        });
    }

    public void onDataProcessed() {
        Intent intent = new Intent();
        intent.putExtra(KEY_MIN_WORD_SIZE, minws_c);
        intent.putExtra(KEY_MAX_WORD_SIZE, maxws_c);
        intent.putExtra(KEY_GAME_TIME, gamet_c);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_MIN_WORD_SIZE, minws_c);
        outState.putInt(KEY_MAX_WORD_SIZE, maxws_c);
        outState.putInt(KEY_GAME_TIME, gamet_c);
    }
}