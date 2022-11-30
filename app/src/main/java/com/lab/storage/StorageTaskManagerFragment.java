package com.lab.storage;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class StorageTaskManagerFragment extends Fragment {

    public static final String TAG = StorageTaskManagerFragment.class.getSimpleName();
    private HashMap<String, Future<?>> activeTasks;
    private ExecutorService executorService;
    private Handler handler = new Handler(Looper.getMainLooper());
    public Storage storage;



    public void newTask(String task_key, StorageTask task, Runnable afterTask){
        if(activeTasks.containsKey(task_key))
            activeTasks.get(task_key).cancel(true);
        activeTasks.put(task_key,
            executorService.submit(() -> {
                if(task != null)
                    task.run(storage);
                handler.post(() -> {
                    activeTasks.remove(task_key);
                    if(afterTask != null)
                        afterTask.run();
                });
            })
        );
    }

    public boolean hasTask(String task){
        return activeTasks.containsKey(task);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        executorService = Executors.newSingleThreadExecutor();
        storage = new Storage(this.getContext());
        activeTasks = new HashMap<>();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        executorService.shutdown();
    }

    public interface StorageTask{
        public void run(Storage storage);
    }
}