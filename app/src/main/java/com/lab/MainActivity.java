package com.lab;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private static final int RQ_CODE_SETTINGS = 1;

    private int minws_c = 0;
    private int maxws_c = 0;
    private int gamet_c = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState != null){
            if(savedInstanceState.containsKey(SettingsActivity.KEY_MIN_WORD_SIZE))
                minws_c = savedInstanceState.getInt(SettingsActivity.KEY_MIN_WORD_SIZE);
            if(savedInstanceState.containsKey(SettingsActivity.KEY_MAX_WORD_SIZE))
                maxws_c = savedInstanceState.getInt(SettingsActivity.KEY_MAX_WORD_SIZE);
            if(savedInstanceState.containsKey(SettingsActivity.KEY_GAME_TIME))
                gamet_c = savedInstanceState.getInt(SettingsActivity.KEY_GAME_TIME);
        }

        Button start_bt = findViewById(R.id.start_btn);

        start_bt.setOnClickListener(view -> {
            Intent intent = new Intent(this, GameActivity.class);
            putExtras(intent);
            startActivityForResult(intent, RQ_CODE_SETTINGS);
        });

        Button stngs_bt = findViewById(R.id.settings_btn);

        stngs_bt.setOnClickListener(view -> {
            Intent intent = new Intent(this, SettingsActivity.class);
            putExtras(intent);
            startActivityForResult(intent, RQ_CODE_SETTINGS);
        });


    }

    private void putExtras(Intent intent){
        intent.putExtra(SettingsActivity.KEY_MIN_WORD_SIZE, minws_c);
        intent.putExtra(SettingsActivity.KEY_MAX_WORD_SIZE, maxws_c);
        intent.putExtra(SettingsActivity.KEY_GAME_TIME, gamet_c);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SettingsActivity.KEY_MIN_WORD_SIZE, minws_c);
        outState.putInt(SettingsActivity.KEY_MAX_WORD_SIZE, maxws_c);
        outState.putInt(SettingsActivity.KEY_GAME_TIME, gamet_c);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == RQ_CODE_SETTINGS && resultCode == RESULT_OK && data != null) {
                minws_c = data.getIntExtra(SettingsActivity.KEY_MIN_WORD_SIZE, 0);
                maxws_c = data.getIntExtra(SettingsActivity.KEY_MAX_WORD_SIZE, 0);
                gamet_c = data.getIntExtra(SettingsActivity.KEY_GAME_TIME, 0);
            }
        }
}