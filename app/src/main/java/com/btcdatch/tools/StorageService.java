package com.btcdatch.tools;

import android.bluetooth.BluetoothCodecConfig;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

public class StorageService {

    private SharedPreferences.Editor editor;
    private SharedPreferences sprefs;
    private Gson gson;

    public StorageService(Context context) {
        sprefs = PreferenceManager.getDefaultSharedPreferences(context);
        gson = new Gson();
    }

    public BluetoothCodecConfig getBluetoothCodecConfig(String key){
        String json = sprefs.getString(key, null);
        BluetoothCodecConfig config = null;
        if(sprefs.contains(key + Configs.CODEC)) {
            config = BluetoothA2dpTool.tool.newBluetoothCodecConfig(
                    sprefs.getInt(key + Configs.CODEC, 0),
                    BluetoothCodecConfig.CODEC_PRIORITY_HIGHEST,
                    sprefs.getInt(key + Configs.SAMPLE_RATE, 0),
                    sprefs.getInt(key + Configs.BITS_PER_SAMPLE, 0),
                    sprefs.getInt(key + Configs.CHANNEL_MODE, 0)
            );
        }

        return config;
    }

    public StorageService putBluetoothCodecConfig(String key, BluetoothCodecConfig config){
        if(editor == null)
            editor = sprefs.edit();
        editor.putInt(key + Configs.CODEC, config.getCodecType());
        editor.putInt(key + Configs.SAMPLE_RATE, config.getSampleRate());
        editor.putInt(key + Configs.BITS_PER_SAMPLE, config.getBitsPerSample());
        editor.putInt(key + Configs.CHANNEL_MODE, config.getChannelMode());
        return this;
    }

    public boolean save(){
        if(editor == null)
            return false;
        editor.apply();
        editor = null;
        return true;
    }

    private static class Configs{
        private final static String CODEC = ".CODEC";
        private final static String SAMPLE_RATE = ".SAMPLE_RATE";
        private final static String BITS_PER_SAMPLE = ".BITS_PER_SAMPLE";
        private final static String CHANNEL_MODE = ".CHANNEL_MODE";
        private final static String SPECIFIC = ".SPECIFIC";
    }
}




