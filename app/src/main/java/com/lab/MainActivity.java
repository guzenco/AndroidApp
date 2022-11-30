package com.lab;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.lab.storage.StorageTaskManagerFragment;
import com.lab.ui.main.MainFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, MainFragment.newInstance())
                    .add(new StorageTaskManagerFragment(), StorageTaskManagerFragment.TAG)
                    .commitNow();
        }
    }
}