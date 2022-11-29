package com.lab.services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class StorageService extends Service {

    private StorageServiceBinder binder = new StorageServiceBinder();
    private SharedPreferences.Editor editor;
    SharedPreferences sprefs;

    @Override
    public void onCreate() {
        sprefs = PreferenceManager.getDefaultSharedPreferences(this);
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public int getInt(String key, int d){
        return sprefs.getInt(key, d);
    }

    public void putInt(String key, int v){
        if(editor == null)
            editor = sprefs.edit();
        editor.putInt(key, v);
    }

    public String[] getStringArray(String key){
        Set<String> set = sprefs.getStringSet(key, new HashSet<>());
        String array[] = new String[set.size()];
        set.toArray(array);
        return array;
    }

    public void putStringArray(String key, String array[]){
        if(editor == null)
            editor = sprefs.edit();
        editor.putStringSet(key, new HashSet<>(Arrays.asList(array)));
    }

    public boolean saveStorageData(){
        if(editor == null)
            return false;
        editor.apply();
        editor = null;
        return true;
    }

    public SharedPreferences.Editor getEditor(){
        return sprefs.edit();
    }

    public class StorageServiceBinder extends Binder {
        public StorageService getService(){
            return StorageService.this;
        }
    }

    public final static class GameSettings{
        public final static String KEY_MIN_WORD_SIZE = "KEY_MIN_WORD_SIZE";
        public final static String KEY_MAX_WORD_SIZE = "KEY_MAX_WORD_SIZE";
        public final static String KEY_GAME_TIME = "KEY_GAME_TIME";
        public final static String KEY_WORDS = "KEY_WORDS";
    }
}